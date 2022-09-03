package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import java.sql.Connection;
import java.sql.DriverManager;

public class TestDb2Inps {
	
public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		Session session = null;
		try {
			session = Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			//factory_libraries.addLibrary("DB2_LOC", "e:\\dati_inps_tbvm");
			factory_libraries.addLibrary("DB2_LOC", connDB2_loc());
			factory_libraries.addLibrary("DB2_INPS", connDB2_inps() );
		
		    // DA DATASET A DB
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep( "DB2_LOC.TBVMSOGLIA","DB2_INPS.TBVMSOGLIA");
			datastep.execute();   
		} 
		finally {
			if (session != null) session.close();
		}
	}

	public static Connection connDB2_inps2() throws Exception { 
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		//String url = "jdbc:db2://SVIL.HOST.INPS:5025/a01db2:currentSchema=PMNT01:" +  "user=WEBS0074;password=VALDONI;";                                     
		// Set URL for data source
		String url = "jdbc:db2://SVIL.HOST.INPS:5025/A01DB2:currentSchema=RM2T3;";                                     
		return DriverManager.getConnection(url,"WEBS0074","VALDONI"); 
	}


	public static Connection connDB2_inps() throws Exception { 
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		//String url = "jdbc:db2://SVIL.HOST.INPS:5025/a01db2:currentSchema=PMNT01:" +  "user=WEBS0074;password=VALDONI;";                                     
		// Set URL for data source
		String url = "jdbc:db2://SVIL.HOST.INPS:5025/A01DB2:currentSchema=PMNT01;";                                     
		return DriverManager.getConnection(url,"WEBS0074","VALDONI"); 
	}
	
	private static Connection connDB2_loc() throws Exception { 
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		String url = "jdbc:db2://localhost:50000/SAMPLE:" +  "user=db2admin;password=ALEX655321;";                                     // Set URL for data source
		return DriverManager.getConnection(url); 

	}
}

