package xample;

import java.util.GregorianCalendar;
import java.util.logging.Logger;
import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.datasource.DataSource;
import it.ssc.log.SscLogger;
import it.ssc.ref.InputRows;
import it.ssc.step.DataStep;
import it.ssc.step.FileStep;

public class Example27 {
	
	public static void main(String[] args) throws Exception {  
		
		
		Session session  = Context.createNewSession();
		
		try {
			
			InputRows input = new InputRows();  
		
			input.setInputFormat("NOME2-NOME3:fixstring(8),  NASCITA:date(object), CELIBE:boolean, ALTEZZA:float, SESSO:singlechar");
			input.addRow("Atefanin","scarioli",null, true, 12.6,'c'); 
			input.addRow("Giuseppe","fernando",new GregorianCalendar(), null, 12.6f,'B');  
			
			session.addLibrary("GENTE", "d:\\appo");	   
		
			DataStep data_step = session.createDataStep("GENTE.PERSONE", input);  
			data_step.execute(); 
			//il progetto va avanti .... e funziona, bisogna cercare nuovi stimoli
			
			FileStep file_step = session.createFileStep("d:\\appo\\stampa_gente.txt", "GENTE.PERSONE");
			file_step.printf("%12s %15s %tD %07.2f %S (sposato:%6$S)", "NOME2", "NOME3", "NASCITA", "ALTEZZA","SESSO", "CELIBE");
			file_step.execute();
			
			Logger log=SscLogger.getLogger(); 
			DataSource data=session.createDataSource(input);
			
			while(data.next()) { 
				String format=String.format("%1$td %1$tm %1$ty", data.getGregorianCalendar("NASCITA")); 
				log.info("valore secondo nome:"+format); 
				
			}
			data.close(); 
		} 
		finally {
			 if(session!=null) session.close();
		}
	}
}
