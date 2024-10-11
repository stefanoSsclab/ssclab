package org.ssclab.pl.milp.scantext;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ssclab.i18n.RB;
import org.ssclab.pl.milp.ParseException;

public class CheckSintaxText {
	
	Pattern pattern_fo1 = Pattern.compile("\\s*(min|max)\\s*:\\s*([+-]?)\\s*((\\d+)(\\.)?(\\d*))?((\\p{Alpha}+)(\\w*))\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_fo2 = Pattern.compile("[+-]\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*)\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons1 = Pattern.compile("\\s*(\\p{Alpha}+\\w*\\s*:\\s*)",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons3 = Pattern.compile("[+-]\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*)\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons4 = Pattern.compile("(([+-])\\s*(\\d+)(\\.?)(\\d*))\\s*",Pattern.CASE_INSENSITIVE);
	String line_fo="";
	boolean exist_fo=false;

	public CheckSintaxText(ArrayList<String> pl_problem) throws  ParseException {
		for(String line_problem:pl_problem)  check(line_problem);
	}
	
	public CheckSintaxText(BufferedReader br) throws  IOException, ParseException {
		String line="";
		while( (line = br.readLine()) != null) check(line);
		
	}
	
	private void check(String line) throws ParseException {
		//se stringa vuota
		if (line.trim().equals("")) return;
		//se stringa f.o.
		else if (line.matches("\\s*(?i)(min|max)\\s*:(.+)")) {
			if(this.exist_fo) throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.CheckSintaxText.msg2")+" ["+line+"]");
			checkSintassiFo(line);
			this.line_fo=line;
			this.exist_fo=true;
		}
		else if (line.matches("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*")) {
			//non sono presenti tutti i controlli, altri controlli li fa quando lo elabora
		}
		else if (line.matches("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*")) {
			//non sono presenti tutti i controlli, altri controlli li fa quando lo elabora
		}
		else if (line.matches("(.+)((<\\s*=)|(>\\s*=)|(=))(.+)")) {
			checkSintassiConstraint(line);
		}
		else if (line.matches("\\s*(?i)((bin)|(sec)|(int))\\s+((\\p{Alpha}+)(\\w*))\\s*(.*)")) {
			checkSintassiInt(line);
		}
		else if (line.matches("\\s*(?i)((sos[12])|(sos[12]\\s*:\\s*bin(\\s*:\\s*force)?)|(sos[12]\\s*:\\s*int))\\s+((\\p{Alpha}+)(\\w*))\\s*(.*)")) {
			checkSintassiSOS(line);
		}
		else { 
			throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.CheckSintaxText.msg1")+" ["+line+"]");
		}
	}
	
	
	public String getLineFO() throws ParseException {
		if(!this.exist_fo) throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.CheckSintaxText.msg3"));
		return line_fo;
	}

	private void checkSintassiFo(String fo_string) throws ParseException {
		//Pattern pattern_fo1 = Pattern.compile("\\s*(min|max)\\s*:\\s*([+-]?)\\s*((\\d+)(\\.)?(\\d*))?((\\p{Alpha}+)(\\w*))\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern_fo1.matcher(fo_string);
		int end=0;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		else { 
			throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.CheckSintaxText.msg4")+" ["+fo_string+"]");
		}
		String resto=fo_string.substring(end);
		
		while(!resto.equals(""))  {
			//Pattern pattern_fo2 = Pattern.compile("[+-]\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*)\\s*",Pattern.CASE_INSENSITIVE);
			Matcher matcher2 = pattern_fo2.matcher(resto);
			if (matcher2.lookingAt()) {
				end=matcher2.end();
				resto=resto.substring(end);
			}	
			else { 
				throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.CheckSintaxText.msg5")+" ["+resto+"]");
			}
		}
	}
	
	
	private void checkSintassiConstraint(String line) throws  ParseException {

		Matcher matcher_group_var = pattern_cons1.matcher(line);
		int end=0,end2=0;;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		String resto2=line.substring(end);
		resto2=resto2.trim();
		String[] separation = resto2.split("[><]?\\s*=");
		Pattern pattern_cons2 = Pattern.compile("[><]?\\s*=");
	    Matcher matcherSep = pattern_cons2.matcher(resto2);
        int conteggio = 0;
        // Scansiona la stringa e conta le occorrenze
        while (matcherSep.find()) {
        	conteggio++;
	    }
    	//System.out.println("conteggio:"+conteggio);
    	if(conteggio!=1 || separation.length!=2 || separation[0].trim().equals("")) { 
    		throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+line+"]");
		}
		//deve iniziare per + o - o niente, se niente va in errore. 
    	for(int _i=0;_i<separation.length;_i++) {
    		separation[_i]=separation[_i].trim();
    		char inizio=separation[_i].charAt(0);
	    	 if (!(inizio == '+' || inizio == '-'))  {
	             separation[_i]="+"+separation[_i];
	    	 }
    	 }
	
		for(String meta:separation)	 {
			String resto=meta;
			while(!resto.equals(""))  {
				Matcher matcher2 = pattern_cons3.matcher(resto);
				Matcher matcher3 = pattern_cons4.matcher(resto);
				if (matcher2.lookingAt()) {
					end2=matcher2.end();
					resto=resto.substring(end2);
				}	
				else if (matcher3.lookingAt()) {
					end2=matcher3.end();
					resto=resto.substring(end2);
				}	
				else { 
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+line+"]");
				}
			}
		}	
	}
	

	private void checkSintassiInt(String line) throws ParseException {

		Pattern pattern_int = Pattern.compile("\\s*((bin)|(sec)|(int))\\s+",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern_int.matcher(line);
		int end=0;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		else { 
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" ["+line+"]");
		}
		String resto=line.substring(end);
		resto=resto.trim();
		//System.out.println("qua:"+resto);
		if(!resto.equals("")) {
			String[] tokens = resto.split("\\s*,\\s*");
			for(String token:tokens) {  
				//System.out.println("qua3:"+token);
				if(!token.matches("\\s*(\\p{Alpha}+\\w*)\\**\\s*"))
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" ["+line+"] : error in "+token);
			}	
		}
	}
	
	private void checkSintassiSOS(String line) throws ParseException {

		Pattern pattern_int  = Pattern.compile("\\s*((sos[12]\\s*:\\s*bin\\s*:\\s*force))\\s+",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var = pattern_int.matcher(line);
		
		Pattern pattern_int2 = Pattern.compile("\\s*((sos[12]\\s*:\\s*bin)|(sos[12]\\s*:\\s*int))\\s+",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var2 = pattern_int2.matcher(line);
		
		Pattern pattern_int3 = Pattern.compile("\\s*((sos[12]))\\s+",Pattern.CASE_INSENSITIVE);
		Matcher matcher_group_var3 = pattern_int3.matcher(line);
		
		int end=0;
		//MAX o MIN
		if (matcher_group_var.lookingAt()) {
			end=matcher_group_var.end();
		}	
		else if (matcher_group_var2.lookingAt()) {
			end=matcher_group_var2.end();
		}	
		else if (matcher_group_var3.lookingAt()) {
			end=matcher_group_var3.end();
		}	
		else { 
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" ["+line+"]");
		}
		String resto=line.substring(end).trim();
		//System.out.println("qua:"+resto);
		if(!resto.equals("")) {
			String[] tokens = resto.split("\\s*,\\s*");
			for(String token:tokens) {  
				if(!token.matches("\\s*(\\p{Alpha}+\\w*)\\**\\s*"))
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" ["+line+"] : error in "+token);
			}	
		}
	}
}
