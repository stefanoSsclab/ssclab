package xample;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.parser.exception.InvalidInformatStringException;
import it.ssc.ref.InputFile;
import it.ssc.step.FactorySteps;
import it.ssc.step.FileStep;

import java.util.regex.Pattern;

public class Example10 {
	
	/**
	* Parole chiave : 
	* 
	* 1) lettura di input e scrittura di output di dati formato testo senza creazione di dataset (nemmeno temporanei)
	*/
	
	public static void main(String[] args) throws Exception, InvalidInformatStringException {
		
		
		Session session = null;
		try {
			session =  Context.createNewSession();

			InputFile ref = new InputFile("E:\\fmt_stat_prove\\punto_tanti.txt");
			ref.setInputFormat("A:fixstring(12), B:varstring(5), c:varstring(30), D:varstring(100), GIORNO:date(gg-mm-aa), REDDITO:double");
			ref.setSeparator(Pattern.compile(";"));

			FactorySteps factory_step = session.getFactorySteps();
			FileStep filestep = factory_step.createFileStep("e:\\fmt_stat_prove\\stampa_tanti2.txt", ref);
			filestep.printf("%s %s %s %s %7.2f", "A", "B", "C", "D","REDDITO");
			filestep.execute();
			
		} 
		finally {
			if (session != null) session.close();
		}
	}
}
