package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;

import java.sql.Connection;
import java.sql.DriverManager;

public class Example26 {
	
public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		Session session = null;
		try {
			session = Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("PIPPO", "e:\\fmt_stat_dati");
			Library lib_db2=factory_libraries.addLibrary("DB2", connDB2());
		
		    // DA DATASET A DB
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("DB2.A49", "PIPPO.TANTI");
			datastep.setDropVarOutput("GIORNO");   
			datastep.setMaxObsRead(88);   
			datastep.execute();   
			
			lib_db2.renameTable("AIO2", "A47");
			
			for(String name_tab:lib_db2.getListTable()) {
				System.out.println("TABLE:"+name_tab);
			}
			lib_db2.dropTable("AIO2");
			
			
		} 
		finally {
			if (session != null) session.close();
		}
	}


	private static Connection connDB2() throws Exception { 
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		String url = "jdbc:db2://localhost:50000/SAMPLE:" +  "user=db2admin;password=ALEX655321;";                                     // Set URL for data source
		return DriverManager.getConnection(url); 

	}
}
