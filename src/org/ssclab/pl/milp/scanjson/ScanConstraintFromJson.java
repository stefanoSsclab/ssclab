package org.ssclab.pl.milp.scanjson;

import static jakarta.json.stream.JsonParser.Event.*;


import java.io.BufferedReader;
import java.util.ArrayList;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;

import org.ssclab.pl.milp.ConsType;
import org.ssclab.pl.milp.Constraint;
import org.ssclab.pl.milp.LPException;
import org.ssclab.pl.milp.ListConstraints;
import org.ssclab.pl.milp.ParseException;

public class ScanConstraintFromJson {
	private ListConstraints constraints;
	private ArrayList<String> nomi_var;
	private int dimension;

	public ScanConstraintFromJson(BufferedReader br,ArrayList<String> nomi_var) throws ParseException, LPException {
		// TODO Auto-generated constructor stub
		this.nomi_var=nomi_var;
		this.dimension=nomi_var.size();
		constraints=new ListConstraints();
		check(br);
	}
	
	public  ArrayList<Constraint> getConstraints() {
		return constraints.getListConstraint();
	}


	private void check(BufferedReader br) throws ParseException, LPException {

		JsonParser parser = Json.createParser(br);
		JsonParser.Event event = null;
		if (parser.hasNext())	event = parser.next();
		while (parser.hasNext()) {
			event = parser.next();
			if (event == KEY_NAME && parser.getString().toUpperCase().equals("CONSTRAINTS")) {
				if (parser.next() == START_ARRAY) {
					JsonObject jsonObject=null,coef=null;
					JsonValue jval=null;
					cons: while (parser.hasNext()) {
						if (parser.next() == START_OBJECT) {
							jval = parser.getValue();
							if (jval.getValueType() == JsonValue.ValueType.OBJECT) {
								double[] Ai=new double[dimension];
								jsonObject = jval.asJsonObject();
								// Accedi ai valori delle chiavi
								coef = jsonObject.getJsonObject("coefficients");
								String rel = jsonObject.getString("relation");
								double rhs = jsonObject.getJsonNumber("rhs").doubleValue();
								String name=null;
								if(jsonObject.containsKey("name")) 	{
									name=jsonObject.getString("name");
								}
								
								double value;
								for (String key : coef.keySet()) {
									int index=nomi_var.indexOf(key);
									value=coef.getJsonNumber(key).doubleValue();
									Ai[index]=value;	
								}
								ConsType relaz=null;
								if(rel.equals("eq")) relaz=ConsType.EQ;
								else if(rel.equals("le")) relaz=ConsType.LE;
								else if(rel.equals("ge")) relaz=ConsType.GE;
								
								if(name==null || name.trim().equals("")) constraints.add(new Constraint(Ai, relaz, rhs));
								else constraints.add(new Constraint(Ai, relaz, rhs,name)); 
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
					double[] upper=new double[dimension];
					for(int i=0;i<dimension;i++) upper[i]=Double.NaN;
					double[] lower=new double[dimension];
					JsonValue jval =null;
					JsonObject jsonObject=null;
					cc: while (parser.hasNext()) {
						if ((event = parser.next()) == KEY_NAME) {
							String key_name=parser.getString();
							int index=nomi_var.indexOf(key_name.toUpperCase());
							if ((event = parser.next()) == START_OBJECT) {
								jval = parser.getValue();
								if (jval.getValueType() == JsonValue.ValueType.OBJECT) {
									jsonObject = jval.asJsonObject();
									if(jsonObject.containsKey("upper")) {
										if(jsonObject.isNull("upper")) upper[index]=Double.NaN;
										else { 
											upper[index]=jsonObject.getJsonNumber("upper").doubleValue();
											if(upper[index] < 0) lower[index]=Double.NaN;
										}
									}
									if(jsonObject.containsKey("lower")) {
										if(jsonObject.isNull("lower")) lower[index]=Double.NaN;
										else lower[index]=jsonObject.getJsonNumber("lower").doubleValue();
									}
									/*
									for (String kej : jsonObject.keySet()) {
										if(kej.equals("upper")) {
											if(jsonObject.isNull(kej)) upper[index]=Double.NaN;
											else upper[index]=jsonObject.getJsonNumber(kej).doubleValue();
										}
										else if(kej.equals("lower")) {
											if(jsonObject.isNull(kej)) lower[index]=Double.NaN;
											else lower[index]=jsonObject.getJsonNumber(kej).doubleValue();
										}
									}*/
								}	
							}
						} 
						else {
							break cc;
						}
					}
					constraints.add(new Constraint(lower, ConsType.LOWER, Double.NaN));
					constraints.add(new Constraint(upper, ConsType.UPPER, Double.NaN));
				}
			} 
			else if (event == KEY_NAME && parser.getString().toUpperCase().equals("VARIABLES")) {
				if (parser.next() == START_OBJECT) {
					double[] integer=new double[dimension];
					double[] binary=new double[dimension];
					double[] semic=new double[dimension];

					vr: while (parser.hasNext()) {
						if ((event = parser.next()) == KEY_NAME) {
							String varName=parser.getString();
							int index=nomi_var.indexOf(varName.toUpperCase());
							if ((event = parser.next()) == VALUE_STRING) {
								String type=parser.getString();
								if(type.equals("integer")) integer[index]=1;
								else if(type.equals("binary"))	binary[index]=1;
								else if(type.equals("semicont")) semic[index]=1;
								else if(type.equals("integer-semicont"))  {
									integer[index]=1;
									semic[index]=1;
								}
							}
						} 
						else {
							break vr;
						}
					}
					constraints.add(new Constraint(integer, ConsType.INT, Double.NaN));
					constraints.add(new Constraint(semic, ConsType.SEMICONT, Double.NaN));
					constraints.add(new Constraint(binary, ConsType.BIN, Double.NaN));
				}
			}
		}
		parser.close();
	}
}
