package ssc.sito.esempiEn;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputFile;
 
public class Example8 {
     
    public static void main(String[] args) throws Exception {
 
        InputFile input = new InputFile("C:\\ssc_project\\ssc\\dati_testo\\pl_problem.txt");
        input.setInputFormat("Y1-Y5:double, TYPE:varstring(10),  RHS:double");
 
        LP lp=new LP(input);
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=lp.getSolution();
            for(Variable var:soluzione.getVariables()) {
                SscLogger.log("Variable name :"+var.getName() + " value:"+var.getValue());
            }
            SscLogger.log("o.f. value:"+soluzione.getOptimumValue());
        }
    }
}
