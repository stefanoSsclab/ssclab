package xample;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.Library;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;

/**
 * Parole chiave : 
 * 
 * 1) SESSIONE FMT
 * 2) FILE TESTO DI INPUT
 * 3) FORMATODI INPUT 
 * 4) LIBRERIA 
 * 5) DATASET 
 * 6) FILE TESTO DI OUTPUT 
 * 7) FORMATO DI OUTPUT 
 * 8) RECUPERO DELLA LIBRERIA ALLOCATA 
 *
 */


public class Example01 {
	
	public static void main(String[] args) throws Exception {
		
		Session session = Context.createNewSession();
		try {
			
			InputFile input = new InputFile("C:\\ssc\\file_ssc\\ssc_txt\\tipi_diversi.txt");
			input.setInputFormat("NOMe2-NOMe3:fixstring(2),  NASCITa:date(gg/mm/aaaa), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar[37]{null}");
			
			Library lib=session.addLibrary("GENTE", "C:\\ssc\\file_ssc\\dati_pl");	
		
			DataStep data_step = session.createDataStep("GENTE.PERSONE", input);  
			data_step.execute();
			
			DataStep datastep2 = session.createDataStep("GENTE.PERSONE2", "GENTE.PERSONE");  
			datastep2.execute();
			
			//FileStep file_step = session.createFileStep("e:/fmt_stat_prove/stampa_gente.txt", "GENTE.PERSONE");
			FileStep file_step = session.createFileStep("C:\\ssc\\file_ssc\\ssc_txt\\stampa_gente.txt", input);
			file_step.printf("%12s %15s %tD %07.2f %S (sposato:%6$S)", "NOME2", "NOME3", "NASCITA", "ALTEZZA","SESSO", "CELIBE")
			.execute();
			
			lib.dropTable("PERSONE");   
			lib.renameTable("PERSONE", "PERSONE2"); 
				
		} 
		finally {
			session.close();
		}
	}
}
