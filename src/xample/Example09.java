package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.context.exception.InvalidSessionException;
import it.ssc.library.FactoryLibraries;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

public class Example09 {
	
	/**
	* Parole chiave : 
	* 
	* 1) DICHIARAZIONE DI NUOVE VARIABILI NON TEMPORANEE (di cui alcune con opzione RETAIN) 
	*/
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException, InvalidSessionException {

		Session session = null;
		try {
			session = Context.createNewSession();
			System.setProperty("java.home", "C:\\Java\\jdk1.8.0_131\\jre");

			InputFile file = new InputFile("C:\\ssc\\fmt_stat_prove\\tipi_diversi.txt");
			file.setInputFormat("NOME:fixstring(20), cognome:varstring(25), NASCITA:date(gg/mm/aaaa), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar");

			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("GENTE", "C:\\ssc\\dati");

			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("GENTE.PERSONE", file); 
			datastep.declareNewVariable("retain PAROLE:varstring(30), OBS:long");
			
			// la variabile PAROLE viene valorizzata solo quando getObs()==6
			datastep.setSourceCode("if(getObs()==6) PAROLE=\"PAPERINO\";"+
					               "log(getObs()+PAROLE); "+ 
								   "OBS=getObs();");
			datastep.execute();

			FileStep filestep = factory_step.createFileStep("C:\\ssc\\fmt_stat_prove\\stampa_gente.txt", "GENTE.PERSONE");
			filestep.printf("%12s %15s  %7.2f %02d (parole:%s)","COGNOME", "NOME", "ALTEZZA", "OBS", "PAROLE");
			
			//filestep.printAllVar();
			filestep.execute();
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
