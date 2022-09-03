package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;

import java.util.ArrayList;
import java.util.Random;
import static it.ssc.pl.milp.LP.NaN;
public class Esempio1_12 {
    public static void main(String arg[]) throws Exception {
         
        final int M = 445;  // rows
        final int N = 345;  // cols
         
        Random random = new Random();
         
        double[] c = new double[N];
        double[] b = new double[M];
        double[][] A = new double[M][N];
        for (int j = 0; j < N; j++)      c[j] = (double) (random.nextInt(20));
        for (int i = 0; i < M; i++)      b[i] = (double) random.nextInt(100000000);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)  A[i][j] = (double) random.nextInt(10);
                 
 
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
 
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], ConsType.LE, b[i])); 
        }
        
        LP lp = new LP(f,constraints);
        lp.setCEpsilon(EPSILON._1E_M5);
        SolutionType solution_type=lp.resolve();
 
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " value:"+var.getValue());
            }
            SscLogger.log("Valore f.o. :"+solution.getOptimumValue());  
        }
        else SscLogger.log("Soluzione non ottima. Tipo di soluzione:"+solution_type);
    }
}
 

