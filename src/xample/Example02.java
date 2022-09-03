package xample;

import java.util.Locale;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;



/**
* Parole chiave : 
* 
* 1) DIVERSO FORMATO DI INPUT DATA
* 2) DICHIARAZIONE DI DLM (SEPARATORE )
* 3) APPEND DEI DATI 
* 4) IMPOSTAZIONE DEL PATH DELLA LIBRERIA DI WORK
* 4) UTILIZZO LIBRERIA DI WORK
* 5) IMPOSTAZIONE DEL PARAMETRO MAX_OBS (NUMERO DI OSSERVAZIONI LETTE)
*/

public class Example02 {

	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
	
		try {
			session =  Context.createNewSession();

			InputFile file_input = new InputFile("K:\\ssc\\fmt_stat_prove\\punto_tanti.txt");
			file_input.setInputFormat("A:varstring(12), B:varstring(5), C:varstring(30), D:varstring(100), GIORNO:date(ggmmaa), REDDITO:double");
			file_input.setSeparator(';');

			session.addLibrary("PIPPO", "e:\\fmt_stat_dati");

			DataStep data_step = session.createDataStep("PIPPO.TANTI", file_input);
			data_step.setAppendOutput(false);
			data_step.execute();
			
			// DA DATASET A DATASET IN WORK 
			DataStep data_step2 = session.createDataStep("WORK.TANTI", "PIPPO.TANTI");
			data_step2.setMaxObsRead(66);
			data_step2.execute();

			FileStep file_step = session.createFileStep("e:\\fmt_stat_prove\\stampa_tanti.txt", "WORK.TANTI");
			file_step.printf("%s %s %s %S %7.2f", "A", "B", "C", "D","REDDITO");
			file_step.setLocale(Locale.US);
			file_step.execute();
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
