package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;
import java.util.regex.Pattern;

/**
* Parole chiave : 
* 
* 1) DIVERSO FORMATO DI INPUT DELLA DATA
* 2) DICHIARAZIONE DI DLM (SEPARATORE )
* 3) APPEND DEI DATI 
* 4) UTILIZZO LIBRERIA DI WORK
* 5) IMPOSTAZIONE DEL PARAMETRO MAX_OBS (NUMERO DI OSSERVAZIONI LETTE o SCRITTE)
*/

public class Example02 {

	
	public static void main(String[] args) throws Exception {
		
			Session session  =  Context.createNewSession();

			InputFile file = new InputFile("d:/ssc/demo_info/dati_testo/molti_record.txt");
			file.setInputFormat("A:varstring(12), B:varstring(5), C:varstring(30), D:varstring(10), GIORNO:date(gg-mm-aa), REDDITO:double");
			//il separatore puo' essere una espressione regolare 
			file.setSeparator(Pattern.compile(";"));
			
			session.addLibrary("PIPPO", "d:/ssc/demo_info/libreria_dati");

			DataStep datastep = session.createDataStep("pippo.MOLTI_RECORD", file);
			datastep.setAppendOutput(false);
			datastep.execute();
			
			
			// DA DATASET A DATASET IN WORK , SOLO 66 RECORD 
			DataStep datastep2 = session.createDataStep("work.MOLTI_RECORD", "pippo.MOLTI_RECORD");
			datastep2.setMaxObsRead(66);
			datastep2.execute();

			FileStep filestep = session.createFileStep("d:/ssc/demo_info/output/stampa_molti_record.txt", "work.MOLTI_RECORD");
			filestep.printf("%s %s %s %s %7.2f", "A", "B", "C", "D","REDDITO");
			filestep.execute();
			
			//chiude la sessione e svuota la work 
			session.close();
		
	}
}
