package org.ssclab.pl.milp.scanjson;

import static javax.json.stream.JsonParser.Event.KEY_NAME;
import static javax.json.stream.JsonParser.Event.START_ARRAY;
import static javax.json.stream.JsonParser.Event.START_OBJECT;
import static javax.json.stream.JsonParser.Event.VALUE_STRING;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;

import org.ssclab.i18n.RB;
import org.ssclab.pl.milp.GoalType;
import org.ssclab.pl.milp.LPException;
import org.ssclab.pl.milp.LinearObjectiveFunction;
import org.ssclab.pl.milp.ParseException;

public class ScanSintaxJson {
	private ArrayList<String> listNomiVar;
	//private ArrayList<String> listNomiVarFo;
	private ArrayList<String> listNomiVarWithType;
	private JsonObject fo;
	private boolean existObjective = false, existConstraints = false;
	private double[] list_cj;

	public ScanSintaxJson(BufferedReader br) throws ParseException {
		listNomiVar=new ArrayList<String>();
		//listNomiVarFo=new ArrayList<String>();
		listNomiVarWithType=new ArrayList<String>();
		try {
			check(br);
			Collections.sort(listNomiVar);
			check2();
			this.list_cj=new double[listNomiVar.size()];
			buildFoCoeff();
		} 
		catch (javax.json.stream.JsonParsingException jpe) {
			throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg2") + jpe.getMessage());
		}
	}

	private void check(BufferedReader br) throws ParseException {

		JsonParser parser = Json.createParser(br);
		JsonParser.Event event = null;
		
		if (parser.hasNext())	event = parser.next();
		
		while (parser.hasNext()) {
			event = parser.next();
			if (event == KEY_NAME && parser.getString().toUpperCase().equals("OBJECTIVE")) {
				if (parser.next() == START_OBJECT) {
					JsonValue fov = parser.getValue();
					if (fov.getValueType() == JsonValue.ValueType.OBJECT) {
						this.fo = fov.asJsonObject();
						existObjective = true;
						if (!this.fo.containsKey("type") || !this.fo.containsKey("coefficients")	) {
							throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg3"));
						}
						String typeValue=fo.getString("type");
						if(!typeValue.equalsIgnoreCase("MAX") && !typeValue.equalsIgnoreCase("MIN")) {
							throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg4"));
							
						}
					}
				}
			} 
			else if (event == KEY_NAME && parser.getString().toUpperCase().equals("CONSTRAINTS")) {
				if (parser.next() == START_ARRAY) {
					JsonObject jsonObject=null, coef=null;
					cons: while (parser.hasNext()) {
						if (parser.next() == START_OBJECT) {
							JsonValue jval = parser.getValue();
							if (jval.getValueType() == JsonValue.ValueType.OBJECT) {
								jsonObject = jval.asJsonObject();
								// Accedi ai valori delle chiavi
								if (!jsonObject.containsKey("coefficients") || 
									!jsonObject.containsKey("relation")	|| 
									!jsonObject.containsKey("rhs")) {
									throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg5") + jval);
								}
								coef = jsonObject.getJsonObject("coefficients");
								String rel = jsonObject.getString("relation");								
								//double rhs = jsonObject.getJsonNumber("rhs").doubleValue();
								if(!rel.equals("eq") && !rel.equals("le") && !rel.equals("ge") ) {
									throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg6") + jval);
								}
								for (String key : coef.keySet()) {
									if(!listNomiVar.contains(key.toUpperCase())) listNomiVar.add(key.toUpperCase());
								}
								existConstraints = true;
							}
						} 
						else {
							break cons;
						}
					}
				}
			} 
			else if (event == KEY_NAME && parser.getString().toUpperCase().equals("BOUNDS")) {
				if (parser.next() == START_OBJECT) {
					JsonValue jval=null;
					JsonObject jsonObject=null;
					cc: while (parser.hasNext()) {
						if ((event = parser.next()) == KEY_NAME) {
							String key=parser.getString();
							if(!listNomiVar.contains(key.toUpperCase())) listNomiVar.add(key.toUpperCase());
							if ((event = parser.next()) == START_OBJECT) {
								jval = parser.getValue();
								if (jval.getValueType() == JsonValue.ValueType.OBJECT) {
									jsonObject = jval.asJsonObject();
									for (String kej : jsonObject.keySet()) {
										if(!kej.equals("upper") && !kej.equals("lower") ) {
											throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg7")  + jval);
										}
									}
								}	
							}
						} 
						else {
							break cc;
						}
					}
				}
			} 
			else if (event == KEY_NAME && parser.getString().toUpperCase().equals("VARIABLES")) {
				if (parser.next() == START_OBJECT) {
					vr: while (parser.hasNext()) {
						if ((event = parser.next()) == KEY_NAME) {
							String varName=parser.getString();
							if(!listNomiVarWithType.contains(varName.toUpperCase())) listNomiVarWithType.add(varName.toUpperCase());
							if ((event = parser.next()) == VALUE_STRING) {
								String type=parser.getString();
								if(!type.equals("integer")  && 
								   !type.equals("binary")   && 
								   !type.equals("semicont") &&
								   !type.equals("integer-semicont") ) {
									throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg8")+ type);
								}
							}
						} 
						else {
							break vr;
						}
					}
				}
			}
		}
		parser.close();
	}
	
	private void check2() throws ParseException {
		
		if(!existObjective) throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg9"));
		if(!existConstraints) throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg10"));
		
		if(listNomiVar.isEmpty()) {
			throw new ParseException(RB.getString("org.ssclab.pl.milp.json.msg11"));
		}
		else {
			for(String nomeVar:listNomiVarWithType) {
				if(!listNomiVar.contains(nomeVar)) {
					
					throw new ParseException(RB.format("org.ssclab.pl.milp.json.msg12",nomeVar));
				}
			}
		}
	}
	
	private void buildFoCoeff() throws ParseException {
		
		JsonObject coeff=this.fo.getJsonObject("coefficients");
		for(String varNameFo:coeff.keySet()) {
			int index=listNomiVar.indexOf(varNameFo.toUpperCase());
			double cj=coeff.getJsonNumber(varNameFo).doubleValue();
			if(index==-1) throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.ScanFoFromLine.msg1")+" ["+varNameFo+"]");
			list_cj[index]=cj+list_cj[index];	
		}
	}

	public ArrayList<String> getListNomiVar() {
		return listNomiVar;
	}

	public LinearObjectiveFunction getFo() throws LPException {
		String target_fo= fo.getString("type");
		GoalType goal=GoalType.MIN;
		if(target_fo.equalsIgnoreCase("MAX")) goal=GoalType.MAX;
		return  new LinearObjectiveFunction(this.list_cj, goal);
	}
}
