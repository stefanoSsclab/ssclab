package org.ssclab.pl.milp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;

import org.ssclab.pl.milp.ObjectiveFunction.TARGET_FO;
import org.ssclab.pl.milp.Variable.TYPE_VAR;

import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;

/**
 * This class represents the solution of a Linear Programming (LP) problem in JSON format.
 * The generated JSON follows this structure:
 * 
 * <pre>
 * {
 *     "meta": {
 *         // Information about the processing (duration, number of threads, etc.)
 *     },
 *     "objectiveType": "min",
 *     "status": "optimal",
 *     "solution": {
 *         "objectiveValue": 1244.0,
 *         "variables": {
 *             // Variable values
 *         }
 *     },
 *     "relaxedSolution": {
 *         "objectiveValue": 1244.0,
 *         "variables": {
 *             // Relaxed solution variable values
 *         }
 *     }
 * }
 * </pre>
 */

public class JsonSolution {
	private JsonObject model;
	private SolutionDetail[] option;
	private SolutionType typeSolution;
	private boolean isFormatted;
	
	 /**
     * Constructor that initializes the JSON representation of the solution.
     * 
     * @param meta Information related to the processing, such as duration, number of threads, etc.
     * @param target The type of objective function (minimization or maximization).
     * @param typeSolution Indicates the type of solution (optimal, feasible, infeasible, unbounded, etc.).
     * @param solution An array containing the solution values, including the objective function variables and constraints.
     *                 If the problem is a Mixed-Integer Linear Program (MILP), the array includes both the relaxed and full solutions.
     * @param option A variable number of SolutionDetail options to include additional information in the JSON,
     *               such as bounds, constraints, and meta data.
     */

	JsonSolution( Meta meta,TARGET_FO target,SolutionType typeSolution,Solution[] solution, SolutionDetail... option) {
		// TODO Auto-generated constructor stub
		String target_fo="min";
		this.isFormatted=false;

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
			.add("averageError",meta.getProperty("averageError")!=null ? (Double)meta.getProperty("averageError") :0)
			.add("maxError",meta.getProperty("maxError")!=null ? (Double)meta.getProperty("maxError"):0);
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
					   .add("solution", getSolution(false,solution[0]));
		if(solution.length==2 && Arrays.asList(option).contains(SolutionDetail.INCLUDE_RELAXED)) {
			jsonObjectBuild.add("relaxedSolution",  getSolution(true,solution[1]));
		}
		
		model =jsonObjectBuild.build();
	}

	
	/**
     * Returns the JSON object built using the jakarta.json library.
     * 
     * @return JsonObject representing the solution.
     */
	
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

	private JsonObjectBuilder getSolution(boolean existRelaxed,Solution solution) {
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
				
				
				if (existRelaxed && Arrays.asList(option).contains(SolutionDetail.INCLUDE_TYPEVAR))
					variable.add("type",TYPE_VAR.REAL.toString());
				else if (Arrays.asList(option).contains(SolutionDetail.INCLUDE_TYPEVAR))
					variable.add("type",  var.getType().toString());
				
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
	
	/**
     * Saves the JSON object to an external file.
     * 
     * @param path The file path where the JSON should be saved.
     * @return The current JsonSolution instance.
     * @throws IOException if an I/O error occurs during the writing process.
     */
	
	public JsonSolution saveToFile(String path) throws IOException {
		JsonWriter jsonWriter = null;
		if(this.isFormatted) {
			HashMap<String, Object> config = new HashMap<>();
			config.put(JsonGenerator.PRETTY_PRINTING, true);
			JsonWriterFactory writerFactory = Json.createWriterFactory(config);
			jsonWriter = writerFactory.createWriter(new FileWriter(path));
		}
		else {
			jsonWriter = Json.createWriter(new FileWriter(path));
		}
		jsonWriter.writeObject(this.model);
		jsonWriter.close();
		return this;
	}
	
	/**
     * Returns the JSON representation of the solution as a String.
     * 
     * @return A String representation of the JSON solution.
     */
    @Override
	
	
	public String toString() {
    	
    	StringWriter stWriter = new StringWriter();
    	JsonWriter jsonWriter =null;
		if (this.isFormatted) {
			HashMap<String, Object> config = new HashMap<>();
			config.put(JsonGenerator.PRETTY_PRINTING, true);
			JsonWriterFactory writerFactory = Json.createWriterFactory(config);
			jsonWriter = writerFactory.createWriter(stWriter);
		} 
		else {
			jsonWriter = Json.createWriter(stWriter);
		}
		jsonWriter.writeObject(this.model);
		jsonWriter.close();
		return stWriter.toString();
	}
    
	/**
     * Formatted the Json String if save into String or File.
     * 
     * @param isFormatted true if formatted
     * @return The current JsonSolution instance.
    
     */
    public JsonSolution formatted(boolean isFormatted) {
    	this.isFormatted=isFormatted;
    	return this;
    }
}
