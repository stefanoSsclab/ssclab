package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.ref.Input;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;

import java.sql.Connection;
import java.sql.DriverManager;

import oracle.jdbc.pool.OracleDataSource;

public class Example08 {
	
	/**
	* Parole chiave : 
	* 
	* 1) LETTURA E SCRITTURA DI DATI DA DATABASE ESTERNI TRAMITE QUERY SQL 
	*/
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("PGSQL", connPgs());
			factory_libraries.addLibrary("DB_ORACLE", connOracle());
			
			
			Library lib_oracle=factory_libraries.getLibrary("DB_ORACLE");
			Input input1=lib_oracle.getInputFromSQLQuery(
					     "select a.neg_nome, a.neg_indiri, b.ord_cod, b.ord_data from negozi a, ordini b "+
					     "where a.neg_cod=b.neg_cod");
			
		    // DA DB A DB
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("PGSQL.new_ordini9",input1);
			//SE E' A TRUE CREA LA TABELLA se false no 
			//datastep.setAppendOutput(false);
			datastep.setMaxObsRead(8);
			datastep.execute();

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
		Connection conn = DriverManager.getConnection(url);
		return conn;
	}
}
