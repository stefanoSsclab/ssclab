package ssc.sito.esempi2;
import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
 
public class Esempio1_06 {
     
    public static void main(String[] args) throws Exception {
         
        ArrayList< String > constraints = new ArrayList< String >();
        constraints.add("min:  3Y +2x2   +4Z +7x4 +8X5 ");
        constraints.add("      5Y +2x2       +3X4      >= 9");
        constraints.add("      3Y + X2   + Z       +5X5 >= 12");
        constraints.add("      6Y +3.0x2 +4Z +5X4      <= 124");
        constraints.add("       Y +3x2       +3X4 +6X5 <= 854");
        constraints.add("-1 <=  x2  <= 6");
        constraints.add("1<=  z <= .");
             
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