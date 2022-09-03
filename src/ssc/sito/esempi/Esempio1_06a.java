package ssc.sito.esempi;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
public class Esempio1_06a {
     
    public static void main(String[] args) throws Exception {
 
        ArrayList< String > constraints = new ArrayList< String >();
        constraints.add("  "); 
        constraints.add("max:  3x -X +y +4x3 +7x4 +8X5 "); 
        constraints.add("vin1: 5x +2y +3X4  -2x     >= 9");
        constraints.add("vin2: 3x + y +X3 +5X5   >= 12");
        constraints.add("vin3: 6X+3.0y +4X3 +5X4 <= 124");
        constraints.add("vin4: X + 3y +3x4 +6.1X5 <= 854");
      
        constraints.add(" Bound_SCAR:+3 <= x <= +66");
        constraints.add(" 5 <= x4 <= .");
        constraints.add(" + 9 >= y >= -8");
        constraints.add(" 8 >= x3");
      
        /*
        */
        
        
        LP lp = new LP("C:\\ssc_project\\ssc\\dati_testo\\dises.txt");
        //LP lp = new LP(constraints); 
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=lp.getSolution();
            for(Variable var:soluzione.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore :"+var.getValue());
            }
            for(SolutionConstraint sol_constraint: soluzione.getSolutionConstraint()) {
                SscLogger.log("Vincolo "+sol_constraint.getName()+" : valore="+sol_constraint.getValue() + 
                              "[ "+sol_constraint.getRel()+"  "+sol_constraint.getRhs()+" ]" );
            }
            SscLogger.log("Valore ottimo:"+soluzione.getOptimumValue());
        }
    }
}