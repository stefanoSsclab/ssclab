package ssc.sito.esempi;


import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
 
 
public class Esempio1_1b {
     
    public static void main(String[] args) throws Exception {
 
        String lp_string = 
                            " 1    1    1   max      .    \n" +  
        
                            " 1    2    3   eq      1    \n" +   
                            " 3    4    6   eq      3    \n" +  
                            "10    5   -3  eq      -4    \n"+
                            " .    .   .   lower    .";
             
 
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("X1-X3:double, TYPE:varstring(5), RHS:double"); 
 
        LP lp = new LP(lp_input); 
        lp.setJustTakeFeasibleSolution(true);
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.FEASIBLE) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
        else SscLogger.log("Soluzione non ottima:"+solution_type);
        
    }
}