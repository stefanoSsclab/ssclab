package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
 
public class Esempio1_1b {
     
    public static void main(String[] args) throws Exception {
 
    	 String lp_string=
    			 
                 "3 1 4 7 8 min      . "  +"\n"+
                 "5 2 0 3 0 le       9 "  +"\n"+
                 "3 1 1 0 5 ge       12"  +"\n"+
                 "6 3 4 5 0 ge       124" +"\n"+
                 "1 3 0 3 6 ge       854" +"\n"+
                 "0 1 1 0 0 lower    . "  +"\n"+
                 ". 6 . . . upper    . "  ;
                 
			                 
			                
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("X1-X5:double,  TYPE:varstring(9), RHS:double"); 
 
        LP lp = new LP(lp_input); 
        SolutionType solution_type=lp.resolve(); 
         
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
        else SscLogger.log("Soluzione non ottima:"+solution_type);
    }
}
                

