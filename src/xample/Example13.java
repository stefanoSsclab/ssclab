package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.library.Library;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.ref.Input;
import it.ssc.step.CrossJoinStep;
import it.ssc.step.DataStep;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.sql.Connection;

import oracle.jdbc.pool.OracleDataSource;

public class Example13 {

	/**
	* Parole chiave : 
	* 
	* 1) Cross join con rename di campi con uguale nome  
	*/
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		Session session = null;
		try {
			session =  Context.createNewSession();
			
			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			
			factory_libraries.addLibrary("DB_ORACLE", connOracle());
			factory_libraries.addLibrary( "PIPPO","E:\\fmt_stat_dati\\"); 
			
			Library lib_oracle=factory_libraries.getLibrary("DB_ORACLE"); 
			Input table1=lib_oracle.getInputFromSQLQuery(
					          "select neg_cod , neg_nome, neg_indiri from negozi ");
			
			Input table2=lib_oracle.getInputFromSQLQuery(
			                  "select  neg_cod  , ord_cod, ord_data from ordini");
			
			
			FactorySteps factory_step = session.getFactorySteps();
			DataStep datastep = factory_step.createDataStep("PIPPO.negozi",table1);
			datastep.execute();
			
			DataStep datastep2 = factory_step.createDataStep("PIPPO.ordini",table2);
			datastep2.execute();
			
			Library lib_pippo=factory_libraries.getLibrary("PIPPO"); 
			Input negozi=lib_pippo.getInput("negozi"); 
			
			int num_colonne=table2.getColumnCount();
			for(int a=1;a<=num_colonne;a++) {
				System.out.println("Nome campo:"+table2.getColumnName(a));
				System.out.println("Lunghezza:"+table2.getField(a).getLenght());
			}
			
			negozi.renameVarToLoad("NEG_COD1", "NEG_COD");
			
			CrossJoinStep cross=factory_step.createCrossJoinStep("pippo.neg_ord");  
			cross.setInputDataForCross(negozi,"PIPPO.ordini");
			cross.setWhere("NEG_COD1.equals(NEG_COD); ");
			cross.execute();
			
			
			FileStep filestep = factory_step.createFileStep("E:\\fmt_stat_prove\\stampa_neg_cod.txt", "pippo.neg_ord");
			filestep.printf("%s %S %S %S ", "NEG_COD1","NEG_COD","NEG_NOME","ORD_COD");
			filestep.execute();
			
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
}
