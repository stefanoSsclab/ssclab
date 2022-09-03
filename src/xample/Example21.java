package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.util.regex.Pattern;

public class Example21 {
	
	/**
	* Parole chiave : 
	* 
	* 1)Passaggio di oggetti al datastep con chiamate a classi esterne
	*   (pericoloso, solo per utenti avanzati e che sanno quello che fanno)
	* 
	* */
	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			InputFile ref = new InputFile("E:\\fmt_stat_prove\\punto_miss.txt");
			ref.setInputFormat( "A:fixstring(10), B:varstring(5), C:varstring(30), ETA:varstring(100), GIORNO:date(gg-mm-aa),REDDITO:double");
			ref.setSeparator(Pattern.compile(";")); 
			ref.setMissingValue(".");
			
	
			FactoryLibraries factory_libraries=session.getFactoryLibraries(); 
			factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			FactorySteps factory_step=session.getFactorySteps();
			DataStep datastep=factory_step.createDataStep("PIPPO.DATI_MISS",ref); 
			datastep.execute();
			
			
			FileStep filestep=factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_missing.txt", "PIPPO.DATI_MISS");
			filestep.setWhere(" in(C,xample.Example21.getValue()) && notin(GIORNO,\"02/04/2012\",\"03-01-2012\") ;");
			filestep.setOutputMissing("@");
			filestep.printf("%2s;%15s", "C","GIORNO");
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
	
	public static String[] getValue() {
		String[] value_pass = { "a", "b", "k", null };
		return value_pass;
	}

}
