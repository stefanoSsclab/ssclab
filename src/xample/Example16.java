package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;

import java.util.regex.Pattern;

public class Example16 {
	
	/**
	* Parole chiave : 
	* 
	* 1) Recupero di metainformazioni da una libreria
	* 2) Dichiarazione di nuove variabili non temporanene
	* 3) Leggere un oggetto ritornato dal passo di data 
	*/
	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			InputFile ref = new InputFile("E:\\fmt_stat_prove\\punto_miss.txt");
			ref.setInputFormat( "A:fixstring(10), B:varstring(5), C:varstring(30), ETA:varstring(100), GIORNO:date(gg-mm-aa),REDDITO:double");
			ref.setSeparator(Pattern.compile(";")); 
			ref.setMissingValue(".");
			
	
			FactoryLibraries factory_libraries=session.getFactoryLibraries(); 
			Library lib= factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			for (String name:lib.getListTable()) {
				System.out.println("Tabelle della libreria "+lib.getName()+":"+name);
			}
			
			FactorySteps factory_step=session.getFactorySteps();
			DataStep datastep=factory_step.createDataStep("PIPPO.DATI_MISS",ref); 
			datastep.declareNewVariable("retain CICLI_TOT:int");
			String java_source_code=
								    "if(getObs()==1) CICLI_TOT=0;"+
			                        "      for(int a=0;a<10;a++) {"+
			                        "         REDDITO= replaceNull(REDDITO,0.0)*1.05;"+
			                        "         CICLI_TOT=CICLI_TOT+1;"+    
			                        "      }"+
			                        "     setReturnObject(CICLI_TOT);   ";  //NON bello , viene eseguito ad ogni passo
			datastep.setSourceCode(java_source_code);
			datastep.setDropVarOutput("CICLI_TOT");
			Object cicli=datastep.execute();
			
			System.out.println("CICLI TOTALI:"+cicli);
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
