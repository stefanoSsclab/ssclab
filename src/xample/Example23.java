package xample;


import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.FactoryLibraries;
import it.ssc.step.FileStep;
import it.ssc.step.SortStep;

public class Example23 {
	
	public static void main(String[] args) throws Exception {

		Session session = null;
		try {
			session = Context.createNewSession();

			FactoryLibraries factory_libraries = session.getFactoryLibraries();
			factory_libraries.addLibrary("PIPPO", "e:\\fmt_stat_dati");
			
			SortStep datastep2 = session.createSortStep ("WORK.TANTI", "PIPPO.TANTI");
			datastep2.setVariablesToSort("REDDITO , A desc"); 
			datastep2.execute(); 

			FileStep filestep = session.createFileStep("e:\\fmt_stat_prove\\stampa_tanti.txt", "WORK.TANTI");
			filestep.printf("%s %s %s %S %7.2f %tD", "A", "B", "C", "D", "REDDITO","GIORNO");
			filestep.execute();

		} 
		finally {
			if (session != null) session.close();
		}
	}
}
