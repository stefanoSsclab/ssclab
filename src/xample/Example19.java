package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.Input;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.util.regex.Pattern;

public class Example19 {
	
	/**
	* Parole chiave : 
	* 
	* 1) Memorizzazione di un dataset in memoria
	* 
	* */

public static void main(String[] args) throws Exception {
		
		
		Session session = null;
		try {
			session =  Context.createNewSession();

			InputFile ref = new InputFile("E:\\prove_ssc\\punto_tanti.txt");
			ref.setInputFormat("A:varstring(12), B:varstring(5), c:varstring(30), D:varstring(100), GIORNO:date(gg-mm-aa), REDDITO:double");
			ref.setSeparator(Pattern.compile(";"));

			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("PIPPO", "E:\\prove_ssc");

			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createMemoryStep( ref);
			//l'istruzione seguente non ha effetti in memoria 
			datastep.setAppendOutput(true);
			datastep.execute();
			
			Input memory=datastep.getDataRefCreated();
			
			// DA DATASET A DATASET IN WORK 
			DataStep datastep2 = factory_step.createDataStep("WORK.TANTI", memory); 
			//datastep2.setMaxObsRead(66);
			datastep2.execute();
			memory.close();
			
			
			FileStep filestep = factory_step.createFileStep("E:\\prove_ssc\\stampa_tanti.txt", "WORK.TANTI");
			filestep.printf("%s %s %s %S %7.2f", "A", "B", "C", "D","REDDITO");
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}

	
}
