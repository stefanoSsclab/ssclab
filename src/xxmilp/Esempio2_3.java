package xxmilp;

import it.ssc.log.SscLogger;

import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.pl.milp.FormatTypeInput.FormatType;
import it.ssc.ref.InputString;
 
public class Esempio2_3 {
    public static void main(String[] args) throws Exception {
 
        String lp_sparse = 
         
         
            //    TYPE   COL_   ROW_    COEF 
                  
                " MAX     .    costo      .    \n" +   
                " GE      .    row1       .    \n" +      
                " LE      .    row2       .    \n" +  
                " EQ      .    row3       .    \n" +
                " UPPER   .    lim_sup    .    \n" +
                " LOWER   .    lim_inf    .    \n" +    
                " INTEGER .    var_int    .    \n" +  
         
                " .      Y1    costo      1    \n" +
                " .      Y1    row1       1    \n" +      
                " .      Y1    row2       1    \n" +  
                " .      Y1    row3      -5    \n" +
                " .      Y1    lim_sup    1    \n" +
                " .      Y1    lim_inf   -1    \n" +               
                  
                " .      Y2    costo      3    \n" +
                " .      Y2    row1       1    \n" +      
                " .      Y2    row2     1.4    \n" +  
                " .      Y2    row3       3    \n" +
                " .      Y2    lim_sup    .    \n" +
                " .      Y2    lim_inf    .    \n" +    
                " .      Y2    var_int    1    \n" +
                  
                " .     RHS    row1       1    \n" +      
                " .     RHS    row2       6    \n" +  
                " .     RHS    row3       5    \n"   ;
             
 
        InputString lp_input = new InputString(lp_sparse); 
        lp_input.setInputFormat("TYPE:varstring(7), COL_:varstring(3) , ROW_:varstring(7), COEF:double"); 
 
        MILP lp = new MILP(lp_input,FormatType.SPARSE); 
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
    }
}

