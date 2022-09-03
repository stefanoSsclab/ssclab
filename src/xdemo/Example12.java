package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.step.CrossJoinStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

public class Example12 {  //NON FARE 

	/**
	* Parole chiave : 
	* 
	* 1) Cross join tra tabelle di qualsiasi natura 
	*/
	public static void main(String[] args) throws Exception {
		
		Session session =  Context.createNewSession();
		
		session.addLibrary("AZIENDA", "f:\\demo\\libreria_dati");
	
		CrossJoinStep cross=session.getFactorySteps().createCrossJoinStep("AZIENDA.JOIN_RISORSE");  
		cross.setInputDataForCross("AZIENDA.IMPIEGATI","AZIENDA.SEGRETARIE");  
		cross.setWhere("NOME.equals(NOME2) &&  NOME.startsWith(\"stef\") ; ");
		cross.execute();
		
		
		FileStep filestep = session.createFileStep("f:\\demo\\dati_testo\\stampa_join.txt", "AZIENDA.JOIN_RISORSE");
		filestep.printf("%s %s %s ", "NOME","COGNOME","NOME2");
		filestep.execute();
	  
	
		if (session != null) session.close();
		
	}
}
