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
import org.ssclab.pl.milp.SosGroup;
import org.ssclab.pl.milp.InternalConstraint.TYPE_CONSTR;
import org.ssclab.pl.milp.SosGroup.TYPE_SOS_GROUP;
import org.ssclab.pl.milp.Variable.TYPE_VAR;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ScanConstraintFromLine {
	
	private ArrayList<InternalConstraint> new_constraints;
	private ArrayList<String> nomi_var;
	private int dimension;
	private double Ai[];
	private ArrayProblem arraysProb;
	//nei pattern il |(\\.) , serve ad indicare il missing
	Pattern pattern_gen1 = Pattern.compile("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_gen2 = Pattern.compile("\\s*(\\p{Alpha}+\\w*\\s*:)?\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_gen3 = Pattern.compile("\\s*((bin)|(sec)|(int))\\s+((\\p{Alpha}+)(\\w*))\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_gen4 = Pattern.compile("\\s*((sos[12])|(sos[12]\\s*:\\s*bin(\\s*:\\s*force)?)|(sos[12]\\s*:\\s*int))\\s+((\\p{Alpha}+)(\\w*))\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_upper1= Pattern.compile("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[[^\\[\\]]+?\\]))|(\\.))\\s*(>|<)\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*((>|<)\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[[^\\[\\]]+?\\]))|(\\.)))?\\s*",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons1 = Pattern.compile("(([+-])\\s*(\\d+\\.?\\d*)?\\s*(\\p{Alpha}+\\w*)\\s*)",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons2 = Pattern.compile("([+-]\\s*(\\d+)(\\.?)(\\d*))\\s*",Pattern.CASE_INSENSITIVE);

	Pattern pattern_cons3 = Pattern.compile("(([+-]?)\\s*\\[([^\\[\\]]+?)\\]\\s*(\\p{Alpha}+\\w*)\\s*)",Pattern.CASE_INSENSITIVE);
	Pattern pattern_cons4 = Pattern.compile("([+-]?)\\s*\\[([^\\[\\]]+?)\\]\\s*",Pattern.CASE_INSENSITIVE);

	
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
		//SOS1
		Matcher sos1 = pattern_gen4.matcher(inequality);

		if (upper_1.matches()) {
			scanUpper(inequality);
		}
		else if (upper_2.matches()) {
			scanUpper(inequality);
		}
		else if (sos1.lookingAt() ) {
			//checkSintassiInt(inequality);
			scanSos1(inequality);
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
			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+inequality);
		}
	}
	
	private void scanUpper(String line) throws LPException {

		line=  line.replaceAll("\\s*(\\p{Alpha}+\\w*\\s*:)\\s*", "");
		boolean minore=false;   
		Expression expression2=null;
		Matcher upper = pattern_upper1.matcher(line);
		if (line.matches("(.+)<\\s*=(.+)")) minore=true; 
		
		if (upper.matches()) {
			
			String segno1=upper.group(4); 
			String numero1=upper.group(5); 
			String punto1=upper.group(6); 
			
			String segno2=upper.group(13);
			String numero2=upper.group(14);
			String punto2=upper.group(15); 
			
			if(numero1==null && punto1==null  && numero2==null && punto2==null) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg4")+" "+line);

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
			else if(numero1!=null && numero1.contains("[")) {
				try {
					expression2 = new ExpressionBuilder(numero1).build();
					a1 = expression2.evaluate();
				}
				catch(Exception e) {
					throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromLine.msg1")+" "+numero1);
				}
				if(segno1.equals("-"))  a1=- a1;
			}
			else if(numero1!=null) a1=Double.parseDouble(segno1+numero1);

			if(punto2!=null) a2=Double.NaN;
			else if(numero2!=null && numero2.contains("[")) {
				try {
					expression2 = new ExpressionBuilder(numero2).build();
					a2 = expression2.evaluate();
				}
				catch(Exception e) {
					throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromLine.msg1")+" "+numero2);
				}
				if(segno2.equals("-"))  a2=- a2;
			}
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

		int index=0;
		if (line.toLowerCase().contains("int ")) {
			//se ho trovato delle varaibil intere , memorizzo l'informazione nel caso mi trovi in ambito 
			//non MILP, altrimenti Exception
			arraysProb.isMilp=true;
			
			if(line.matches("\\s*(?i)(int)\\s+(?i)(all)\\s*")) {
				for(int j=0;j<arraysProb.array_int.length;j++) {
					arraysProb.array_int[j]=1;
				}
				//System.out.println("ALL INT");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(int)\\s+", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg8")+" "+line);
			for(String none_var:tokens) {  
				//System.out.println("->"+none_var+"<-");
				if(none_var.endsWith("*")) { 
					String prefix=none_var.replace("*", "").toUpperCase();
					boolean trovate=false;
					for(String variabile:nomi_var) {
						if(variabile.startsWith(prefix)) {
							trovate=true;
							index=nomi_var.indexOf(variabile);
							if(arraysProb.array_int[index]==1) 
								throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg16",variabile));
							
							arraysProb.array_int[index]=1;
							//System.out.println("->"+none_var+"<- 111");
						}
					}
					if(!trovate)  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg22")+prefix+"*");
				}
				else { 
					index=nomi_var.indexOf(none_var.toUpperCase());
					if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg12", none_var));
					if(arraysProb.array_int[index]==1) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg16",none_var));
					arraysProb.array_int[index]=1;
				}
			}		 
		} 
		else if (line.toLowerCase().contains("bin ")) {
			arraysProb.isMilp=true;
			
			if(line.matches("\\s*(?i)(bin)\\s+(?i)(all)\\s*")) {
				for(int j=0;j<arraysProb.array_bin.length;j++) {
					arraysProb.array_bin[j]=1;
				}
				//System.out.println("ALL bin");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(bin)\\s+", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg10")+" "+line);
			for(String none_var:tokens) {  
				if(none_var.endsWith("*")) { 
					String prefix=none_var.replace("*", "").toUpperCase();
					boolean trovate=false;
					for(String variabile:nomi_var) {
						if(variabile.startsWith(prefix)) {
							trovate=true;
							index=nomi_var.indexOf(variabile);
							if(arraysProb.array_bin[index]==1) 
								throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg17",variabile));
							
							arraysProb.array_bin[index]=1;
							//System.out.println("->"+none_var+"<- 111");
						}
					}
					if(!trovate)  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg22")+prefix+"*");
				}
				else { 
					index=nomi_var.indexOf(none_var.toUpperCase());
					if(index==-1 )  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
					if(arraysProb.array_bin[index]==1) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg17",none_var));
					arraysProb.array_bin[index]=1;
				}	
			}	
		} 
		else if (line.toLowerCase().contains("sec ")) {
			arraysProb.isMilp=true;
			if(line.matches("\\s*(?i)(sec)\\s+(?i)(all)\\s*")) {
				for(int j=0;j<arraysProb.array_sec.length;j++) {
					arraysProb.array_sec[j]=1;
				}
				//System.out.println("ALL sec");
				return;
			}
			
			String line2=line.replaceAll("\\s*(?i)(sec)\\s+", "").trim();
			//System.out.println("@@@@@@@@@@@@::"+line);
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg9")+" "+line);
			for(String none_var:tokens) {  
				if(none_var.endsWith("*")) { 
					String prefix=none_var.replace("*", "").toUpperCase();
					boolean trovate=false;
					for(String variabile:nomi_var) {
						if(variabile.startsWith(prefix)) {
							trovate=true;
							index=nomi_var.indexOf(variabile);
							if(arraysProb.array_sec[index]==1) 
								throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg18",variabile));
							
							arraysProb.array_sec[index]=1;
							//System.out.println("->"+none_var+"<- 111");
						}
					}
					if(!trovate)  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg22")+prefix+"*");
				}
				else { 
					index=nomi_var.indexOf(none_var.toUpperCase());
					if(index==-1 )  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2", none_var));
					if(arraysProb.array_sec[index]==1) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg18",none_var));
					arraysProb.array_sec[index]=1;
				}	
			}	
		} 
	}

	public ArrayProblem getArraysProblem() {
		return arraysProb;
	}
	
	
	private void scanDisequestionCompleta(String line) throws LPException, ParseException {
		
		InternalConstraint internal=new InternalConstraint(dimension);
		Expression expression2=null;
		String name;
		double aj=0,b=0,bi=0;
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
		
		//deve iniziare per + o - o niente, se niente va in errore. 
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
				Matcher matcher4 = pattern_cons3.matcher(resto);
				Matcher matcher5 = pattern_cons4.matcher(resto);
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
				
				else if (matcher4.lookingAt()) {
					
					String segno_var=matcher4.group(2); 
					if(segno_var==null) segno_var="+";
					
					String number_var=matcher4.group(3); 
					if(number_var==null) number_var="1";
					try {
						//System.out.println(":"+number_var);
						expression2 = new ExpressionBuilder(number_var).build();
				  	    aj = expression2.evaluate();
					}
					catch(Exception e) {
						throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromLine.msg1")+" "+number_var);
					}
					if(segno_var.equals("-"))  aj=- aj;
					
					//aj=Double.parseDouble(segno_var+number_var);
					//System.out.println("numero []:"+aj);
					String nome_var=matcher4.group(4).toUpperCase(); 
					index=nomi_var.indexOf(nome_var);
					if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg2",nome_var));
					//System.out.println("var []:"+nome_var);
					if(_j==0) Ai[index]=aj+Ai[index];
					else Ai[index]=-aj+Ai[index];
					
					end=matcher4.end();
					resto=resto.substring(end);
				}	
				
				
				else if (matcher3.lookingAt()) {
					end=matcher3.end();
					resto=resto.substring(end);
					//System.out.println("Greuppo b:"+matcher3.group(0));
					String doppio=matcher3.group(0).replaceAll("\\s", "");
					bi= Double.parseDouble(doppio);
					if(_j==0) b=b-bi;
					else b=b+bi;
				}	
				
				
				else if (matcher5.lookingAt()) {
					end=matcher5.end();
					resto=resto.substring(end);
					
					String segno_var=matcher5.group(1); 
					if(segno_var==null) segno_var="+";
					
					String number_var=matcher5.group(2); 
					if(number_var==null) number_var="1";
					try {
						//System.out.println(number_var);
						expression2 = new ExpressionBuilder(number_var).build();
						bi = expression2.evaluate();
					}
					catch(Exception e) {
						throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromLine.msg1")+" "+number_var);
						
					}
					if(segno_var.equals("-"))  bi=- bi;
					//System.out.println("numero bb []:"+bi);
					if(_j==0) b=b-bi;
					else b=b+bi;
				}	
				
				
				else { 
					//System.out.println("reess:"+resto);
					throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+line);
				}
			}
		}	
		internal.setBi(b);
		for(int a=0;a<Ai.length;a++) if(Ai[a]!=0) internal.setAij(a,Ai[a]);
		new_constraints.add(internal);
	}
	
	
	private void scanSos1(String line) throws LPException { 
		int index=0;
		if (line.toLowerCase().matches("\\s*sos[12]\\s*:\\s*bin(\\s*:\\s*force)?\\s*.*")) {
			
			arraysProb.isMilp=true;
			
			TYPE_SOS_GROUP typeSos;
			if(line.toLowerCase().matches("\\s*sos1\\s*:\\s*bin\\s*:\\s*force\\s*.*"))  typeSos=TYPE_SOS_GROUP.SOS1_BIN_FORCE;
			else if(line.toLowerCase().matches("\\s*sos2\\s*:\\s*bin\\s*:\\s*force\\s*.*"))   typeSos=TYPE_SOS_GROUP.SOS2_BIN_FORCE;
			else if(line.toLowerCase().matches("\\s*sos1\\s*:\\s*bin\\s*.*"))  typeSos=TYPE_SOS_GROUP.SOS1_BIN;
			else typeSos=TYPE_SOS_GROUP.SOS2_BIN;
			
			SosGroup group=new SosGroup(typeSos);
			
			String line2=line.replaceAll("\\s*(?i)(sos[12]\\s*:\\s*bin(\\s*:\\s*force)?)\\s+","").trim();
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg13")+" "+line);
			for(String none_var:tokens) {  
				if(none_var.endsWith("*") && (typeSos==TYPE_SOS_GROUP.SOS1_BIN_FORCE || typeSos==TYPE_SOS_GROUP.SOS1_BIN)) { 
					String prefix=none_var.replace("*", "").toUpperCase();
					for(String variabile:nomi_var) {
						if(variabile.startsWith(prefix)) {
							index=nomi_var.indexOf(variabile);
							if(arraysProb.array_sos[index]!=null) 
								throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg14",variabile));
							arraysProb.array_sos[index]=TYPE_VAR.BINARY;
							if(typeSos==TYPE_SOS_GROUP.SOS1_BIN) arraysProb.array_bin[index]=1;
							group.addVar(variabile.toUpperCase(), index);
						}
					}
					if(group.size()==0) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg22")+prefix+"*");
				}
				else if(none_var.endsWith("*") && (typeSos==TYPE_SOS_GROUP.SOS2_BIN_FORCE || typeSos==TYPE_SOS_GROUP.SOS2_BIN)) { 
					throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg21"));
				}
				else { 
					index=nomi_var.indexOf(none_var.toUpperCase());
					if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg15", none_var));
					if(arraysProb.array_sos[index]!=null) 
						throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg14",none_var));
					
					arraysProb.array_sos[index]=TYPE_VAR.BINARY;
					if(typeSos==TYPE_SOS_GROUP.SOS1_BIN || typeSos==TYPE_SOS_GROUP.SOS2_BIN)
						arraysProb.array_bin[index]=1;
					group.addVar(none_var.toUpperCase(), index);
				}
			}	
			if(typeSos==TYPE_SOS_GROUP.SOS1_BIN || typeSos==TYPE_SOS_GROUP.SOS1_BIN_FORCE) {
				if(group.size() < 2) 
					 throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg19"));
			}
			else if(typeSos==TYPE_SOS_GROUP.SOS2_BIN || typeSos==TYPE_SOS_GROUP.SOS2_BIN_FORCE) {
				if(group.size() < 3) 
					 throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg20"));
			}
			arraysProb.listSosGroup.add(group);
		} 
		
		else if (line.toLowerCase().matches("\\s*sos[12]\\s*:\\s*int\\s*.*")) {
			
			arraysProb.isMilp=true;
			
			TYPE_SOS_GROUP typeSos;
			if(line.toLowerCase().matches("\\s*sos1\\s*:\\s*int\\s*.*"))  typeSos=TYPE_SOS_GROUP.SOS1_INT;
			else  typeSos=TYPE_SOS_GROUP.SOS2_INT;
			
			SosGroup group=new SosGroup(typeSos);
						
			String line2=line.replaceAll("\\s*(?i)(sos[12]\\s*:\\s*int)\\s+", "").trim();
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg13")+" "+line);
			for(String none_var:tokens) {  
				if(none_var.endsWith("*") && typeSos==TYPE_SOS_GROUP.SOS1_INT) { 
					String prefix=none_var.replace("*", "").toUpperCase();
					for(String variabile:nomi_var) {
						if(variabile.startsWith(prefix)) {
							index=nomi_var.indexOf(variabile);
							if(arraysProb.array_sos[index]!=null) 
								throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg14",variabile));
							arraysProb.array_sos[index]=TYPE_VAR.INTEGER;
							arraysProb.array_int[index]=1;
							group.addVar(variabile.toUpperCase(), index);
						}
					}
					if(group.size()==0)  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg22")+prefix+"*");
				}
				else if(none_var.endsWith("*") && (typeSos==TYPE_SOS_GROUP.SOS2_INT)) { 
					throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg21"));
				}
				else { 
					index=nomi_var.indexOf(none_var.toUpperCase());
					if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg15", none_var));
					if(arraysProb.array_sos[index]!=null) 
						throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg14",none_var));
					
					arraysProb.array_sos[index]=TYPE_VAR.INTEGER;
					arraysProb.array_int[index]=1;
					group.addVar(none_var.toUpperCase(), index);
				}
			}	
			if(typeSos==TYPE_SOS_GROUP.SOS1_INT) {
				if(group.size() < 2) 
					 throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg19"));
			}
			else if(typeSos==TYPE_SOS_GROUP.SOS2_INT) {
				if(group.size() < 3) 
					 throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg20"));
			}
			arraysProb.listSosGroup.add(group);
		} 
		else if (line.toLowerCase().contains("sos1 ") || line.toLowerCase().contains("sos2 ")) {
			
			arraysProb.isMilp=true;
			TYPE_SOS_GROUP typeSos;
			if(line.toLowerCase().contains("sos1 "))  typeSos=TYPE_SOS_GROUP.SOS1;
			else  typeSos=TYPE_SOS_GROUP.SOS2;
			
			SosGroup group=new SosGroup(typeSos);
			
			String line2=line.replaceAll("\\s*(?i)(sos[12])\\s+", "").trim();
			String[] tokens = line2.split("\\s*,\\s*");
			if(tokens.length==0) throw new LPException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg13")+" "+line);
			for(String none_var:tokens) {  
				if(none_var.endsWith("*") && typeSos==TYPE_SOS_GROUP.SOS1) { 
					String prefix=none_var.replace("*", "").toUpperCase();
					for(String variabile:nomi_var) {
						if(variabile.startsWith(prefix)) {
							index=nomi_var.indexOf(variabile);
							if(arraysProb.array_sos[index]!=null) 
								throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg14",variabile));
							arraysProb.array_sos[index]=TYPE_VAR.REAL;
							group.addVar(variabile.toUpperCase(), index);
						}
					}
					if(group.size()==0)  throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg22")+prefix+"*");
				}
				else if(none_var.endsWith("*") && (typeSos==TYPE_SOS_GROUP.SOS2)) { 
					throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg21"));
				}
				else { 
					index=nomi_var.indexOf(none_var.toUpperCase());
					if(index==-1 ) throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg15", none_var));
					if(arraysProb.array_sos[index]!=null) 
						throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg14",none_var));
					
					arraysProb.array_sos[index]=TYPE_VAR.REAL;
					group.addVar(none_var.toUpperCase(), index);
				}
			}	
			
			if(typeSos==TYPE_SOS_GROUP.SOS1) {
				if(group.size() < 2) 
					 throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg19"));
			}
			else if(typeSos==TYPE_SOS_GROUP.SOS2) {
				if(group.size() < 3) 
					 throw new LPException(RB.format("it.ssc.pl.milp.ScanConstraintFromString.msg20"));
			}
			arraysProb.listSosGroup.add(group);
		} 
	}
}
