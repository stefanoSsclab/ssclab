package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
  
public class Esempio1_3 {
    public static void main(String[] args) throws Exception {
  
    	 String lp_string = 
                 " 1    3    max      .    \n" + 

                 " 1    1    ge       -3    \n" + 
                 " 1  1.4    le       6    \n" +
                 "-1    3    eq       3    \n" + 
                 " -1    .   upper    .    \n" +
                 "-2    .    lower    .    \n" ; 
              
  
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("X1:double, X2:double, TYPE:varstring(8), RHS:double"); 
  
        LP lp = new LP(lp_input); 
        SolutionType solution_type=lp.resolve();
              
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Variabile "+var.getName() +": "+var.getLower() + " <= ["+var.getValue()+"] <= "+var.getUpper());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
    }
}

