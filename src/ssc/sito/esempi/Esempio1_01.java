package ssc.sito.esempi;


import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
 
 
public class Esempio1_01 {
     
    public static void main(String[] args) throws Exception {
 
        String lp_string = 
                            " 1    3    max      .    \n" +  
                            " 1    1    ge      -1    \n" +   
                            " 1  1.4    le       6    \n" +  
                            "-5    3    eq       5";
             
 
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("X1:double, X2:double, TYPE:varstring(3), RHS:double"); 
 
        LP lp = new LP(lp_input); 
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMAL) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
        else SscLogger.log("Soluzione non ottima:"+solution_type);
        
    }
}