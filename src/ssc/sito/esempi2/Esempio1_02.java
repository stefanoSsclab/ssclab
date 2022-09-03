package ssc.sito.esempi2;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.pl.milp.ListConstraints;

 
 
public class Esempio1_02 {
 
    public static void main(String[] args) throws Exception {
 
        double A[][]={ 
                { 1.0 , 1.0 },
                { 1.0 , 1.4 },
                {-5.0 , 3.0 } } ;
        double b[]= {-1.0, 6.0 ,5.0 };
        double c[]= { 1.0, 3.0  };  
 
        ConsType[] rel= {ConsType.GE, ConsType.LE, ConsType.EQ};
 
        LinearObjectiveFunction fo = new LinearObjectiveFunction(c, GoalType.MAX);
 
        ListConstraints constraints = new ListConstraints();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
 
        LP lp = new LP(fo,constraints); 
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
