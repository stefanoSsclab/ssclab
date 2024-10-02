package org.ssclab.pl.milp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.ssclab.pl.milp.ObjectiveFunction.TARGET_FO;

import jakarta.json.*;

public class JsonSolution {
	JsonObject model;
	SolutionDetail[] option;
	SolutionType typeSolution;

	JsonSolution( Meta meta,TARGET_FO target,SolutionType typeSolution,Solution[] solution, SolutionDetail... option) {
		// TODO Auto-generated constructor stub
		String target_fo="min";
		if(target==TARGET_FO.MAX) target_fo="max";
		this.typeSolution=typeSolution;
		ZonedDateTime now = ZonedDateTime.now();
	    String executionTimestamp =  now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		JsonObjectBuilder metaJsonBuilder= Json.createObjectBuilder()
				                           .add("executionTimestamp", executionTimestamp);
		String title=(String)meta.getProperty("title");
		if(title==null) metaJsonBuilder.add("problemTitle", JsonValue.NULL);
		else metaJsonBuilder.add("problemTitle", title);
	
		if(solution.length==2) { 
			metaJsonBuilder
			.add("problemDescription","MILP problem")
			.add("threads", (Integer)meta.getProperty("threads"))
			.add("numberOfSimplices", (Integer)meta.getProperty("numberOfSimplices"))
			.add("optimizationDuration",meta.getProperty("optimizationDuration").toString());
		
		}
		else { 
			metaJsonBuilder
			.add("problemDescription","LP problem")
			.add("threads", (Integer)meta.getProperty("threads"))
			.add("iterations", (Long)meta.getProperty("iterationsLP"))
			.add("optimizationDuration", meta.getProperty("optimizationDuration").toString())
			.add("averageError", (Double)meta.getProperty("averageError"))
			.add("maxError", (Double)meta.getProperty("maxError"));
		}
		
		JsonObjectBuilder jsonObjectBuild= Json.createObjectBuilder();
		if(Arrays.asList(option).contains(SolutionDetail.INCLUDE_META)) {
			jsonObjectBuild.add("meta", metaJsonBuilder);
		}
		
		//System.out.println("hh:"+solution[0]);
		this.option = option;
		//SolutionType typeSolution = solution[0].getTypeSolution();
		String statusolution = getStringTypeSolution(typeSolution);
		
		jsonObjectBuild.add("objectiveType", target_fo)
					   .add("status", statusolution)
					   .add("solution", getSolution(solution[0]));
		if(solution.length==2 && Arrays.asList(option).contains(SolutionDetail.INCLUDE_RELAXED)) {
			jsonObjectBuild.add("relaxedSolution",  getSolution(solution[1]));
		}
		
		model =jsonObjectBuild.build();
	}

	public JsonObject getJsonObject() {
		return model;
	}

	private String getStringTypeSolution(SolutionType typeSolution) {
		switch (typeSolution) {
		case FEASIBLE:
			return "feasible";
		case VUOTUM:
			return "infeasible";
		case ILLIMITATUM:
			return "unbounded";
		case OPTIMUM:
			return "optimal";
		case MAX_ITERATIUM:
			return "max iterations";
		case MAX_NUM_SIMPLEX:
			return "max Simplexes";

		default:
			System.out.println("Unknown solution status.");
			return "Unknown";
		}
	}

	private JsonObjectBuilder getSolution(Solution solution) {
		if(solution==null ) return Json.createObjectBuilder();
		JsonObjectBuilder jsonObject = Json.createObjectBuilder();
		if (this.typeSolution == SolutionType.OPTIMAL || this.typeSolution == SolutionType.FEASIBLE) {

			Double lower, upper;
			JsonObjectBuilder solutionVariables = Json.createObjectBuilder();
			for (Variable var : solution.getVariables()) {
				lower = var.getLower();
				upper = var.getUpper();
				JsonObjectBuilder variable = Json.createObjectBuilder()
						.add("value", var.getValue());
						//.add("originalType",  var.getType().toString());
				if (Arrays.asList(option).contains(SolutionDetail.INCLUDE_TYPEVAR))
					variable.add("originalType",  var.getType().toString());
				
				if (Arrays.asList(option).contains(SolutionDetail.INCLUDE_BOUNDS)) {
					if (Double.isInfinite(lower)) variable.add("lower", JsonValue.NULL);
					else variable.add("lower", lower);
					if (Double.isInfinite(upper)) variable.add("upper", JsonValue.NULL);
					else variable.add("upper", upper);
				}
				solutionVariables.add(var.getName(), variable);
			}
			jsonObject.add("objectiveValue", solution.getValue());
			jsonObject.add("variables", solutionVariables);
		
			if (Arrays.asList(option).contains(SolutionDetail.INCLUDE_CONSTRAINT)) {
				JsonArrayBuilder solutionConstraints = Json.createArrayBuilder();
				for (SolutionConstraint cons : solution.getSolutionConstraint()) {
					solutionConstraints.add(Json.createObjectBuilder() 
						  .add("name", cons.getName())
						  .add("lhs", cons.getValue())
						  .add("rel", cons.getRel().toString())
						  .add("rhs", cons.getRhs()));
				}
				jsonObject.add("constraints", solutionConstraints);
			}
		} 
		else jsonObject = Json.createObjectBuilder();;

		return jsonObject;
	}
	
	
	public JsonSolution saveToFile(String path) throws IOException {
		JsonWriter jsonWriter = Json.createWriter(new FileWriter(path));
		jsonWriter.writeObject(this.model);
		jsonWriter.close();
		return this;
	}
	
	
	public String toString() {
		StringWriter stWriter = new StringWriter();
		JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.writeObject(this.model);
		jsonWriter.close();
		return stWriter.toString();
	}
}
