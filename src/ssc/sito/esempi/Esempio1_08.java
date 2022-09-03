package ssc.sito.esempi;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputFile;
 
public class Esempio1_08 {
     
    public static void main(String[] args) throws Exception {
 
    	InputFile input = new InputFile("C:\\ssc_project\\ssc\\dati_testo\\pl_problem.txt");
        input.setInputFormat("Y1-Y5:double, TYPE:varstring(10),  RHS:double");
 
        LP lp=new LP(input);
        //lp.setParallelSimplex(true);
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=lp.getSolution();
            for(Variable var:soluzione.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore :"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+soluzione.getOptimumValue());
        }
    }
}
