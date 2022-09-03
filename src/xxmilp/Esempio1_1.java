package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
 
public class Esempio1_1 {
     
    public static void main(String[] args) throws Exception {
 
    	 String lp_string = 
			                 " 4   2   -1    min      .    \n" +  
			                 " 1    1   2   ge       3     \n" +   
			                 " 2   -2    4   le       5     " ;
			                 
             
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("X1-X3:double, TYPE:varstring(9), RHS:double"); 
 
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
                

