package org.ssclab.pl.milp.util;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	                "-1<=  x2 <= 6, Y >=0 ,  N >=0\n"+
	                ". <=  z  <= . , x4 >=5 \n"+
	                "int x2,x3,x4";

		try (BufferedReader originalReader = new BufferedReader(new StringReader(pl_string))) {

			// Converte lo StringWriter in un nuovo BufferedReader
			try (BufferedReader resultReader = splitterBounds(originalReader)) {
				String newLine;
				while ((newLine = resultReader.readLine()) != null) {
					System.out.println(newLine); // Stampa ogni nuova riga
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedReader splitterBounds(BufferedReader originalReader) throws IOException,ParseException {
    	
		StringWriter stringWriter = new StringWriter() ;
		Pattern pattern_min =Pattern.compile("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.))\\s*<\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(<\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.)))?\\s*");
		Pattern pattern_max =Pattern.compile("\\s*(((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.))\\s*>\\s*=)?\\s*(\\p{Alpha}+\\w*)\\s*(>\\s*=\\s*((([+-]?)\\s*(\\d+\\.?\\d*|\\[([^\\[\\]]+?)\\]))|(\\.)))?\\s*");
		Matcher matcher_min,matcher_max;
		String line;
        String[] tokens;
    	while ((line = originalReader.readLine()) != null) {
    		if(line.matches("(.+)((<\\s*=)|(>\\s*=)|(=))(.+)")) { 
    			// Suddivide la riga in token separati da virgole
    			if (line.endsWith(",")) {
    				 throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+line+"");
    			}
	            tokens = line.split(",");
	            for (String token : tokens) {
	                // Scrive ogni token come una nuova riga nello StringWriter
	            	if(tokens.length > 1 ) {
	            		
	            		matcher_min = pattern_min.matcher(token);
	            		matcher_max = pattern_max.matcher(token);
	            		
	            		//System.out.println("entrato:"+line); // Stampa ogni nuova riga
		            	if (matcher_min.matches()) {
		        			//non sono presenti tutti i controlli, altri controlli li fa quando lo elabora
		            		//System.out.println("MIN:"+token);
		            		if(matcher_min.group(1)==null && matcher_min.group(9)==null) {
		            			 //System.out.println("ERRORE");
		            			 throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+line+"");
		            		}
		        		}
		        		//pattern per identificare uppur o lower del tipo  "nome_upper: a>= x >= b"  , anche con a o b mancanti 
		        		else if (matcher_max.matches()) {
		        			//non sono presenti tutti i controlli, altri controlli li fa quando lo elabora
		        			//System.out.println("MAX:"+token);
		        			if(matcher_max.group(1)==null && matcher_max.group(9)==null) {
		            			 //System.out.println("ERRORE");
		            			 throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+line+"");
		            		}
		        		}	            	
		        		else {
		        			throw new ParseException(RB.getString("it.ssc.pl.milp.ScanConstraintFromString.msg1")+" "+line+"");
		        		}
	            	}	
	                stringWriter.write(token+System.lineSeparator());
	            }
    		}
    		else  stringWriter.write(line+System.lineSeparator());
        }

       originalReader.close();
       //Converte lo StringWriter in un nuovo BufferedReader
       return new BufferedReader(new StringReader(stringWriter.toString())); 
	
    }
	

    public static BufferedReader splitterBoundsAndDouble(BufferedReader originalReader) throws IOException,ParseException {
    	
		StringWriter stringWriter = new StringWriter() ;
		Pattern pattern_double =Pattern.compile("\\s*(\\p{Alpha}+\\w*\\s*:)?(.+)((<|>)\\s*=)(.+)((<|>)\\s*=)(.+)");
        String line;
        String[] tokens;
        Matcher matcher_double;
    	while ((line = originalReader.readLine()) != null) {
    		if(line.matches("\\s*(\\p{Alpha}+\\w*\\s*:)?(.+)(<\\s*=)(\\s*([+-]?)\\s*(((\\d+)(\\.)?(\\d*))|(\\[([^\\[\\]]+?)\\]))?((\\p{Alpha}+)(\\w*))\\s*){2,}(<\\s*=)(.+)") || 
    		   line.matches("\\s*(\\p{Alpha}+\\w*\\s*:)?(.+)(>\\s*=)(\\s*([+-]?)\\s*(((\\d+)(\\.)?(\\d*))|(\\[([^\\[\\]]+?)\\]))?((\\p{Alpha}+)(\\w*))\\s*){2,}(>\\s*=)(.+)")		         ) { 
    			
    			matcher_double = pattern_double.matcher(line);
    			if (matcher_double.matches()) { 
    			//System.out.println("numero--:"+matcher_double.groupCount());
    			
    				String gruppo1=matcher_double.group(1); 
    				if(gruppo1==null) gruppo1="";
    				//System.out.println("numero1-:"+gruppo1);
    				String gruppo2=matcher_double.group(2);
    				//System.out.println("numero2-:"+gruppo2);
    				String gruppo3=matcher_double.group(3);
    				//System.out.println("numero3-:"+gruppo3);
    				//String gruppo4=matcher_double.group(4);
    				//System.out.println("numero4-:"+gruppo4);
    				String gruppo5=matcher_double.group(5);
    				//System.out.println("numero5-:"+gruppo5);
    				String gruppo6=matcher_double.group(6); 
    				//System.out.println("numero6-:"+gruppo6);
    				
    				//String gruppo7=matcher_double.group(7);
    				//System.out.println("numero7-:"+gruppo7);
    				String gruppo8=matcher_double.group(8); 
    				//System.out.println("numero8-:"+gruppo8);
    				
    				String vincolo_1=gruppo1+gruppo2+gruppo3+gruppo5;
    				String vincolo_2=gruppo1+gruppo5+gruppo6+gruppo8;
    				//System.out.println("VINCOLO 1:"+vincolo_1);
    				//System.out.println("VINCOLO 2:"+vincolo_2);
    				 
    				stringWriter.write(vincolo_1+System.lineSeparator());
    				stringWriter.write(vincolo_2+System.lineSeparator());
    			}
    		}
    		
    		else if(line.matches("(.+)((<\\s*=)|(>\\s*=)|(=))(.+)")) { 
    			// Suddivide la riga in token separati da virgole
	            tokens = line.split(",");
	            for (String token : tokens) {
	                stringWriter.write(token+System.lineSeparator());
	            }
    		}
    		else  stringWriter.write(line+System.lineSeparator());
        }
       originalReader.close();
       //Converte lo StringWriter in un nuovo BufferedReader
       return new BufferedReader(new StringReader(stringWriter.toString())); 
    }
}
