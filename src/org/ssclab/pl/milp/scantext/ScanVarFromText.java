package org.ssclab.pl.milp.scantext;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ssclab.i18n.RB;
import org.ssclab.pl.milp.ParseException;

public class ScanVarFromText {
	
	private ArrayList<String> list_nomi_var;
	//pattern per un token del vincolo con presenza di variabile :" +4X1"
	Pattern pattern2 = Pattern.compile("(([+-])\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*)\\s*)",Pattern.CASE_INSENSITIVE);
	//pattern per un token del vincolo con solo numero :" +4"
	Pattern pattern3 = Pattern.compile("([+-]\\s*(\\d+)(\\.?)(\\d*))\\s*",Pattern.CASE_INSENSITIVE);
	
	//pattern per un token del vincolo con presenza di variabile :" +[4*3]X1"
	Pattern pattern4 = Pattern.compile("(([+-]?)\\s*\\[([^\\[\\]]+?)\\](\\p{Alpha}+\\w*)\\s*)",Pattern.CASE_INSENSITIVE);
	//pattern per un token del vincolo con solo numero :" +[4-2]"
	Pattern pattern5 = Pattern.compile("(([+-]?)\\s*\\[([^\\[\\]]+?)\\])\\s*",Pattern.CASE_INSENSITIVE);
	
	Pattern pattern_up = Pattern.compile("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*(>|<)\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*((>|<)\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	
	public ScanVarFromText(ArrayList<String> pl_problem) throws  ParseException {
		list_nomi_var=new ArrayList<String>();
		for(String line_problem:pl_problem)  parse(line_problem);
	}
	
	
	public ScanVarFromText(BufferedReader br) throws  IOException, ParseException {
		String line="";
		list_nomi_var=new ArrayList<String>();
		while( (line = br.readLine()) != null   ) {
			parse(line);
		}
	}
	
	private void parse(String line ) throws ParseException {
		
		if(line.trim().equals("")) return;
		
		//se e' un vincolo , analizza dopo con le if successive
		if(Pattern.compile("<\\s*=|>\\s*=|=").matcher(line).find()) { }
		
		else if (line.matches("\\s*(?i)(min|max)\\s*:(.+)")) return ;
		
		else if (line.matches("\\s*(?i)(bin|int|sec)\\s+(.+)")) return ;
		
		else if (line.matches("\\s*(?i)(sos[12])(.+)")) return ;
		
		//upper bound del tipo "nome_vincolo:a<=x<=b" , con a o b opzionali
		if (line.matches("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*")) {
			//System.out.println("11"+line);
			scanUpper(line)   ;     
		}
		//upper bound del tipo "nome_vincolo:a>=x>=b" , con a o b opzionali
		else if (line.matches("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*")) {
			scanUpper(line);
			//System.out.println("22"+line);
		}
		//gestione vincolo completa
		else if (line.matches("(.+)((<\\s*=)|(>\\s*=)|(=))(.+)")) {
			//System.out.println("33"+line);
			scanDisequestionCompleta(line);
		}
	}
	
	
	public ArrayList<String> getListNomiVar() {
		Collections.sort(list_nomi_var);
		return list_nomi_var;
	}

	private void scanDisequestionCompleta(String line) throws ParseException  {
		
		String line2=line;
		if (line2.matches("\\s*(\\p{Alpha}+\\w*\\s*:)(.+)")) {
			String[] tokens = line.split(":");
			line2=tokens[1];
		} 

		String[] disequation = line2.split("[><]?\\s*=");
		
		//deve iniziare per + o - o nienmte, se niente va in errore. 
    	for(int _i=0;_i<disequation.length;_i++) {
    		disequation[_i]=disequation[_i].trim();
    		char inizio=disequation[_i].charAt(0);
	    	 if (!(inizio == '+' || inizio == '-'))  {
	    		 disequation[_i]="+"+disequation[_i];
	    	 }
    	 }
		
    	int end2=0;
		
		//controllo lunghezz ==2
		//for(String meta:disequation)	 {
		for(int _j=0;_j<2;_j++) {
			String resto=disequation[_j];
			while(!resto.equals(""))  {
				Matcher matcher2 = pattern2.matcher(resto);
				Matcher matcher3 = pattern3.matcher(resto);
				Matcher matcher4 = pattern4.matcher(resto);
				Matcher matcher5 = pattern5.matcher(resto);
				if (matcher2.lookingAt() ) {
					String nome_var=matcher2.group(4).toUpperCase(); 
					//System.out.println(nome_var);
					if(nome_var.equals("BIN") || nome_var.equals("INT") ||  nome_var.equals("SEC") || 
					   nome_var.equals("TYPE") || nome_var.equals("RHS") || nome_var.equals("ALL") || 
					   nome_var.startsWith("SOS")) 
						throw new ParseException(RB.getString("it.ssc.pl.milp.ScanLineFOFromString.msg4")+" ["+line+"]");
					
					if(!list_nomi_var.contains(nome_var)) list_nomi_var.add(nome_var);		
					end2=matcher2.end();
					resto=resto.substring(end2);
				}	
				
				else if (matcher4.lookingAt() ) {
					String nome_var=matcher4.group(4).toUpperCase(); 
					//System.out.println(nome_var);
					if(nome_var.equals("BIN") || nome_var.equals("INT") ||  nome_var.equals("SEC") || 
					   nome_var.equals("TYPE") || nome_var.equals("RHS") || nome_var.equals("ALL") ||
					   nome_var.startsWith("SOS")) 
						throw new ParseException(RB.getString("it.ssc.pl.milp.ScanLineFOFromString.msg4")+" ["+line+"]");
					
					if(!list_nomi_var.contains(nome_var)) list_nomi_var.add(nome_var);			
					end2=matcher4.end();
					resto=resto.substring(end2);
				}	
				
				else if (matcher3.lookingAt() ) {
					end2=matcher3.end();
					resto=resto.substring(end2);
				}
				else if (matcher5.lookingAt() ) {
					end2=matcher5.end();
					resto=resto.substring(end2);
				}	
				else { 
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+line+"]");
				}
				//mettere esle con errore 
				//mettere esle con errore 
			}
		}	
	}
	
	private void scanUpper(String line) throws ParseException  {
		
		line=  line.replaceAll("\\s*(\\p{Alpha}+\\w*\\s*:)\\s*", "");
		                               
		Matcher upper = pattern_up.matcher(line);	
		if (upper.matches()) {
			String nome_var=upper.group(8).toUpperCase();
			if(nome_var.equals("BIN") || nome_var.equals("INT") ||  nome_var.equals("SEC") || 
			   nome_var.equals("TYPE") || nome_var.equals("RHS") || nome_var.equals("ALL") 	) 
				throw new ParseException(RB.getString("it.ssc.pl.milp.ScanLineFOFromString.msg4")+" ["+line+"]");
			
			if(!list_nomi_var.contains(nome_var)) list_nomi_var.add(nome_var);
		}
	}
}
