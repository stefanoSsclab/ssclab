package xample;

import java.util.regex.Pattern;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;

public class Example07 {

	/**
	* Parole chiave : 
	* 
	* 1) LETTURA AVANZATA DI FILE DI TESTO (OPZIONI SPACE E NULL) 
	*    L'opzione {null} su campi stringa sostituisce ogni stringa costituita da zero caratteri "" o sequenza di n spazi con null
	*    L'opzione {space} su campi stringa sostituisce ogni null con uno spazio " " 
	*/
	
	public static void main(String[] args) throws Exception {
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			InputFile ref = new InputFile("e:\\fmt_stat_prove\\punto_miss.txt");
			ref.setInputFormat( "A:fixstring(10), B:varstring(5){space}, C:varstring(30){null}, ETA:varstring(100), GIORNO:date(gg-mm-aa),REDDITO:double");
			ref.setSeparator(Pattern.compile(";")); 
			ref.setMissingValue(".");
			
			session.addLibrary( "PIPPO","e:\\fmt_stat_dati"); 
			
			DataStep datastep = session.createDataStep("PIPPO.MISS_NULL", ref);
			datastep.execute();
			
			FileStep filestep = session.createFileStep("e:\\fmt_stat_prove\\stampa_miss_null.txt", "PIPPO.MISS_NULL");
			filestep.printf("%10s %10s %20s ","A", "B","C");
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}


