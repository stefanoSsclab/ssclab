package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.step.DataStep;
import oracle.jdbc.pool.OracleDataSource;
import java.sql.Connection;
import java.sql.DriverManager;


public class Example22 {
	
	public static void main(String[] args) throws Exception {
		
		Session session =   Context.createNewSession();
		System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_102\\jre");
					
		session.addLibrary( "LOCALE","d:/ssc/demo_info/libreria_dati"); 
		session.addLibrary( "ORACLE",connOracle()); 
		session.addLibrary( "POSTGRES",connPgs()); 
		
		//scarico la tabella dal DB oracle
		DataStep datastep=session.createDataStep("LOCALE.anagrafica_destinatari","ORACLE.anagrafica_destinatari"); 
		datastep.execute();
		
		//elaboro in locale l'anagrafica destinatari 
		datastep=session.createDataStep("LOCALE.anagrafica_destinatari_georef","LOCALE.anagrafica_destinatari"); 
			//dichiaro le due nuove variabili in cui inserire LATITUDINE E LONGITUDINE
			datastep.declareNewVariable("LATITUDINE:Float,LONGITUDINE:Float");
			
			//Inizializzo e uso  l'oggetto geocoder che a fronte dell'INDIRIZZO E CITTA recupera le coordinate 
			datastep.declareJavaAttribute("geocodingFree.GeocodingEnterprise geocoder;");
			String java_source_code=
								    "if(geocoder==null)   { "+
								    "     geocoder= new geocodingFree.GeocodingEnterprise();"+
								    "  }"+
			                        "geocodingFree.Coordinate coordinate =geocoder.georeferenzia(INDIRIZZO+\",\"+CITTA);"+
			                        "LATITUDINE =coordinate.getLatitudine();"+
			                        "LONGITUDINE=coordinate.getLongitudine();";                                  
			datastep.setSourceCode(java_source_code);
			
		datastep.execute();
		
		//carico su DB PostGres la nuova tabella con le latitudini e le longitudini
		datastep=session.createDataStep("POSTGRES.anag_destinatari_georef2","LOCALE.anagrafica_destinatari_georef"); 
		datastep.execute();
		
		session.close();
	
	}
	
	
	
	
	
	
	
	
	// http://maps.google.com/maps/api/geocode/xml?address=Via+ivano+Scarioli%2C+00045+Genzano+di+Roma&sensor=false&language=it
	
	
	private static Connection connOracle() throws Exception {
		OracleDataSource ods = new OracleDataSource();
		String URL = "jdbc:oracle:thin:@//scarioli-life:1521/XE";
		ods.setURL(URL);
		ods.setUser("system");
		ods.setPassword("alex655321");
		return ods.getConnection();
	}

	private static Connection connPgs() throws Exception {
		String url = "jdbc:postgresql://scarioli-life/postgres?user=postgres&password=alex655321";
		return DriverManager.getConnection(url); 
	}

}
