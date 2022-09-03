package ssc.sito.esempi;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import static it.ssc.pl.milp.LP.NaN;
public class Esempio2_07 {
     
    public static void main(String[] args) throws Exception {
 
        double c[]= { 3, 1, 4, 7, 8  }; 
        LinearObjectiveFunction fo = new LinearObjectiveFunction(c, GoalType.MIN);
         
        ArrayList< String > constraints = new ArrayList< String >();
        
        constraints.add("min:  3x1 +4X2 +4x3 +3x4 +3X5 "); 
        constraints.add("4x1 +2x2 +3X4       >= 9");
        constraints.add("3x1 + X2 +X3 +5X5   >= 12.5");
        constraints.add("6X1+3.0x2 +4X3 +5X4 <= 124");
        constraints.add(" X1 + 3x2 +3X4 +6X5 <= 854");
        constraints.add(" int x1,x5");
             
        MILP milp = new MILP(constraints); 
        //milp.setJustTakeFeasibleSolution(true);
        SolutionType solution_type=milp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution sol=milp.getSolution();
            Solution sol_relax=milp.getRelaxedSolution();
            Variable[] var_int=sol.getVariables();
            Variable[] var_relax=sol_relax.getVariables();
            for(int _i=0; _i< var_int.length;_i++) {
                SscLogger.log("Nome variabile :"+var_int[_i].getName() + " valore:"+var_int[_i].getValue()+ 
                              " ["+var_relax[_i].getValue()+"]");
            }
            SscLogger.log("valore ottimo:"+sol.getOptimumValue() +" ["+sol_relax.getOptimumValue()+"]"); 
        }
    }
}
