package xample;

import java.util.regex.Pattern;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;
import it.ssc.step.SortStep;

public class Example24 {
	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
		try {
			
			session = Context.createNewSession();
			
			InputFile ref = new InputFile("e:\\fmt_stat_prove\\ds760.csv");
			ref.setInputFormat("REDDITI1 - REDDITI71 :double, CLASSE1 - CLASSE17:varstring(10)");
			ref.setSeparator(Pattern.compile(","));
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("GENTE", "e:\\fmt_stat_dati2");	
		
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("WORK.DS760", ref);  
			datastep.setKeepVarOutput("CLASSE2","REDDITI2" );
			datastep.execute();
			
			SortStep datastep2 = factory_step.createSortStep ("GENTE.CATA_ORD", "WORK.DS760");
			datastep2.setVariablesToSort("CLASSE2 , REDDITI2 " ); 
			datastep2.execute(); 
			
			FileStep filestep = factory_step.createFileStep("e:\\fmt_stat_prove\\stampa_gente.txt", "GENTE.CATA_ORD");
			filestep.printf("%s %s" ,"CLASSE2", "REDDITI2" );
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}