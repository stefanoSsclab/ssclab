package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.DataStep;

import java.sql.Connection;

import oracle.jdbc.pool.OracleDataSource;


/**
* Parole chiave : 
* 
* 1) LIBRERIE COME RIFERIMENTI A DATABASE
* 2) DROP (KEEP) DI VARIABILI IN UN PASSO DI DATA 
* 3) LETTURA DI UNA TABELLA DAL DB 
* 
*/

public class Example03 {
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		Session session = Context.createNewSession();
		try {
			
			session.addLibrary("PIPPO", "e:\\fmt_stat_dati");
			session.addLibrary("DB_ORACLE", connOracle());
		
		    // DA DATASET A DB
			DataStep data_step = session.createDataStep("DB_ORACLE.A24", "PIPPO.TANTI");
			data_step.setDropVarOutput("GIORNO");   
			data_step.setMaxObsRead(88);    
			data_step.execute();   
			
		} 
		finally {
			session.close();
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
}
