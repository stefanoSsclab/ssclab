package ssc.sito.esempiEn;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import java.util.ArrayList;
import java.util.Random;
 
public class Example11 {
     
 public static void main(String[] args) throws Exception {
  
        LP lp = new LP("C:\\ssc_project\\ssc\\dati_testo\\pl_proble.txt");
        SolutionType solution_type=lp.resolve();
          
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=lp.getSolution();
            for(Variable var:soluzione.getVariables()) {
                SscLogger.log("Variable name :"+var.getName() + " value :"+var.getValue());
            }
            for(SolutionConstraint sol_constraint: soluzione.getSolutionConstraint()) {
                SscLogger.log("Vincolo "+sol_constraint.getName()+" : valore="+sol_constraint.getValue() + 
                              "[ "+sol_constraint.getRel()+"  "+sol_constraint.getRhs()+" ]" );
            }
            SscLogger.log("Valore ottimo:"+soluzione.getOptimumValue());
        }
    }
}
