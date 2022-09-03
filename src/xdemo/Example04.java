package xdemo;

import java.util.logging.Logger;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.Library;
import it.ssc.log.SscLogger;
import it.ssc.ref.Input;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;

public class Example04 {
	
	/**
	* Parole chiave : 
	* 
	* 1) LETTURA AVANZATA DI FILE DI TESTO 
	* 2) RECUPERO DELLE METAINFORMAZIONI DA UNA SORGENTE DI DATI FILE O DATASET 
	* */


	public static void main(String[] args) throws Exception {
		/**
		 * 
		 * Il formato di input gestisce i salti record , input a colonna , 
		 * il salto di colonne o il leggere lo 
		 * stesso  record logico presente su piu‘ righe fisiche: <br>
		 * 
		 * a) @@ fa leggere sullo stesso pdv i dati della righa successiva del
		 * file. Naturalmente non ha senso metterlo alla fine dell'istruzioni di
		 * importazione. In quanto non ci sarebbero piu' campi da leggere.
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
			Logger log=SscLogger.getLogger();
			
			InputFile ref = new InputFile("c:/ssc/demo_info/dati_testo/salta_record.txt");
			ref.setInputFormat("CAP:fixstring(5)[1-5], FISCALE:varstring(17)[6-21], CITTA:varstring(50)[22-63], PROV:varstring(2), SUB_CF:varstring(2)[12-13] ,@@, VIA:varstring(64)[1-63], CIVICO:varstring(2),@@, DATA_NASCITA:date(gg-mm-aaaa),@64, REDDITO:int,#2");
			
			Library lib=session.addLibrary("LIBREF", "c:/ssc/demo_info/libreria_dati");
		
			DataStep datastep = session.createDataStep("LIBREF.TABSALTA_REC", ref); 
			datastep.execute();
			
			Input tabella=lib.getInput("TABSALTA_REC");
			
			int num_colonne2=tabella.getColumnCount();
			for(int a=1;a<=num_colonne2;a++) {
				log.info("-Nome campo:"+tabella.getColumnName(a));
				log.info("-Lunghezza:"+tabella.getField(a).getLenght());
			}
			
			FileStep filestep = session.createFileStep("c:/ssc/demo_info/output/print_salta_record.txt", tabella);
			filestep.printf("%-19s %S", "FISCALE","SUB_CF"); 
			filestep.execute();
			
			session.close();
		
	}
}
