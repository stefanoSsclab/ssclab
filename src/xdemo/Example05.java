package xdemo;

import java.util.regex.Pattern;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

public class Example05 {
	
	/**
	* Parole chiave : 
	* 
	* 1) LETTURA DI FILE DI TESTO CON SPECIFICO SEPARATORE E CARATTERE PER LA GESTIONE DEI MISSING 
	* 2) ESECUZIONE DI CODICE NEL PASSO DI DATA  (FUNZIONI getObs(),delete(),replaceNull()) in() e notIn() 
	* 3) LETTURA CONDIZIONATA DI DATI DA UNA SORGENTE INPUT
	* 4) SCRITTURA DI FILE TESTO DI OUTPUT CON SELEZIONE DI UN CARATTERE SPECIFICO PER I MISSING
	* */
	
	public static void main(String[] args) throws Exception {
		
		Session session =  Context.createNewSession();
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_102\\jre");
		
		InputFile ref = new InputFile("d:/ssc/demo_info/dati_testo/dati_con_missing.txt");
		ref.setInputFormat( "A:fixstring(20), B:varstring(5), C:varstring(30), ETA:varstring(100), GIORNO:date(gg-mm-aa),REDDITO:double");
		ref.setSeparator(';').setMissingValue("."); 
		
		session.addLibrary("LIBRERIA1", "d:/ssc/demo_info/libreria_dati");
		
		DataStep datastep=session.createDataStep("LIBRERIA1.DATI_CON_MISS",ref); 
		
		int anni_interesse=15;
		double tasso_interesse=1.05;
		
		String java_source_code=
								"      if(REDDITO==null || REDDITO > 100) delete();"+
								"      if(getObs()==6) log(\"ECCO il  reddito del sesto record =\"+REDDITO);"+
		                        "      A= A+\"ciccia\";"+
		                        "      for(int a=0;a<"+anni_interesse+";a++) {"+
		                        "         REDDITO= replaceNull(REDDITO,0.0)*"+tasso_interesse+";"+
		                        "      }";
		                     
		datastep.setSourceCode(java_source_code);
		datastep.execute();
		
		FileStep filestep=session.createFileStep("d:/ssc/demo_info/output/stampa_dati_con_missing.txt", "LIBRERIA1.DATI_CON_MISS");
		filestep.setWhere(" in(C,\"d\", null) || notin(GIORNO,\"02/04/2012\",\"03-01-2012\") ;");
		filestep.printf("%2s; %15s; %20s; %10.2f", "C","GIORNO","A","REDDITO");
		filestep.setOutputMissing("@");
		filestep.execute();
	
		//session.close();
		
	}
}
