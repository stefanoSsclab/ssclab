package xample;

import java.util.regex.Pattern;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.CrossJoinStep;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

public class Example12 {

	/**
	* Parole chiave : 
	* 
	* 1) Cross join tra tabelle di qualsiasi natura 
	*/
	public static void main(String[] args) throws Exception {
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			InputFile ref1 = new InputFile("e:\\fmt_stat_prove\\tipi_simili.txt");
			ref1.setInputFormat("NOME:fixstring(20), COGNOME:varstring(25), NASCITA:date(gg/mm/aaaa), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar[37]");
		
			InputFile ref2 = new InputFile("E:\\fmt_stat_prove\\punto_tanti.txt");
			ref2.setInputFormat("A:fixstring(12), B:varstring(5), C:varstring(30), D:varstring(100), GIORNO:date(gg-mm-aa), REDDITO:double");
			ref2.setSeparator(Pattern.compile(";"));
			
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("GENTE", "e:\\fmt_stat_dati");
		
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("GENTE.SEGRETARIE", ref1);
			datastep.execute();
			
			
			DataStep datastep2 = factory_step.createDataStep("GENTE.IMPIEGATI", ref2);
			datastep2.execute();
			
			
			CrossJoinStep cross=factory_step.createCrossJoinStep("GENTE.JOIN");  
			cross.setInputDataForCross("GENTE.IMPIEGATI","GENTE.SEGRETARIE");  
			cross.setWhere("NOME.equals(A); ");
			cross.execute();
			
			
			FileStep filestep = factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_join.txt", "GENTE.JOIN");
			filestep.printf("%s %S %s %S", "NOME","COGNOME","A","B");
			filestep.execute();
		  
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
