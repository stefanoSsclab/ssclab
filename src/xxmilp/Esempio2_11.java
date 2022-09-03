package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;

import java.util.ArrayList;
import static it.ssc.pl.milp.LP.NaN;
public class Esempio2_11 {
     
    public static void main(String[] args) throws Exception {
 
        double A[][]={ 
                { 1.0 , 1.0 },
                { 1.0 , 1.4 },
                {-5.0 , 3.0 }, 
                { 1.0 , 0.0 },  //definizione degli integer
                { 0.0 , 1.0 },  //definizione dei binary
                { 1.0 , 0.0 },  //definizione dei semicont
                { 3.0 , NaN }, //definizione dei upper
                { 1.0 , 0.0 },  //definizione dei lower
                } ;
        double b[]= { 1.0, 6.0 ,5.0, NaN,NaN,NaN,NaN,NaN};
        double c[]= { -1.0, 3.0  };  
 
        ConsType[] rel= {ConsType.GE, ConsType.LE, ConsType.LE, ConsType.INT , ConsType.BIN,
        		         ConsType.SEMICONT,ConsType.UPPER,ConsType.LOWER};
 
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
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
    }
}

