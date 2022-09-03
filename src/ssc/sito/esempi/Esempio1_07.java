package ssc.sito.esempi;

import it.ssc.log.SscLogger;

import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionConstraint;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.pl.milp.FormatTypeInput.FormatType;
import it.ssc.ref.InputString;
 
public class Esempio1_07 {
     
    public static void main(String[] args) throws Exception {
 
        String lp_sparse = 
         
            //    TYPE   COL_   ROW_    COEF 
                  
                " MAX     .    costo      .    \n" +   
                " GE      .    row1       .    \n" +      
                " LE      .    row2       .    \n" +  
                " EQ      .    row3       .    \n" +
                " UPPER   .    lim_sup    .    \n" +
                " LOWER   .    lim_inf    .    \n" +              
         
                " .      X1    costo      1    \n" +
                " .      X1    row1       1    \n" +      
                " .      X1    row2       1    \n" +  
                " .      X1    row3      -5    \n" +
                " .      X1    lim_sup    1    \n" +
                " .      X1    lim_inf   -1    \n" +               
                  
                " .      X2    costo      3    \n" +
                " .      X2    row1       1    \n" +      
                " .      X2    row2     1.4    \n" +  
                " .      X2    row3       3    \n" +
                " .      X2    lim_inf    .    \n" +           
                  
                " .      RHS   row1       1    \n" +      
                " .      RHS   row2       6    \n" +  
                " .      RHS   row3       5    \n"   ;
             
 
        InputString lp_input = new InputString(lp_sparse); 
        lp_input.setInputFormat("TYPE:varstring(5), COL_:varstring(3) , ROW_:varstring(7), COEF:double"); 
 
        LP lp = new LP(lp_input,FormatType.SPARSE); 
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            
            for(SolutionConstraint sol_constraint: solution.getSolutionConstraint()) {
                SscLogger.log("Vincolo "+sol_constraint.getName()+" : valore="+sol_constraint.getValue() + 
                              "[ "+sol_constraint.getRel()+"  "+sol_constraint.getRhs()+" ]" );
            }
            
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
    }
}
