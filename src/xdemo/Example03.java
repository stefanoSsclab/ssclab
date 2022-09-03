package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.Library;
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
			
			session.addLibrary("LIB_DATI", "c:/ssc/demo_info/libreria_dati");
			Library lib_ora=session.addLibrary("DB_ORACLE", connOracle());
			
			lib_ora.dropTable("A7");
			
		    // DA DATASET A DB
			DataStep data_step = session.createDataStep("DB_ORACLE.A7", "LIB_DATI.MOLTI_RECORD");
			data_step.setDropVarOutput("GIORNO");   
			data_step.execute();   
			
			//chiude le connessioni e svuota la work
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
}
