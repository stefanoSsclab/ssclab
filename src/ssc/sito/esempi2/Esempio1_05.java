package ssc.sito.esempi2;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
 
public class Esempio1_05 {
     
    public static void main(String[] args) throws Exception {
 
         
        ArrayList< String > constraints = new ArrayList< String >();
        constraints.add("min: 10x1 -2x2 -x3 +4x4 "); 
        constraints.add("x1 +x2 +x3 +x4  >=10");
        constraints.add("x3 - 100x4 >=20 ");
        constraints.add("10x1 + 20x4 >=50 ");
        constraints.add("34x2 +12x3 <= 500");
        constraints.add("x1 >= 5");
        constraints.add("x3 <= 40 ");
         
        LP lp = new LP(constraints); 
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
