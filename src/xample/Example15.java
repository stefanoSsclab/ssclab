package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.ref.Input;
import it.ssc.step.CrossJoinStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.sql.Connection;

import oracle.jdbc.pool.OracleDataSource;

public class Example15 {

	/**
	* Parole chiave : 
	* 
	* 1) Equi join tra tabelle di qualsiasi natura , con gestione automatica dei null
	*/
	
	public static void main(String[] args) throws Exception {
		Session session = null;
		try {
			session = Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("DB_ORACLE", connOracle());
			factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			
			Library lib_oracle=factory_libraries.getLibrary("DB_ORACLE"); 
			Input table1=lib_oracle.getInput("NEGOZI");
			table1.renameVarToLoad("NEG_COD1", "NEG_COD");
			
			Input table2=lib_oracle.getInputFromSQLQuery(
			                  "select  neg_cod as neg_cod2, ord_cod, ord_data from ordini");
			table2.renameVarToLoad("NEG_COD3", "NEG_COD2");
			
			FactorySteps factory_step = session.getFactorySteps();
			CrossJoinStep cross=factory_step.createCrossJoinStep("PIPPO.neg_ord");  
			cross.setInputDataForCross(table1,table2);
			// DA PROBLEMA IN CASO DI NULL
			//merge.setWhereOnCrossInputData("NEG_COD1.equals(NEG_COD3); ");
			//cosi no
			cross.setEquiJoinVar("NEG_COD1", "NEG_COD3");
			cross.execute();
			
			FileStep filestep = factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_neg_cod.txt", "pippo.neg_ord");
			filestep.printf("%s %s %S %S ","NEG_COD1", "NEG_COD3","NEG_NOME","ORD_COD");
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}


	private static Connection connOracle() throws Exception {
		OracleDataSource ods = new OracleDataSource();
		ods.setURL("jdbc:oracle:thin:system/alex655321@localhost:1521/XE");
		return ods.getConnection();
	}

}
