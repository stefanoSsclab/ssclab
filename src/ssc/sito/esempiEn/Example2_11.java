package ssc.sito.esempiEn;

import static it.ssc.pl.milp.LP.NaN;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import java.util.ArrayList;
  
public class Example2_11 {
      
    public static void main(String[] args) throws Exception {
  
        double A[][]={ 
                { 1.0 , 1.0 },
                { 1.0 , 1.4 },
                {-5.0 , 3.0 }, 
                { 1.0 , 0.0 },  //def. integer
                { 0.0 , 1.0 },  //def.  binary
                { 1.0 , 0.0 },  //def. semicontinuous
                { 3.0 , NaN},  //def. upper
                { 1.0 , 0.0 },  //def. lower
                } ;
        double b[]= { 1.0, 6.0 ,5.0, NaN,NaN,NaN,NaN,NaN};
        double c[]= { -1.0, 3.0  };  
  
        ConsType[] rel= {ConsType.GE, ConsType.LE, ConsType.LE, ConsType.INT, ConsType.BIN,
                         ConsType.SEMICONT, ConsType.UPPER, ConsType.LOWER};
  
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
  
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
  
        MILP lp = new MILP(f,constraints); 
        SolutionType solution_type=lp.resolve();
  
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Variable :"+var.getName() + " value:"+var.getValue());
            }
            SscLogger.log("best value:"+solution.getOptimumValue());
        }   
    }
}