package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.util.regex.Pattern;

public class Example22 {
	
	public static void main(String[] args) throws Exception {
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			InputFile ref = new InputFile("E:\\fmt_stat_prove\\indirizzi.txt");
			ref.setInputFormat( "nome:fixstring(20), cognome:varstring(15), indirizzo:varstring(30), cap:varstring(5)");
			ref.setSeparator(Pattern.compile(";")); 
					
	
			FactoryLibraries factory_libraries=session.getFactoryLibraries(); 
			factory_libraries.addLibrary( "GEOREF","E:\\fmt_stat_dati\\"); 
			
			
			FactorySteps factory_step=session.getFactorySteps();
			DataStep datastep=factory_step.createDataStep("GEOREF.dati_geo",ref); 
			//le newvar stanno sempre inizializzate a null, anche se boolean - > Boolean
			datastep.declareNewVariable("LATI:Double,LONGI:Double");
			//questi non sono campi, non vengono convertiti in maiuscolo, ma sono sottoposti alle regole degli 
			//attributi java 
			datastep.declareJavaAttribute("test.GeoRef obj_geo;");
			String java_source_code=
								    "if(obj_geo==null)   { "+
								    "     obj_geo= new test.GeoRef();"+
								    "     log(\"inizializzato test.GeoRef\");"+
								    "  }"+
			                        "obj_geo.setGeoRef(INDIRIZZO,CAP);"+
			                        "LATI=obj_geo.getLati();"+
			                        "LONGI=obj_geo.getLongi();";
			                                        
			datastep.setSourceCode(java_source_code);
			datastep.execute();
			
			FileStep filestep = factory_step.createFileStep("e:\\fmt_stat_prove\\geo_ref_indizzi.txt", "GEOREF.dati_geo");
			filestep.printf("%s %s  ->%f ->%f", "COGNOME","INDIRIZZO","LATI","LONGI");
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}

}
