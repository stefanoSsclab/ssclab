package org.ssclab.pl.milp.scantext;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ssclab.i18n.RB;
import org.ssclab.pl.milp.ArrayProblem;
import org.ssclab.pl.milp.InternalConstraint;
import org.ssclab.pl.milp.LPException;
import org.ssclab.pl.milp.ParseException;
import org.ssclab.pl.milp.InternalConstraint.TYPE_CONSTR;

public class ScanConstraintFromLine {
	
	private ArrayList<InternalConstraint> new_constraints;
	private ArrayList<String> nomi_var;
	private int dimension;
	private double Ai[];
	private ArrayProblem arraysProb;
	//nei pattern il |(\\.) , serve ad indicare il missing
	Pattern pattern_gen1 = Pattern.compile("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_gen2 = Pattern.compile("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_gen3 = Pattern.compile("\\s*((bin)|(sec)|(int))\\s+((\\p{Alpha}+)(\\w*))\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_upper1 = Pattern.compile("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.))\\s*(>|<)\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*((>|<)\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons1 = Pattern.compile("(([+-])\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*)\\s*)",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons2 = Pattern.compile("([+-]\\s*(\\d+)(\\.?)(\\d*))\\s*",Pattern.CASE_INSENSITIVE);

	
	public ScanConstraintFromLine(ArrayList<String> inequality,ArrayList<String> nomi_var) throws  ParseException, LPException {
		this.nomi_var=nomi_var;
		this.dimension=nomi_var.size();
		this.new_constraints=new ArrayList<InternalConstraint>();
		Ai=new double[this.dimension];
		arraysProb=new ArrayProblem(this.dimension);
		
		for(String line_problem:inequality) {
			parseSingleLine(line_problem);
		}	
	}
	
	//da file 
	public ScanConstraintFromLine(BufferedReader br,ArrayList<String> nomi_var) throws  IOException, ParseException, LPException {
		this.nomi_var=nomi_var;
		this.dimension=nomi_var.size();
		this.new_constraints=new ArrayList<InternalConstraint>();
		Ai=new double[this.dimension];
		arraysProb=new ArrayProblem(this.dimension);
		
		String line;
		//NON CHIUDE IL FILE ? 
		while((line = br.readLine()) != null   ) {
			parseSingleLine(line);
		}	
	}
	
	public ArrayList<InternalConstraint> getConstraints() {
		return new_constraints;
	}
	

	private void parseSingleLine(String inequality) throws  ParseException, LPException {
		
		if(inequality.trim().equals("")) return;	
		if (inequality.matches("\\s*(?i)(min|max)\\s*:\\s*(.+)")) return;
		//upper e lower di tipo <= x <=
		Matcher upper_1 = pattern_gen1.matcher(inequality);
		//upper e lower di tipo >= x >=
		Matcher upper_2 = pattern_gen2.matcher(inequality);
		//int, binary e semicont 
		Matcher int_bin_sec = pattern_gen3.matcher(inequality);

		if (upper_1.matches()) {
			scanUpper(inequality);
		}
		else if (upper_2.matches()) {
			scanUpper(inequality);
		}
		else if (int_bin_sec.lookingAt() ) {
			//checkSintassiInt(inequality);
			scanIntSecBin(inequality);
		}
		else  if (inequality.matches("(.+)((<\\s*=)|(>\\s*=)|(=))(.+)")) {
			//checkSintassiCompleta(inequality);
			scanDisequestionCompleta(inequality);
		}
		else {
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+inequality+"]");
		}
	}
	
	private void scanUpper(String line) throws LPException {

		line=  line.replaceAll("\\s*(\\p{Alpha}+\\w*\\s*:)\\s*", "");
		boolean minore=false;                               
		Matcher upper = pattern_upper1.matcher(line);
		if (line.matches("(.+)<\\s*=(.+)")) minore=true; 
		
		if (upper.matches()) {
			
			String segno1=upper.group(4); 
			String numero1=upper.group(5); 
			String punto1=upper.group(6); 
			
			String segno2=upper.group(13);
			String numero2=upper.group(14);
			String punto2=upper.group(15); 
			
			if(numero1==null && punto1==null  && numero2==null && punto2==null) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+"["+line+"]");

			String nome_var=upper.group(8).toUpperCase();
			int index=nomi_var.indexOf(nome_var);
			if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg5",nome_var,line));

			/*
			System.out.println("--NUMERO1>>>>>"+numero1);
			System.out.println("--NUMERO2>>>>>"+numero2);
			System.out.println("--SEGNO1>>>>>"+segno1);
			System.out.println("--SEGNO2>>>>>"+segno2);
			System.out.println("--var>>>>>"+nome_var);
			System.out.println("--PPP1>>>>>"+punto1);
			System.out.println("--PPP2>>>>>"+punto2);
			*/

			if(segno1==null) segno1="+"; 
			if(segno2==null) segno2="+"; 

			double a1=0,a2=0;
			if(punto1!=null) a1=Double.NaN;
			else if(numero1!=null) a1=Double.parseDouble(segno1+numero1);

			if(punto2!=null) a2=Double.NaN;
			else if(numero2!=null) a2=Double.parseDouble(segno2+numero2);

			if(minore) {
				if(numero1!=null || punto1!=null) {
					if(arraysProb.array_lower[index]==null) arraysProb.array_lower[index]=a1;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg6",nome_var));
				}

				if(numero2!=null || punto2!=null) {
					if(arraysProb.array_upper[index]==null) arraysProb.array_upper[index]=a2;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg7",nome_var));
				}
			}
			else {
				if(numero1!=null || punto1!=null) {
					if(arraysProb.array_upper[index]==null) arraysProb.array_upper[index]=a1;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg7",nome_var));
				}
				if(numero2!=null || punto2!=null)  {
					if(arraysProb.array_lower[index]==null) arraysProb.array_lower[index]=a2;
					else throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg6",nome_var));
				}
			}
		}
	}

	private void scanIntSecBin(String line) throws LPException { 

		if (line.toLowerCase().contains("int")) {
			//se ho trovato delle varaibil intere , memorizzo l'informazione nel caso mi trovi in ambito 
			//non MILP, altrimenti Exception
			arraysProb.isMilp=true;
			
			if(line.matches("\\s*(?i)(int)\\s*(?i)(all)\\s*")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_int[j]=1;
				}
				//System.out.println("ALL INT");
				return;
			}
			
			
			String line2=line.replaceAll("\\s*(?i)(int)\\s*", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg8")+"["+line+"]");
			for(String none_var:tokens) {  
				int index=nomi_var.indexOf(none_var.toUpperCase());
				if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg12", none_var));
				arraysProb.array_int[index]=1;
			}		 
		} 
		else if (line.toLowerCase().contains("bin")) {
			arraysProb.isMilp=true;
			
			if(line.matches("\\s*(?i)(bin)\\s*(?i)all\\s")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_bin[j]=1;
				}
				//System.out.println("ALL bin");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(bin)\\s*", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg10")+"["+line+"]");
			for(String none_var:tokens) {  
				int index=nomi_var.indexOf(none_var.toUpperCase());
				if(index==-1 )  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
				arraysProb.array_bin[index]=1;
			}	
		} 
		else if (line.toLowerCase().contains("sec")) {
			arraysProb.isMilp=true;
			if(line.matches("\\s*(?i)(sec)\\s*(?i)all\\s")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_sec[j]=1;
				}
				//System.out.println("ALL sec");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(sec)\\s*", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg9")+"["+line+"]");
			for(String none_var:tokens) {  
				int index=nomi_var.indexOf(none_var.toUpperCase());
				if(index==-1 )  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
				arraysProb.array_sec[index]=1;
			}	
		} 
	}

	public ArrayProblem getArraysProb() {
		return arraysProb;
	}
	
	
	private void scanDisequestionCompleta(String line) throws LPException, ParseException {
		
		InternalConstraint internal=new InternalConstraint(dimension);
		String name;
		double aj=0,b=0;
		int index=0;
		for(int a=0;a<dimension;a++) Ai[a]=0;
		String line2=line;
		if (line2.matches("\\s*(\\p{Alpha}+\\w*\\s*:)(.+)")) {
			String[] tokens = line.split(":");
			name=tokens[0].trim();
			//nome del vincolo
			internal.setName(name);
			line2=tokens[1];
		} 

		if (line2.matches("(.+)>\\s*=(.+)")) {
			internal.setType(TYPE_CONSTR.GE);
		} 
		else if (line2.matches("(.+)<\\s*=(.+)")) {
			internal.setType(TYPE_CONSTR.LE);
		} 
		else if (line2.contains("=")) {
			internal.setType(TYPE_CONSTR.EQ);
		} 

		String[] disequation = line2.split("[><]?\\s*=");
		
		//deve iniziare per + o - o nienmte, se niente va in errore. 
    	for(int _i=0;_i<disequation.length;_i++) {
    		disequation[_i]=disequation[_i].trim();
    		char inizio=disequation[_i].charAt(0);
	    	 if (!(inizio == '+' || inizio == '-')) {
	    		 disequation[_i]="+"+disequation[_i];
	         }
    	 }
    	int end=0;
		//controllo lunghezz ==2
		//for(String meta:disequation)	 {
		for(int _j=0;_j<2;_j++) {
			String resto=disequation[_j];
			while(!resto.equals(""))  {
				Matcher matcher2 = pattern_cons1.matcher(resto);
				Matcher matcher3 = pattern_cons2.matcher(resto);
				if (matcher2.lookingAt()) {
					
					String segno_var=matcher2.group(2); 
					if(segno_var==null) segno_var="+";
					
					String number_var=matcher2.group(3); 
					if(number_var==null) number_var="1";
					aj=Double.parseDouble(segno_var+number_var);

					String nome_var=matcher2.group(4).toUpperCase(); 
					index=nomi_var.indexOf(nome_var);
					if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2",nome_var));
					
					if(_j==0) Ai[index]=aj+Ai[index];
					else Ai[index]=-aj+Ai[index];
					
					end=matcher2.end();
					resto=resto.substring(end);
				}	
				else if (matcher3.lookingAt()) {
					end=matcher3.end();
					resto=resto.substring(end);
					//System.out.println("Greuppo b:"+matcher3.group(0));
					String doppio=matcher3.group(0).replaceAll("\\s", "");
					double bi= Double.parseDouble(doppio);
					if(_j==0) b=b-bi;
					else b=b+bi;
				}	
				else { 
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" ["+line+"]");
				}
			}
		}	
		internal.setBi(b);
		for(int a=0;a<Ai.length;a++) if(Ai[a]!=0) internal.setAij(a,Ai[a]);
		new_constraints.add(internal);
	}
}
