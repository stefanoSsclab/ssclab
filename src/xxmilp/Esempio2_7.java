package xxmilp;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import static it.ssc.pl.milp.MILP.NaN;
 
public class Esempio2_7 {
     
    public static void main(String[] args) throws Exception {
 
        double c[]= { 3, 1, 4, 7, 8  }; 
        LinearObjectiveFunction fo = new LinearObjectiveFunction(c, GoalType.MIN);
         
        ArrayList< String > constraints = new ArrayList< String >();
        
        constraints.add("min:  3x1 +X2 +4x3 +7x4 +8X5 "); 
        constraints.add("5x1 +2x2 +3X4       >= 9");
        constraints.add("3x1 + X2 +X3 +5X5   >= 12.5");
        constraints.add("6X1+3.0x2 +4X3 +5X4 <= 124");
        constraints.add(" X1 + 3x2 +3X4 +6X5 <= 854");
        constraints.add(" int x2, X3 ");
             
        MILP milp = new MILP(constraints); 
       
        SolutionType solution_type=milp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=milp.getSolution();
            for(Variable var:soluzione.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore :"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+soluzione.getOptimumValue());
        }
    }
}
            

