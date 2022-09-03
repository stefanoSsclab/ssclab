package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.ref.Input;
import it.ssc.step.CrossJoinStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.sql.Connection;

import oracle.jdbc.pool.OracleDataSource;

public class Example14 {
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("DB_ORACLE", connOracle());
			factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			
			Library lib_oracle=factory_libraries.getLibrary("DB_ORACLE"); 
			Input table1=lib_oracle.getInputFromSQLQuery(
					          "select neg_cod as neg_cod1, neg_nome, neg_indiri from negozi ");
			
			Input table2=lib_oracle.getInputFromSQLQuery(
			                  "select  neg_cod as neg_cod2, ord_cod, ord_data from ordini");
			
		
			FactorySteps factory_step = session.getFactorySteps();
			
			CrossJoinStep merge=factory_step.createCrossJoinStep("PIPPO.neg_ord");  
			merge.setInputDataForCross(table1,table2);
			merge.setWhere("NEG_COD1.equals(NEG_COD2); ");
			merge.setDropVarOutput("NEG_COD1");
			merge.execute();
			
			FileStep filestep = factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_neg_cod.txt", "pippo.neg_ord");
			filestep.printf("%s %S %S ", "NEG_COD2","NEG_NOME","ORD_COD");
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
