package xdemo;

import java.util.logging.Logger;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.datasource.DataSource;
import it.ssc.library.Library;
import it.ssc.log.SscLogger;
import it.ssc.ref.Input;

public class Example22b {

	public static void main(String[] args) throws Exception {
		
		Session session =   Context.createNewSession();		
		Logger log=SscLogger.getLogger();
		Input input =session.addLibrary( "LOCALE","C:/ssc/demo_info/libreria_dati").getInput("anagrafica_destinatari_georef");
		DataSource data=session.createDataSource(input);

		while(data.next()) { 
			for(int i=1;i< data.getNumColunm();i++) {
				log.info(data.getObject(i)+"\t");
			}
			log.info("--------");
		}
		data.close();
		
		session.close();
	}
	
}
