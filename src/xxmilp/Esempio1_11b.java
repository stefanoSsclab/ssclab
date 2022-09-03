package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;

import java.util.ArrayList;
import java.util.Random;
import static it.ssc.pl.milp.LP.NaN;
public class Esempio1_11b {
     
public static void main(String arg[]) throws Exception {
     
     
	final int M = 870;  // rows
    final int N = 1150;  // cols
        
        double[] c = new double[N];
        double[] b = new double[M];
        double[][] A = new double[M][N];
        for (int j = 0; j < N; j++)      c[j] = 5;
        for (int i = 0; i < M; i++)      b[i] = 1000+ i;
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)  A[i][j] = 1.+ i+j;
                 
 
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
 
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], ConsType.LE, b[i])); 
        }
 
        LP lp = new LP(f,constraints);
        SolutionType solution_type=lp.resolve();
 
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
            	 SscLogger.log("Variabile "+var.getName() +": "+var.getLower() + " <= ["+var.getValue()+"] <= "+var.getUpper());
            }
            SscLogger.log("Valore f.o. :"+solution.getOptimumValue());  
        }
        else SscLogger.log("Soluzione non ottima. Tipo di soluzione:"+solution_type);
    }
}

