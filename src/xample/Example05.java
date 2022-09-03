package xample;

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
	* 2) ESECUZIONE DI CODICE DINAMICO NEL PASSO DI DATA  (FUNZIONI getObs(),delete(),replaceNull())
	* 3) LETTURA CONDIZIONATA DI DATA DA UNA SORGENTE INPUT
	* 4) SCRITTURA DI FILE TESTO DI OUTPUT CON SELEZIONE DI UN CARATTERE SPECIFICO PER I MISSING
	* */
	
	public static void main(String[] args) throws Exception {
		
		Session session =  Context.createNewSession();
		System.setProperty("java.home", "C:\\Java\\jdk1.8.0_131\\jre");
		try {
			
			InputFile file = new InputFile("C:\\ssc_project\\ssc\\fmt_stat_prove\\punto_miss.txt");
			file.setInputFormat( "A:fixstring(10), B:varstring(5), C:varstring(30), ETA:varstring(100), GIORNO:date(gg-mm-aa),REDDITO:double");
			file.setSeparator(Pattern.compile(";")); 
			file.setMissingValue(".");
			
	
			FactoryLibraries factory_libraries=session.getFactoryLibraries(); 
			factory_libraries.addLibrary( "PIPPO","C:\\ssc_project\\ssc\\dati"); 
			
			FactorySteps factory_process=session.getFactorySteps();
			DataStep data_step=factory_process.createDataStep("PIPPO.DATI_MISS",file); 
			int anni_interesse=15;
			double tasso_interesse=1.05; 
			
			
			String data_process_subcode=
			"      if(REDDITO==null || REDDITO > 100) {  delete(); return;    } "+ 
			"      if(getObs()==6) log(\"reddito=\"+REDDITO);"+
			"      A= A+\"ciccia\";"+
			"      for(int a=0;a<"+anni_interesse+";a++) {"+
			//Se si lascia return : "         REDDITO= (REDDITO)*"+tasso_interesse+";"+
			"         REDDITO= replaceNull(REDDITO,0.0)*"+tasso_interesse+";"+
			"      }";
			                     
			data_step.setSourceCode(data_process_subcode);
			data_step.execute();
			
			FileStep file_step=factory_process.createFileStep("C:\\ssc\\fmt_stat_prove\\stampa_missing.txt", "PIPPO.DATI_MISS");
			file_step.setWhere(" in(C,\"d\", null) || notin(GIORNO,\"02/04/2012\",\"03-01-2012\") ;");
			file_step.setOutputMissing("@");
			file_step.printf("%2s;%15s", "C","GIORNO");
			file_step.execute();
			
		} 
		finally {
			session.close();
		}
	}
}
