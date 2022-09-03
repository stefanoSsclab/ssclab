package xdemo;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.ref.InputFile;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.SortStep;

import java.sql.Connection;
import java.util.regex.Pattern;

import oracle.jdbc.pool.OracleDataSource;

public class ZConfronto {
	
	public static void main(String[] args) throws Exception {
		
		Session session  =  Context.createNewSession();

		InputFile ref = new InputFile("f:\\demo\\dati_testo\\molti_record.txt");
		ref.setInputFormat("A:varstring(12), B:varstring(5), C:varstring(30), D:varstring(5), GIORNO:date(gg-mm-aa), REDDITO:double");
		//il separatore puo' essere una espressione regolare 
		ref.setSeparator(Pattern.compile(";"));

		FactoryLibraries factory_libraries = session.getFactoryLibraries();
		factory_libraries.addLibrary("PIPPO", "f:\\demo\\libreria_dati");
		Library lib_ora=factory_libraries.addLibrary("DB_ORACLE", connOracle());

		FactorySteps factory_step = session.getFactorySteps();
		DataStep datastep = factory_step.createDataStep("PIPPO.MOLTI_RECORD", ref);
		datastep.execute();
		
		// DA DATASET A DATASET IN WORK 
		DataStep datastep2 = factory_step.createDataStep("WORK.MOLTI_RECORD", "PIPPO.MOLTI_RECORD");
		datastep2.execute();
		
		
		SortStep sortstep = factory_step.createSortStep ("PIPPO.MOLTI_RECORD_ORD", "PIPPO.MOLTI_RECORD");
		sortstep.setVariablesToSort("REDDITO, A" );  
		sortstep.execute(); 
		
		DataStep datastep4 = factory_step.createDataStep("DB_ORACLE.ATANTI_ORA2", "PIPPO.MOLTI_RECORD");
		datastep4.execute();   
		
			
		lib_ora.dropTable("ATANTI_ORA2");
		
		//chiude la sessione e svuota la work 
		session.close();
	
	}

	
	private static Connection connOracle() throws Exception {
		OracleDataSource ods = new OracleDataSource();
		ods.setURL("jdbc:oracle:thin:system/alex655321@localhost:1521/XE");
		return ods.getConnection();
	}

}
