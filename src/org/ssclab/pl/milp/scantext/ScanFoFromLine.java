package org.ssclab.pl.milp.scantext;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ssclab.i18n.RB;
import org.ssclab.pl.milp.GoalType;
import org.ssclab.pl.milp.LPException;
import org.ssclab.pl.milp.LinearObjectiveFunction;
import org.ssclab.pl.milp.ParseException;

public class ScanFoFromLine {
	
	private String target_fo;
	private ArrayList<String> list_var;
	private double[] list_cj;
	
	public ScanFoFromLine(String line_fo,ArrayList<String> list_var) throws  ParseException {
	
		this.list_var=list_var;
		this.list_cj=new double[list_var.size()];
		scanFoFromString(line_fo);
	}
	
	
	/*
	public ScanFoFromLine(ArrayList<String> pl_problem) throws  ParseException {
		Iterator<String> iter=pl_problem.iterator();
		String fo_string="";
		while(iter.hasNext() && fo_string.equals("")) {
			fo_string=iter.next().trim();
			iter.remove();
		}
		scanFoFromString(fo_string);
	}
	
	
	public ScanFoFromLine(BufferedReader br) throws  IOException, ParseException {
		
		String fo_string="",line="";
		while(fo_string.equals("") && (line = br.readLine()) != null   ) {
			fo_string=line.trim();
		}
		scanFoFromString(fo_string);
	}
	*/
	
	public LinearObjectiveFunction getFOFunction() throws LPException {
		
		GoalType goal=GoalType.MIN;
		if(target_fo.equals("MAX")) goal=GoalType.MAX;
		return new LinearObjectiveFunction(this.list_cj, goal);
	}
	
	private void scanFoFromString(String fo_string) throws ParseException  {

		Pattern pattern = Pattern.compile("\\s*(min|max)\\s*:\\s*(([+-]?)\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*))\\s*",Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fo_string);
		int end=0;
		//MAX o MIN
		if (matcher.lookingAt()) {
			end=matcher.end();
			target_fo =matcher.group(1).toUpperCase(); //MAX o MIN
			String segno_prima_var=matcher.group(3); 
			if(segno_prima_var==null) segno_prima_var="+";

			String number_prima_var=matcher.group(4); 
			if(number_prima_var==null) number_prima_var="1";
			double cj=Double.parseDouble(segno_prima_var+number_prima_var);

			String nome_prima_var=matcher.group(5).toUpperCase(); 
			//non verifico nulla, in quanto e' la prima variabile inserita 
				
			int index=list_var.indexOf(nome_prima_var);
			if(index==-1) throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.ScanFoFromLine.msg1")+" ["+nome_prima_var+"]");
			list_cj[index]=cj+list_cj[index];
						
		}	
		//tolgo la parte gia elaborata
		String resto=fo_string.substring(end);

		Pattern pattern2 = Pattern.compile("(([+-])\\s*(\\d+\\.?\\d*)?(\\p{Alpha}+\\w*)\\s*)",Pattern.CASE_INSENSITIVE);
		Matcher matcher2 = pattern2.matcher(resto);
		double cj;
		while (matcher2.find()) {

			String segno_var=matcher2.group(2); 
			if(segno_var==null) segno_var="+";

			String number_var=matcher2.group(3); 
			if(number_var==null) number_var="1";
			
			cj=Double.parseDouble(segno_var+number_var);
			
			String nome_var=matcher2.group(4).toUpperCase(); 
			//if(list_nomi_var.contains(nome_var)) throw new LPException(RB.format("it.ssc.pl.milp.ScanLineFOFromString.msg2",nome_var));
			
			int index=list_var.indexOf(nome_var);
			if(index==-1) throw new ParseException(RB.getString("org.ssclab.pl.milp.scantext.ScanFoFromLine.msg1")+" ["+nome_var+"]");
			list_cj[index]=cj+list_cj[index];	
		}	
	}
}
