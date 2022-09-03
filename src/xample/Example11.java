package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.util.regex.Pattern;

public class Example11 {
	
	/**
	* Parole chiave : 
	* 
	* 1) lettura di date in formato di ora
	* 2) utilizzo delle funzione in() / notin() 
	* 3) impostazione di un valore per la rappresentazione dei null su output di tipo testo 
	*/


	public static void main(String[] args) throws Exception {
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			InputFile ref = new InputFile("E:\\fmt_stat_prove\\time.txt");
			ref.setInputFormat( "A:fixstring(10), B:varstring(5), C:varstring(30), ETA:varstring(100), ORA:date(hh:mm:ss),REDDITO:double");
			ref.setSeparator(Pattern.compile(";")); 
			ref.setMissingValue(".");
			
	
			FactoryLibraries factory_libraries=session.getFactoryLibraries(); 
			factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			
			FactorySteps factory_step=session.getFactorySteps();
			DataStep datastep=factory_step.createDataStep("PIPPO.DATI_TIME",ref); 
			datastep.execute();
			
			
			FileStep filestep=factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_time.txt", "PIPPO.DATI_TIME");
			filestep.setWhere("notin(ORA,\"5:22:59\",\"6:42:57\");"); 
			filestep.setOutputMissing("#");
			filestep.printf("%2s;%2$tT", "C","ORA"); 
			filestep.execute(); 
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
