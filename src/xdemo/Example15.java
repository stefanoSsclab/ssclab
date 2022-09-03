package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.step.FileStep;
import it.ssc.step.ParameterStep;

public class Example15 {  //NON FARE 
	
	
	/**
	* Parole chiave : 
	* 
	* 1)Passaggio di oggetti al datastep 
	* 
	* */
	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
		
		session =  Context.createNewSession();
		session.addLibrary("PIPPO", "f:\\demo\\libreria_dati");
		
		String[] value_pass = {"g" , "d",  null };
		ParameterStep param=new ParameterStep();
		param.setParamAttribute("VALORES_BONOS_PARA_C", value_pass);
		
		
		FileStep filestep=session.createFileStep("f:\\demo\\dati_testo\\stampa_missing3.txt", "PIPPO.DATI_CON_MISS");
		filestep.setParameter(param);
		filestep.setWhere(" in(C,(String[])getParamAttribute(\"VALORES_BONOS_PARA_C\")) ;");
		filestep.printf("%s   %15s", "C","GIORNO");
		filestep.execute();
		
	
		session.close();
		
	}
}
