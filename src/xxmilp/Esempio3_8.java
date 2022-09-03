package xxmilp;



import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;

import java.util.ArrayList;
import static it.ssc.pl.milp.LP.NaN;
 
public class Esempio3_8 {
	 public static void main(String arg[]) throws Exception {
         
	        final int M = 10244;  // rows
	        final int N = 12192;  // cols
	         
	        double[] c = new double[N];
	        double[] b = new double[M];
	        double[][] A = new double[M][N];
	        for (int j = 0; j < N; j++)      c[j] =  3;
	        for (int i = 0; i < M; i++)      b[i] = 10000;
	        double value=0;
	        for (int i = 0; i < M; i++) 
	            for (int j = 0; j < N; j++)   { 
	            	value=i+j;
	            	if(i>j) value=i-j;
	            	A[i][j] = value;
	            }
	         
	        double[] integer=new double[N] ;
	        int alter=1;
	        for (int j = 0; j < N; j++)  {
	        	integer[j] = 1*alter;
	        	if (alter==0) alter=1;
	        	else if (alter==1) alter=0;
	        }
	        
	        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
	 
	        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
	        for(int i=0; i < A.length; i++) {
	            constraints.add(new Constraint(A[i], ConsType.LE, b[i])); 
	        }
	        
	        //constraints.add(new Constraint(integer, ConsType.INT , null)); 
	        
	        MILP lp = new MILP(f,constraints);
	        //lp.setCEpsilon(EPSILON._1E_M5);
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
 