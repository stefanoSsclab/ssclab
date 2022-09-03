package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
  
public class Esempio1_13 {
    public static void main(String[] args) throws Exception {
  
        String lp_string = 
                            " 1    3    max      .    \n" + 
                            " 1    1    ge       1    \n" + 
                            " 1  1.4    le       6    \n" +
                          
                            " 2    6    upper    .    \n" +
                            " 0    4    lower    .    \n" + 
                            " 0    1    semicont .    \n" + 
                            " 1    1    integer .     \n" ; 
              
  
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("X1:double, X2:double, TYPE:varstring(8), RHS:double"); 
  
        MILP lp = new MILP(lp_input); 
        
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

