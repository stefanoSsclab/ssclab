package ssc.sito.esempi;




import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;


import java.util.ArrayList;
import static it.ssc.pl.milp.LP.NaN;
 
public class Esempio31 { 
    public static void main(String arg[]) throws Exception {
    	
    	int mb = 1024*1024;
    	
    	//Getting the runtime reference from system
    	Runtime runtime = Runtime.getRuntime();
    	System.out.println("Used Memory:"+ (runtime.totalMemory() - runtime.freeMemory()) / mb);
         
        final int M = 620;  // rows
        final int N = 1550;  // cols
        
        
        /*  1) Attivo
         *  senza parallelismo 
         *  1:45 
         *  1:46
         *  1:43
         *  1:42 (con TBEX_I[])
         *  
         *  2) 
         *  parallelismo con due cicli 
         * 1:47
         * 1:47
         * 
         * 
         * 3) 
         * parallelismo su formula originale 
         * 
         * 2:55
         * 2:55
         * 
         * 4) 
         * parallelismo con Row 
         * 
         * 1:48
         * 1:46 
         * 
         *  5) 
         * parallelismo con Row  e TBEX[out] risolto
         * 1:45
         * 1:46
         * 
         * 6) 
         * parallelismo con Row  e TBEX[out] risolto , ma senza creazione PRO_IN_DIV_PERNO
         * 1:46
         * 1:46
         * 
         *  7) 
         * parallelismo con Row  e TBEX[out] risolto , ma senza creazione PRO_IN_DIV_PERNO
         * e un solo ciclo
         * 
         * 1:46
         * 1:46
         * 
         */
        
        
         
        double[] c = new double[N];
        double[] b = new double[M];
        double[][] A = new double[M][N];
        for (int j = 0; j < N; j++)      c[j] =  3;
        for (int i = 0; i < M; i++)      b[i] = 10000;
        double value=0;
        for (int i = 0; i < M; i++) 
            for (int j = 0; j < N; j++)   { 
            	value=i+j;
            	if(i>=j) value=i-j;
            	if(j%100==0 & i%2==0) value=0;
            	A[i][j] = value;
            }
         
        double[] integer=new double[N] ;
        int alter=1;
        for (int j = 0; j < N; j++)  {
        	integer[j] = 1*alter;
        	if (alter==0) alter=1;
        	else if (alter==1) alter=0;
        }
        System.out.println("Used Memory:" 
    			+ (runtime.totalMemory() - runtime.freeMemory()) / mb);
        
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MAX);
 
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], ConsType.LE, b[i])); 
        }
        
        System.out.println("Used Memoryn:" 
    			+ (runtime.totalMemory() - runtime.freeMemory()) / mb);
        
        constraints.add(new Constraint(integer, ConsType.INT , NaN)); 
        
        //MILP lp = new MILP(f,constraints);
        LP lp = new LP(f,constraints);
        //lp.setParallelSimplex(false);
        //lp.setThreadsNumber(LPThreadsNumber.AUTO);
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
 

