package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.ref.Input;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;
import it.ssc.step.OuterJoinStep;

import java.sql.Connection;

import oracle.jdbc.pool.OracleDataSource;

public class Example17 {
	
	/**
	* Parole chiave : 
	* 
	* 1) OuterJoin join tra tabelle di qualsiasi natura , con gestione automatica dei null
	*/
	
	public static void main(String[] args) throws Exception {
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			Library lib_oracle2=factory_libraries.addLibrary("DB_ORACLE", connOracle());
			factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			System.out.println("URL:"+lib_oracle2.getUrl());
			
			Input input1=lib_oracle2.getInput("NEGOZI");
			input1.renameVarToLoad("NEG_COD1", "NEG_COD");
			
			
			Input input2=lib_oracle2.getInputFromSQLQuery("select  neg_cod , ord_cod, ord_data from ordini");
			input2.renameVarToLoad("NEG_COD3", "NEG_COD");
			
			
			FactorySteps factory_step = session.getFactorySteps();
			OuterJoinStep outer=factory_step.createOuterJoinStep("PIPPO.neg_ord");  
			outer.setInputDataForLeftJoin(input1,input2);
			outer.setOuterJoinVar("NEG_COD1", "NEG_COD3");
			outer.execute();
			
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
