package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.Library;
import it.ssc.ref.Input;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

public class Example04 {
	
	/**
	* Parole chiave : 
	* 
	* 1) LETTURA AVANZATA DI FILE DI TESTO 
	* 2) RECUPERO DELLE METAINFORMAZIONI DA UNA SORGENTE DI DATI 
	* */


	public static void main(String[] args) throws Exception {
		/**
		 * 
		 * Il formato di input gestisce i salti record , input a colonna , 
		 * il salto di colonne e il leggere lo 
		 * stesso  record logico presente su piu‘ righe fisiche: <br>
		 * 
		 * a) @@ fa leggere sullo stesso pdv i dati della righa successiva del
		 * file. Naturalmente non ha senso metterlo alla fine dell'istruzioni di
		 * importazione .
		 * @@ equivale anche a  #1 <br>
		 * 
		 * b) @n fa puntare il cursore alla colonna n sul riga fisica
		 * attualmente caricata dal file.
		 * 
		 * <br>
		 *    c) #n fa puntare alla n-esima successiva riga del file di input.
		 *    Puo essere messo in mezzo alla istruzione di importazione andando
		 *    avanti nelle operazioni di lettura ; se invece messo alla fine fa
		 *    puntare alla n-esima riga e inizia un altro record . Se metto ad
		 *    esempio alla fine dell'istruzione di importazione #1, punta alla
		 *    riga successsiva, ma poi inizia il record successivo alla riga in
		 *    quanto l'istruzione di input essendo terminata passa al record
		 *    successivo. Puo essere messo in mezzo o alla fine.
		 */
		
		
		Session session =  Context.createNewSession();
		try {
			
			InputFile file_input = new InputFile("E:\\fmt_stat_prove\\tipi_colonne.txt");
			file_input.setInputFormat("CAP:fixstring(5)[1-5], FISCALE:varstring(17)[6-21], CITTA:varstring(50)[22-63], PROV:varstring(2), SUB_CF:varstring(2)[12-13] ,@@, VIA:varstring(64)[1-63], CIVICO:varstring(2),@@, DATA_NASCITA:date(gg-mm-aaaa),@64, reddito:int,#2");
		
			int num_colonne=file_input.getColumnCount();
			for(int a=1;a<=num_colonne;a++) {
				System.out.println("Nome campo:"+file_input.getColumnName(a));
				//ma che lunghezza e', della variabile contenitore o del testo letto ????
				System.out.println("Lunghezza:"+file_input.getField(a).getLenght());
			}
			
			
			Library lib=session.addLibrary("GENTE", "e:\\fmt_stat_dati");
		
			FactorySteps factory_step = session.getFactorySteps();
			DataStep data_process = factory_step.createDataStep("GENTE.colonne", file_input); 
			data_process.execute();
			
			Input dataset=lib.getInput("colonne"); 
			
			int num_colonne2=dataset.getColumnCount();
			for(int a=1;a<=num_colonne2;a++) {
				System.out.println("-Nome campo:"+dataset.getColumnName(a));
				System.out.println("-Lunghezza:"+dataset.getField(a).getLenght());
			}
			
			FileStep file_process = factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_colonne.txt", "GENTE.colonne");
			file_process.printf("%19s", "FISCALE");  
			file_process.execute();
			
		} 
		finally {
			session.close();
		}
	}
}
