package xdemo;

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
		
		Session session =  Context.createNewSession();
		session.addLibrary("PGSQL", connPgs());
		Library lib_oracle=session.addLibrary("DB_ORACLE", connOracle());
		
		Input select1=lib_oracle.getInputFromSQLQuery(
				          "select a.neg_nome, a.neg_indiri, b.ord_cod, b.ord_data from negozi a, ordini b "+
				          "where a.neg_cod=b.neg_cod");
		
	    // DA DB A DB
		DataStep datastep = session.createDataStep("PGSQL.NEW_ORDINI4",select1); 
		datastep.setMaxObsRead(8);
		datastep.execute();

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
