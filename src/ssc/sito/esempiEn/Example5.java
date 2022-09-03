package ssc.sito.esempiEn;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
 
public class Example5 {
     
    public static void main(String[] args) throws Exception {
 
         
        ArrayList< String > constraints = new ArrayList< String >();
        constraints.add("min:  3Y +2x2 +4x3 +7x4 +8X5 "); 
        constraints.add("5Y +2x2 +3X4       >= 9");
        constraints.add("3Y + X2 +X3 +5X5   >= 12");
        constraints.add("6Y+3.0x2 +4X3 +5X4 <= 124");
        constraints.add(" y + 3x2 +3X4 +6X5 <= 854");
         
        LP lp = new LP(constraints); 
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=lp.getSolution();
            for(Variable var:soluzione.getVariables()) {
                SscLogger.log("Variable name :"+var.getName() + " value :"+var.getValue());
            }
            SscLogger.log("Value:"+soluzione.getOptimumValue());
        }
    }
}
