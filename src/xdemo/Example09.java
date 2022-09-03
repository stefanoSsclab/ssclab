package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.context.exception.InvalidSessionException;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;



public class Example09 {
	
	/**
	* Parole chiave : 
	* 
	* 1) DICHIARAZIONE DI NUOVE VARIABILI NON TEMPORANEE (di cui alcune con opzione RETAIN) 
	*/
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException, InvalidSessionException {

	
		
		Session session = Context.createNewSession();
		System.setProperty("java.home", "C:\\java\\jre");
		
		InputFile ref = new InputFile("C:/ssc/demo_info/dati_testo/gente.txt");
		ref.setInputFormat("NOME:fixstring(20), cognome:varstring(25), NASCITA:date(gg/mm/aaaa), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar");

		session.addLibrary("GENTE", "C:\\ssc\\dati");

		DataStep datastep = session.createDataStep("GENTE.PERSONE", ref); 
		
		//fanno parte del pgm e vengono salvate sul ds di destinazione (se non droppate)
		datastep.declareNewVariable("retain PAROLE:varstring(30), OBS:long");
		
		// la variabile PAROLE viene valorizzata solo quando getObs()==6
		datastep.setSourceCode(
				
				"if(getObs()==6) PAROLE=\"PAPERINO\";"+
				"log(getObs()+PAROLE); "+ 
				"OBS=getObs();");
		
		datastep.execute();

		FileStep filestep = session.createFileStep("C:/ssc/demo_info/output/stampa_gente_retain.txt", "GENTE.PERSONE");
		filestep.printf("%12s %15s  %7.2f %02d (parole:%s)","COGNOME", "NOME", "ALTEZZA", "OBS", "PAROLE");
		
		//Stampa tutte le variabili senza dover dichiare ogni singola var 
		//filestep.printAllVar();
		filestep.execute();
	
	
		session.close();
		
	}
}
