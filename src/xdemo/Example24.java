package xdemo;

import java.util.regex.Pattern;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;
import it.ssc.step.SortStep;

/**
* Parole chiave :  LETTURA DEI DATI DEL 760 
* 
* 1) Memorizzazione di un dataset in memoria
* 
* */

public class Example24 {
	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
		try {
			
			session = Context.createNewSession();
			
			InputFile ref = new InputFile("F:\\demo\\dati_testo\\ds760.csv");
			ref.setInputFormat("REDDITI1 - REDDITI71 :double, CLASSE1 - CLASSE17:varstring(10)");
			ref.setSeparator(Pattern.compile(","));
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("DICHIARAZIONI", "f:\\demo\\libreria_dati");	
		
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("WORK.DS760", ref);  
			datastep.execute();
			
			SortStep sortstep2 = factory_step.createSortStep ("DICHIARAZIONI.CATA_ORD", "WORK.DS760");
			sortstep2.setVariablesToSort("CLASSE2 desc, REDDITI2 asc" );  
			sortstep2.execute(); 
			
			FileStep filestep = factory_step.createFileStep("F:\\demo\\dati_testo\\stampa_redditi.txt", "DICHIARAZIONI.CATA_ORD");
			filestep.printf("%s %s" ,"CLASSE2", "REDDITI2" ); 
			filestep.execute(); 
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}