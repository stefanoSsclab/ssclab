package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;

import java.sql.Connection;
import java.sql.DriverManager;

import oracle.jdbc.pool.OracleDataSource;

public class Example06 {
	
	/**
	* Parole chiave : 
	* 
	*  1) LETTURA E SCRITTURA DI DATI SU DATABASE ESTERNI 
	*/
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		
		Session session =  Context.createNewSession();
		session.addLibrary("PGS", connPgs());
		Library lib_ora= session.addLibrary("ORACLE", connOracle());
		
		/*String DDL="CREATE TABLE ALL_TYPE ( INT_VAR INTEGER, SMALL_VAR INTEGER,  CHARA_VAR CHAR(6), "+
		                                   "VARCHAR_VAR VARCHAR(22), FLOAT_VAR FLOAT, DEC_VAR NUMERIC(18,2), "+
				                           "DEC2_VAR NUMERIC, NUM_VAR NUMERIC(22,6), NUM2_VAR NUMERIC, "+
		                                   "REAL_VAR REAL, DATE_VAR DATE )";
		lib_ora.executeSQLUpdate(DDL); */
		
	    // DA DB A DB
		
		DataStep datastep = session.createDataStep("ORACLE.ALL_TYPE","PGS.TUTTI_TIPI");
		datastep.setDropVarOutput("BIG_VAR","TIME_VAR" );
		//datastep.setAppendOutput(true);
		datastep.execute();

		//vale per tutti i tipi di librerie 
		lib_ora.dropTable("ALL_TYPE");
		
		session.close();

	}


	private static Connection connOracle() throws Exception {
		OracleDataSource ods = new OracleDataSource();
		String URL = "jdbc:oracle:thin:@//192.168.243.130:1521/XE";
		ods.setURL(URL);
		ods.setUser("system");
		ods.setPassword("alex655321");
		return ods.getConnection();
	}

	private static Connection connPgs() throws Exception {
		String url = "jdbc:postgresql://192.168.243.130/postgres?user=postgres&password=alex655321";
		return DriverManager.getConnection(url); 
	}
}

