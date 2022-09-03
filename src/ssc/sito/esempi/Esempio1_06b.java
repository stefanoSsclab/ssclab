package ssc.sito.esempi;

import java.util.ArrayList;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import it.ssc.pl.milp.util.MILPThreadsNumber;
public class Esempio1_06b {
     
    public static void main(String[] args) throws Exception {
 
        ArrayList< String > constraints = new ArrayList< String >();
      
        constraints.add("min:120X1A1 + 140X1A2 + 160X1A3+ 120X1B1 + 140X1B2 + 160X1B3 +"+ 
        			    "120X1C1 + 140X1C2 + 160X1C3 +100X1d1 + 120X1d2 + 140X1d3 +"+ 
        				"100X1e1 + 120X1e2 + 140X1e3 +150X2A1 + 70X2A2 + 90X2A3 +"+ 
        			    "150X2B1 + 70X2B2 + 90X2B3 +130X2d1 + 50X2d2 + 70X2d3 +"+
        				"130X2e1 + 50X2e2 + 70X2e3 ");
        
        constraints.add("120X1A1 + 140X1A2 + 160X1A3 <=340");
        constraints.add("120X1B1 + 140X1B2 + 160X1B3 <=340");
        constraints.add("120X1C1 + 140X1C2 + 160X1C3 <=340");
        constraints.add("120X1d1 + 140X1d2 + 160X1d3 <=380");
        constraints.add("120X1e1 + 140X1e2 + 160X1e3 <=380");
        
        constraints.add("150X2A1 + 70X2A2 + 90X2A3 <=340");
        constraints.add("150X2B1 + 70X2B2 + 90X2B3 <=340");
        constraints.add("150X2d1 + 70X2d2 + 90X2d3 <=380");
        constraints.add("150X2e1 + 70X2e2 + 90X2e3 <=380");
        
        
        constraints.add("90X1A1 + 90X1A2 + 90X1A3 +  "
        		      + "90X1B1 + 90X1B2 + 90X1B3 +  "
        		      + "90X1C1 + 90X1C2 + 90X1C3 +  "
        		      + "30X1d1 + 30X1d2 + 30X1d3 +  "
        			  + "30X1e1 + 30X1e2 + 30X1e3 >= 680  ");
        
        constraints.add(  "90X2A1 + 90X2A2 + 90X2A3 +  "
  		      			+ "90X2B1 + 90X2B2 + 90X2B3 +  "
  		      			+ "30X2d1 + 30X2d2 + 30X2d3 +  "
  		      			+ "30X2e1 + 30X2e2 + 30X2e3 >= 490  ");
 
       
        constraints.add("90X1A1 + 90X1B1 + 90X1C1 + 30X1d1 + 30X1e1 +"+
        		        "90X2A1 + 90X2B1+ 30X2d1 +30X2e1 <= 620  ");
        
        constraints.add("90X1A2 + 90X1B2 + 90X1C2 + 30X1d2 + 30X1e2 +"+
		        		"90X2A2 + 90X2B2+ 30X2d2+ 30X2e2 <= 320  ");
        
        constraints.add("90X1A3 + 90X1B3 + 90X1C3 + 30X1d3 + 30X1e3 +"+
        				"90X2A3 + 90X2B3+ 30X2d3+ 30X2e3 <= 420  ");
        
        
        
        constraints.add("int X1A1 , X1A2 , X1A3 , X1B1 , X1B2 , X1B3, "+ 
			    "X1C1 , X1C2 , X1C3 , X1d1 , X1d2 , X1d3 ,"+ 
				"X1e1 , X1e2 , X1e3 , X2A1 , X2A2 , X2A3 ,"+ 
			    "X2B1 , X2B2 , X2B3 , X2d1 , X2d2 , X2d3 ,"+
				"X2e1 , X2e2 , X2e3 ");
        
        /*
        */
         
        //MILP lp = new MILP("C:\\ssc_project\\ssc\\dati_testo\\dises2.txt"); 
        MILP lp = new MILP(constraints); 
        //lp.setJustTakeFeasibleSolution(true);
		//lp.setThreadNumber(MILPThreadsNumber.N_4);
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) {
            Solution soluzione=lp.getSolution();
            Solution sol_relax=lp.getRelaxedSolution();
            Variable[] var_int=soluzione.getVariables();
            Variable[] var_relax=sol_relax.getVariables();
            for(int _i=0; _i< var_int.length;_i++) {
                SscLogger.log("Nome variabile :"+var_int[_i].getName() + " valore:"+var_int[_i].getValue()+ 
                              " ["+var_relax[_i].getValue()+"]");
            }
            for(SolutionConstraint sol_constraint: soluzione.getSolutionConstraint()) {
                SscLogger.log("Vincolo "+sol_constraint.getName()+" : valore="+sol_constraint.getValue() + 
                              "[ "+sol_constraint.getRel()+"  "+sol_constraint.getRhs()+" ]" );
            }
            SscLogger.log("Valore ottimo:"+soluzione.getOptimumValue()+" ["+sol_relax.getOptimumValue()+"]");
        }
    }
}