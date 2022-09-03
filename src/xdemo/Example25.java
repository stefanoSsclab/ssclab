package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;
import it.ssc.step.SortStep;
import it.ssc.step.parallel.*;

import java.util.regex.Pattern;

public class Example25 {
	
public static void main(String[] args) throws Exception {
		
		Session session = null;
		try {
			
			session = Context.createNewSession();
			
			final InputFile ref = new InputFile("F:\\demo\\dati_testo\\ds760.csv");
			ref.setInputFormat("REDDITI1 - REDDITI71 :double, CLASSE1 - CLASSE17:varstring(10)");
			ref.setSeparator(Pattern.compile(","));
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("DICHIARAZIONI", "f:\\demo\\libreria_dati");	
		
			 
			DataStep datastep = session.createDataStep("DICHIARAZIONI.DS760_SOLO", "DICHIARAZIONI.CATA_ORD");  
			datastep.setMaxObsRead(100000);
			//datastep.execute();
			
			
			final FactorySteps factory_step = session.getFactorySteps();
			Parallelizable processo = new Parallelizable() {
				
				public void run() throws Exception {
					
					
					SortStep datastep2 = factory_step.createSortStep("DICHIARAZIONI.DS7602_ORD", ref);
					datastep2.setVariablesToSort("CLASSE2 , REDDITI2 ");
					datastep2.setMaxObsRead(10000);
					datastep2.execute();
					
					FileStep filestep = factory_step.createFileStep("F:\\demo\\dati_testo\\stampa_redditi.txt", "DICHIARAZIONI.DS7602_ORD");
					filestep.printf("%s %s" ,"CLASSE2", "REDDITI2" );  
					filestep.execute(); 
					
				}
			};
			
			
			ParallelProcesses processi=new ParallelProcesses(datastep,processo);
			processi.esecute();
			
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
