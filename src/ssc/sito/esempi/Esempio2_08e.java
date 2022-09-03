package ssc.sito.esempi;

import it.ssc.context.exception.InvalidSessionException;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;

import java.util.ArrayList;
import static it.ssc.pl.milp.LP.NaN;
 
public class Esempio2_08e {
     
	  public static void main(String arg[]) throws InvalidSessionException, Exception {
		  
		  /*si potrebbe pensare di completare l'algoritmo :
		   * 
		   * a) usando la funzione obiettivo in questione (se max -> min, 
		   *                                               coefficienti unitari con segno di quelli originali 
		   *                                               
		   *                                               Proviamo a ragionarci sopra e mettere gli stessi coefficienti 
		   *                                               delle variabili. Inoltre se il valore si discosta da quello impostato intero,
		   *                                               inpostarlo intero verso il segno in cui andava incrementandosi o decrementandosi. 
		   *                                               Controllare il valore della f.o. ? per capire se si sta ciclando. 
		   *                                               
		   *                                               Perche gli stessi coefficienti delle variabili vere: 
		   *                                               1) perche' la f.o. risulta con valori confrontabili con quella reale. 
		   *                                               2) ha lo stesso peso (sulla f.o) la variazione della variabile e 
		   *                                                 della sua distanza dj nel sistema. 
		   *                                                 Cosa accade se una variabile che ha un basso peso (coefficiente) 
		   *                                                 non e' intera ? Riusciamo a farla diventare intera ? NOn credo. 
		   *                                              
		   * 
		   * 
		   * */
		  
	        //double[]   c =  { 2, 2, 2, 2, 2 ,2, 2, 2, 2, 2,2 ,2 ,2, 3 ,1,1,1,1,1,1};
		    double[]   c =   { -2, -2, -2, -2, -2 ,-2, -2, -2, -2, -2,-2 ,-2 ,-2, -3   ,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		    //double[]   c =   { -1, -1, -1, -1, -1 ,-1, -1, -1, -1, -1,-1 ,-1 ,-1, -1     ,1,1,1,1,1,1,1};
	       double[]   b =  {1000, 1234, 1000, 1000, 1000, 1000, 1000, 1000, 1000,666,  -1,1,-2,2,-1.5,1.5,   0,0,-1,1,-0.5,0.5,  0,0,-1,1,-0.5,0.5,   -33,33,-34,34,-33.5,33.5,    -6,6,-7,7,-6.5,6.5 ,2.5,2.5,2.5,2.5,2.5 };
	         
	       double[][] A ={ { 2. , 9. ,7. ,5. ,9. ,6. ,3., 7., 8. ,7. ,5. ,3. ,1., 5.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0     },
	                        { 4. ,1. ,2. ,3. ,6. ,4. ,5. ,2. ,8. ,5. ,3. ,4., 7., 6.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0     },
	                        { 3. ,4. ,2. ,5. ,7. ,6. ,3. ,5. ,7. ,4. ,6. ,8. ,6., 7.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0    },
	                        { 4. ,6. ,9. ,8. ,7. ,6. ,5. ,4. ,3. ,2. ,3. ,5. ,6., 8.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0    },
	                        { 4. ,4. ,7. ,5. ,3. ,8. ,5. ,6. ,3. ,5. ,6. ,4. ,6., 9.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0     },
	                        { 9. ,4. ,2. ,5. ,5. ,6. ,3. ,2. ,7. ,4. ,4. ,8. ,1., 9.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0     },
	                        { 2. ,6. ,4. ,5. ,7. ,5. ,6. ,4. ,6. ,7. ,4. ,4. ,6., 0.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0    },
	                        { 4. ,6. ,9. ,8. ,3. ,6. ,5. ,5. ,3. ,2. ,9. ,5. ,6., 1.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0     },
	                        { 4. ,5. ,7. ,8. ,3. ,8. ,3. ,6. ,3. ,5. ,6. ,1. ,6. ,2.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0    },
	                        { 2., 2., 4., 3., 7. ,5. ,9. ,4. ,6. ,7. ,8. ,4., 6. ,3.   ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0     },
	                        
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0,-1, 0, 0, 0, 0  ,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 1, 0, 0, 0, 0  ,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0,-1, 0, 0, 0, 0  ,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 1, 0, 0, 0, 0  ,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0,-1, 0, 0, 0, 0  ,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 1, 0, 0, 0, 0  ,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0 }, 
	                                                                                                      
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 0,-1, 0, 0, 0  ,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 0, 1, 0, 0, 0  ,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 0,-1, 0, 0, 0  ,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 0, 1, 0, 0, 0  ,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 0,-1, 0, 0, 0  ,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0 }, 
	                        { 0, 0, 0, 0, 0 , 0, 0 ,0, 0, 0, 1, 0, 0, 0  ,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0 }, 
	                                                                                                      
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0,-1, 0, 0  ,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 1, 0, 0  ,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0,-1, 0, 0  ,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 1, 0, 0  ,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0,-1, 0, 0  ,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 1, 0, 0  ,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0 },  
	                                                                                                      
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0,-1, 0  ,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 1, 0  ,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0,-1, 0  ,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 1, 0  ,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0,-1, 0  ,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 1, 0  ,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0 },  
	                                                                                                      
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0,-1  ,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 1  ,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0,-1  ,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 1  ,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0,-1  ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1 },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 1  ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1 },
	                        
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 0  ,2,2,1, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0    },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 0  , 0, 0, 0,2,2,1, 0, 0, 0, 0, 0, 0,0,0,0    },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 0  , 0, 0, 0, 0, 0, 0,2,2,1, 0, 0, 0,0,0,0    },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 0  , 0, 0, 0, 0, 0, 0, 0, 0, 0,2,2,1,0,0,0    },  
	                        { 0, 0, 0, 0, 0, 0, 0  ,0, 0, 0,0, 0, 0, 0  , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,2,2,1 },
	                     

	                        
	                        };
	 
	        double[] upper ={ 190.5, 55.0, 55.0, NaN, NaN ,NaN ,NaN ,NaN ,35.0 ,NaN ,NaN ,NaN, 210.0,NaN,NaN ,NaN ,NaN,NaN ,NaN ,NaN ,NaN,NaN,NaN ,NaN ,NaN,NaN ,NaN ,NaN ,NaN };
	        //double[] integer ={ 0.0, 0.0, 0.0, 0.0, 0.0 ,0.0 ,0.0 ,0.0 ,1.0 ,1.0 ,1.0 ,1.0, 1.0 ,1.0};
	 
	        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MIN);
	 
	        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
	        for(int i=0; i< 10; i++) {
	            constraints.add(new Constraint(A[i], ConsType.LE, b[i])); 
	        }
	 
	        for(int i=10; i< 40; i++) {
	            constraints.add(new Constraint(A[i], ConsType.GE, b[i])); 
	        }
	        
	        for(int i=40; i< 45; i++) {
	            constraints.add(new Constraint(A[i], ConsType.EQ, b[i])); 
	        }
	        
	        constraints.add(new Constraint(upper,   ConsType.UPPER, NaN)); 
	        //constraints.add(new Constraint(integer, ConsType.INT , NaN)); 
	 
	        LP milp = new LP(f,constraints);
	        //milp.setJustTakeFeasibleSolution(true);
	        SolutionType solution=milp.resolve();
	        
	        if(solution==SolutionType.OPTIMUM) { 
	            Solution sol=milp.getSolution();
	            //Solution sol_relax=milp.getRelaxedSolution();
	            Variable[] var_int=sol.getVariables();
	           
	            for(int _i=0; _i< var_int.length;_i++) {
	                SscLogger.log("Nome variabile :"+var_int[_i].getName() + " valore:"+var_int[_i].getValue());
	            }
	            
	            for(SolutionConstraint sol_constraint: sol.getSolutionConstraint()) {
	                SscLogger.log("Vincolo "+sol_constraint.getName()+" : valore="+sol_constraint.getValue() + 
	                              "[ "+sol_constraint.getRel()+"  "+sol_constraint.getRhs()+" ]" );
	            }
	            SscLogger.log("valore ottimo:"+sol.getOptimumValue() ); 
	        }
	        SscLogger.log("sol:"+solution ); 
	    }
}
