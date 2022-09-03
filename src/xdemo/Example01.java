package xdemo;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;

/**
 * Parole chiave : 
 * 
 * 1) SESSIONE DI LAVORO
 * 2) INPUT FILE IN FORMATO TESTO
 * 3) FORMATO DI INPUT 
 * 4) LIBRERIA 
 * 5) DATASET 
 * 6) OUTPUT FILE IN FORMATO TESTO
 * 7) FORMATO DI OUTPUT
 *
 */



public class Example01 {
	
	public static void main(String[] args) throws Exception {
		 /*
		 * La classe Context rappresenta la classe che permette di accedere 
		 * all'insieme delle parti costituenti il sistema. Partendo dal contesto  
		 * si generano una o piu' sessioni di lavoro.
		 */
		
		//creo una sessione di lavoro in analogia a quelle SAS , con la sua area di work, etc....
		Session session = Context.createNewSession();
		

		//creo un riferimento ad un file esterno che presenta dati di tipo  testo 
		InputFile file = new InputFile("d:/ssc/demo_info/dati_testo/gente.txt"); 
		
		
		//specifico il formato di input dei dati presenti nel file 
		file.setInputFormat("NOME:varstring(20), COGNOME:varstring(20), NASCITA:date(gg/mm/aaaa), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar");

				
		//alloco una libreria con nome logico "LIB_DATI" che corrisponde al path fisico "f:\\demo\\libreria_dati"
		session.addLibrary("LIB_DATI","d:\\ssc\\demo_info\\libreria_dati");

		
		//creo l'oggetto datastep che ha per input l'oggetto InputFile  ref  e come output il dataset 
		// in formato nativo "PERSONE"  all'interno della  libreria "LIB_DATI"
		DataStep datastep = session.createDataStep("LIB_DATI.PERSONE5", file); 
		
		
		//esegue il passo di data
		datastep.execute();

		//creo un oggetto filestep per scrivere su file in formato testo i dati nativi   del dataset "LIB_DATI.PERSONE"
		FileStep filestep = session.createFileStep("d:\\ssc\\demo_info\\output\\stampa_gente.txt", "LIB_DATI.PERSONE5");
		filestep.printf("%12s %-15s %tD %03.2f %S    (sposato da poco : %s)", "COGNOME","NOME", "NASCITA", "ALTEZZA", "SESSO", "CELIBE");
		filestep.execute();

		//chiude tutto e svuota la work 
		session.close(); 
		
	}
}	
