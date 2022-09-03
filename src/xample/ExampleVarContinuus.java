package xample;

import java.util.logging.Logger;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.datasource.DataSource;
import it.ssc.log.SscLogger;
import it.ssc.ref.InputString;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;

/**
 * Parole Chiave : 
 * 
 * 1) SESSIONE SSC
 * 2) FILE DI INPUT IN FORMATO STRINGA
 * 3) FORMATI DI INPUT 
 * 4) LIBRERIA 
 * 5) DATASET 
 * 6) FILE TESTO DI OUTPUT 
 * 7) FORMATO DI OUTPUT 
 * 
 */


public class ExampleVarContinuus {

	public static void main(String[] args) throws Exception {


		Session session  = Context.createNewSession();

		String dati=

				"Atefanin scarioli 070168   f 1.78 M  "  +
				"Giuseppe fernando 071271   t 1.64 M  "  +
				"Badiac omaneci    031143   f 1.64 F  "  +
				"Stefano Scarioli  072068   f 1.78 M  "  +
				"Giuseppe fernanda 030571   t 1.64 M  ";  

		try {
			InputString input = new InputString(dati);
			input.setIndexForNewLine(37);
			input.setInputFormat("NOME2-NOME3:fixstring(8),  NASCITA:date(ggmmaa), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar[37]");

			session.addLibrary("GENTE", "c:\\appo");	

			DataStep data_step = session.createDataStep("GENTE.PERSONE", input);  
			data_step.execute();

			FileStep file_step = session.createFileStep("C:\\ssc\\fmt_stat_prove\\stampa_gente.txt", "GENTE.PERSONE");
			file_step.printf("%12s %15s %tD %07.2f %S (sposato:%6$S)", "NOME2", "NOME3", "NASCITA", "ALTEZZA","SESSO", "CELIBE");
			file_step.execute();

			Logger log=SscLogger.getLogger();
			DataSource data=session.createDataSource(input);

			while(data.next()) { 
				String format=String.format("%1$td %1$tm %1$ty", data.getGregorianCalendar("NASCITA"));
				String pim=data.<String,String>getValueReduce( x -> "Sign. "+x,"NOME2");
				log.info("valore primo nome:"+pim);
				log.info("valore secondo nome:"+format);
			}
			data.close();
		} 
		finally {
			session.close();
		}
	}
}

