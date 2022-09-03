package xdemo;

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

			InputFile ref = new InputFile("f:\\demo\\dati_testo\\molti_record.txt");
			ref.setInputFormat("A:varstring(12), B:varstring(5), c:varstring(30), D:varstring(100), GIORNO:date(gg-mm-aa), REDDITO:double");
			ref.setSeparator(Pattern.compile(";"));

			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("PIPPO", "f:\\demo\\libreria_dati");

			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createMemoryStep( ref);
			datastep.setMaxObsRead(6666);
			datastep.execute();
			
			Input memory=datastep.getDataRefCreated();
			
			
			DataStep datastep2 = factory_step.createDataStep("PIPPO.MEMORY", memory); 
			datastep2.execute();
		
			
			FileStep filestep = factory_step.createFileStep("f:\\demo\\dati_testo\\stampa_molti_record2.txt", memory);
			filestep.printf("%s %s %s %s %7.2f", "A", "B", "C", "D","REDDITO");
			filestep.execute();
			
			memory.close();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}

	
}
