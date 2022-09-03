package ssc.sito.esempiEn;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import java.util.ArrayList;
import static it.ssc.pl.milp.LP.NaN;
 
public class Esample2_8 {
     
    public static void main(String arg[]) throws InvalidSessionException, Exception {
 
        double[]   c =  { 2, 2, 2, 2, 2 ,2, 2, 2, 2, 2,2 ,2 ,2 };
        double[]   b =  {1000, 1234, 1000, 1000, 1000, 1000, 1000, 1000, 1000};
         
        double[][] A ={ { 2., 9. ,7. ,5. ,9. ,6. ,3., 7., 8. ,7. ,5. ,3. ,1. },
                        { 4. ,1. ,2. ,3. ,6. ,4. ,5. ,2. ,8. ,5. ,3. ,4., 7. },
                        { 3. ,4. ,2. ,5. ,7. ,6. ,3. ,5. ,7. ,4. ,6. ,8. ,6. },
                        { 4. ,6. ,9. ,8. ,7. ,6. ,5. ,4. ,3. ,2. ,3. ,5. ,6. },
                        { 4. ,4. ,7. ,5. ,3. ,8. ,5. ,6. ,3. ,5. ,6. ,4. ,6. },
                        { 2. ,6. ,4. ,5. ,7. ,5. ,6. ,4. ,6. ,7. ,4. ,4. ,6. },
                        { 4. ,6. ,9. ,8. ,3. ,6. ,5. ,5. ,3. ,2. ,9. ,5. ,6. },
                        { 4. ,5. ,7. ,8. ,3. ,8. ,3. ,6. ,3. ,5. ,6. ,1. ,6. },
                        { 2., 2., 4., 3., 7. ,5. ,9. ,4. ,6. ,7. ,8. ,4., 6. }};
 
        double[] upper ={ 190.5, NaN, NaN, NaN, NaN ,NaN ,NaN ,NaN ,35.0 ,NaN ,NaN ,NaN, NaN };
        double[] integer ={ 1.0, 1.0, 1.0, 1.0, 1.0 ,1.0 ,1.0 ,1.0 ,1.0 ,1.0 ,1.0 ,1.0, 1.0 };
 
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
 
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i< A.length; i++) {
            constraints.add(new Constraint(A[i], ConsType.LE, b[i])); 
        }
 
        constraints.add(new Constraint(upper,   ConsType.UPPER, NaN)); 
        constraints.add(new Constraint(integer, ConsType.INT , NaN)); 
 
        MILP milp = new MILP(f,constraints);
        SolutionType solution=milp.resolve();
 
        if(solution==SolutionType.OPTIMUM) { 
            Solution sol=milp.getSolution();
            Solution sol_relax=milp.getRelaxedSolution();
            Variable[] var_int=sol.getVariables();
            Variable[] var_relax=sol_relax.getVariables();
            for(int _i=0; _i< var_int.length;_i++) {
                SscLogger.log("Variable name :"+var_int[_i].getName() + " value:"+var_int[_i].getValue()+ 
                              " ["+var_relax[_i].getValue()+"]");
            }
            SscLogger.log("o.f. value:"+sol.getOptimumValue() +" ["+sol_relax.getOptimumValue()+"]"); 
        }
    }
}