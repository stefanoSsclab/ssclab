package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.step.DataStep;
import java.sql.Connection;
import java.sql.DriverManager;




/**
* Parole chiave : 
* 
* 1) LIBRERIE COME RIFERIMENTI A DATABASE
* 2) DROP (KEEP) DI VARIABILI IN UN PASSO DI DATA 
* 3) LETTURA DI UNA TABELLA DAL DB 
* 
*/

public class Example03b {
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
			Session session = Context.createNewSession();
			
			session.addLibrary("LIB_DATI", "d:/ssc/demo_info/libreria_dati");
			session.addLibrary("DB_ORACLE", connOracle());
			
			
			
		    // DA DATASET A DB
			DataStep data_step = session.createDataStep("DB_ORACLE.A9", "LIB_DATI.MOLTI_RECORD");
			data_step.setDropVarOutput("GIORNO");   
			data_step.execute();   
			
			//chiude le connessioni e svuota la work
			session.close();
		
	}


	private static Connection connOracle() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Connection conn = DriverManager.getConnection("jdbc:derby:D:\\ssc\\derby\\46;create=true");
		return conn;
	}
}
