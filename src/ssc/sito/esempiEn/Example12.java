package ssc.sito.esempiEn;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.FormatTypeInput.FormatType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputFile;
 
public class Example12 {
      
    public static void main(String[] args) throws Exception {
          
        InputFile input_sparse = new InputFile("C:\\ssc_project\\ssc\\dati_testo/sparse_problem.txt");
        input_sparse.setInputFormat("TYPE:varstring(5), COL_:varstring(3) , ROW_:varstring(7), COEF:double"); 
        LP lp = new LP(input_sparse,FormatType.SPARSE);  
        SolutionType solution_type=lp.resolve();
             
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Variable name :"+var.getName() + " value:"+var.getValue());
            }
            SscLogger.log("o.f. value :"+solution.getOptimumValue());
        }   
        
    }
}