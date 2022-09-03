package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.Library;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.DataStep;

import java.sql.Connection;
import java.sql.DriverManager;

import oracle.jdbc.pool.OracleDataSource;

public class Example06 {
	
	/**
	*  Parole chiave : 
	* 
	*  1) LETTURA E SCRITTURA DI DATI SU DATABASE ESTERNI 
	*/
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
		
			session.addLibrary("PGS", connPgs());
			Library lib_ora= session.addLibrary("DB_ORACLE", connOracle());
			
			
			//E' POSIZIONALE 
			
			String DDL="CREATE TABLE A7 (  SMALL_VAR SMALLINT, INT_VAR INTEGER, CHAR_VAR CHAR(2), VARCHAR_VAR VARCHAR(22), FLOAT_VAR FLOAT, DEC_VAR NUMERIC(18,2), DEC2_VAR NUMERIC, NUM_VAR NUMERIC(22,6), NUM2_VAR NUMERIC, REAL_VAR REAL, DATE_VAR DATE )";
			lib_ora.executeSQLUpdate(DDL);
		    // DA DB A DB
			
			DataStep datastep = session.createDataStep("DB_ORACLE.A7","PGS.TUTTI_TIPI");
			datastep.setDropVarOutput("BIG_VAR","TIME_VAR" );
			datastep.setAppendOutput(true);
			datastep.execute();

			/*
			for(String name_tab:lib_psg.getListTable()) {
				System.out.println("TABLE:"+name_tab);
			}
			*/
			lib_ora.renameTable("CICCIO","A7");
			lib_ora.dropTable("CICCIO"); 
			
		} 
		finally {
			if (session != null) session.close();
		}
	}


	private static Connection connOracle() throws Exception {
		OracleDataSource ods = new OracleDataSource();
		String URL = "jdbc:oracle:thin:@//localhost:1521/XE";
		ods.setURL(URL);
		ods.setUser("system");
		ods.setPassword("alex655321");
		return ods.getConnection();
	}

	private static Connection connPgs() throws Exception {

		String url = "jdbc:postgresql://localhost/postgres?user=postgres&password=alex655321";
		return DriverManager.getConnection(url);
		 
	}
}

