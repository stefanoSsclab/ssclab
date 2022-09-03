package ssc.sito.esempi;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import static it.ssc.pl.milp.LP.NaN;
public class Esempio2_07b {
     
    public static void main(String[] args) throws Exception {
 
        
        ArrayList< String > constraints = new ArrayList< String >();
        
        constraints.add("min:  3x1 +4X2 +4x3 +3x4 +3X5 +0t1 +0t2 +0d1+0d2"); 
        constraints.add("4x1 +2x2 +3X4       >= 9");
        constraints.add("3x1 + X2 +X3 +5X5   >= 12.5");
        constraints.add("6X1+3.0x2 +4X3 +5X4 <= 124");
        constraints.add(" X1 + 3x2 +3X4 +6X5 <= 854");
        //constraints.add(" int x1,x5");
       
       
        constraints.add("2 <= X1 <= 3");
        constraints.add("t1-X1= -2");
        constraints.add("t2+X1= 3");
       
        constraints.add("d1 +t1-t2=0");
        constraints.add("d2 +t2-t1=0");
       
        
        /*
         * 
        constraints.add(" d1 - X1 >=  -2");
        constraints.add(" d1 + X1 >= 2");
        constraints.add(" d2 - X1 >=  -3");
        constraints.add(" d2 + X1 >= 3");
        
        constraints.add(" d3 -X1 >= -2.5");
        constraints.add(" d3 +X1 >= 2.5");
        
        constraints.add(" d1+d2-30d3 = -14");
    
        
       
        constraints.add(" d2 -X1 >= -3");
        constraints.add(" d2 +X1 >= 3");
      
       
          */
        /*
        constraints.add(" d1 - X4 >=  -1");
        constraints.add(" d1 + X4 >= 1");
        constraints.add(" d2 -X4 >= -2");
        constraints.add(" d2 +X4 >= 2");
        constraints.add(" d3 -X4 >= -3.5");
        constraints.add(" d3 +X4 >= +3.5");
        constraints.add(" d3 >= +0.5");
        */
             
        LP milp = new LP(constraints); 
        //milp.setJustTakeFeasibleSolution(true);
        SolutionType solution_type=milp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution sol=milp.getSolution();
       
            Variable[] var_int=sol.getVariables();
           
            for(int _i=0; _i< var_int.length;_i++) {
                SscLogger.log("Nome variabile :"+var_int[_i].getName() + " valore:"+var_int[_i].getValue());
            }
            SscLogger.log("valore ottimo:"+sol.getOptimumValue() ); 
        }
    }
}
