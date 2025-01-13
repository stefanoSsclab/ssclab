package org.ssclab.pl.milp.util;

import java.io.*;
import org.ssclab.i18n.RB;
import org.ssclab.pl.milp.ParseException;

public class CsvSplitter {
	public static void main(String[] args) throws ParseException {
		 String  pl_string = 
	                "min:  3Y +2x2   +4Z +7x4 +8X5       \n"+
	                "      5Y +2x2       +3X4      >= 9 \n"+
	                "      3Y + X2   + Z      +5X5  = 12 \n"+
	                "      6Y +3.0x2 +4Z +5X4      <= 124\n"+
	                "       Y +3x2       +3X4 +6X5 <= 854\n"+
	                "-1<=  x2 <= 6, Y >=0 , N >=0\n"+
	                ". <=  z  <= . , x4 >=5 \n";

		try (BufferedReader originalReader = new BufferedReader(new StringReader(pl_string))) {

			// Converte lo StringWriter in un nuovo BufferedReader
			try (BufferedReader resultReader = splitterCsv(originalReader)) {
				String newLine;
				while ((newLine = resultReader.readLine()) != null) {
					System.out.println(newLine); // Stampa ogni nuova riga
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedReader splitterCsv(BufferedReader originalReader) throws IOException,ParseException {
    	
		StringWriter stringWriter = new StringWriter() ;
        String line;
    
    	while ((line = originalReader.readLine()) != null) {
            // Suddivide la riga in token separati da virgole
            String[] tokens = line.split(",");
            for (String token : tokens) {
                // Scrive ogni token come una nuova riga nello StringWriter
            	if(tokens.length > 1) {
            		//System.out.println("entrato"); // Stampa ogni nuova riga
	            	if (token.matches("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[(.+?)\\]))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[(.+?)\\]))|(\\.)))?\\s*")) {
	        			//non sono presenti tutti i controlli, altri controlli li fa quando lo elabora
	        		}
	        		//pattern per identificare uppur o lower del tipo  "nome_upper: a>= x >= b"  , anche con a o b mancanti 
	        		else if (token.matches("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[(.+?)\\]))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[(.+?)\\]))|(\\.)))?\\s*")) {
	        			//non sono presenti tutti i controlli, altri controlli li fa quando lo elabora
	        		}
	        		else {
	        			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+line+"");
	        		}
            	}	
                stringWriter.write(token.trim());
                stringWriter.write(System.lineSeparator());
            }
        }

       originalReader.close();
    // Converte lo StringWriter in un nuovo BufferedReader
       return new BufferedReader(new StringReader(stringWriter.toString())); 
	
    }
}
