package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.*;
import it.ssc.pl.milp.util.MILPThreadsNumber;

import java.util.ArrayList;
import static it.ssc.pl.milp.LP.NaN;
public class Esempio_discarica_1 {
     
    public static void main(String[] args) throws Exception {
 
        double A[][]={ 
                             
                
               //vincoli kilometraggio per tipologia 
                { 35,	45,	70,	 0,	 0,	 0,	0,	0,	0,	0,	0,	0,	-300,	0,	0	,0     },  // <  300 
                {  0,	 0,	 0,	55,	65,	75,	0,	0,	0,	0,	0,	0,	  0,  -300,	0	,0     },  // <  300 
                {  0,	 0,	 0,	0,	 0,	 0,	35,	45,	70,	0,	0,	0,	  0,    0,	-400	,0 },  // <  400
                {  0,	 0,	 0,	0,	 0,	 0,	0,	0,	0,	55,	65,	75,	  0,    0,	0	,-400  },  // <  400
                
               
                //occorre smaltire tutto tranne 5 t a sede
                {  14,	 14,   14,	0,	 0,	 0,	 5,	5,	5,	0,	0,	0,	  0,    0,	0	,0  }, //  > 155
                {  0,	 0,	   0,  14,	14, 14,	 0,	0,	0,  5,	5,	5,	  0,    0,	0	,0  }, // >  230 
                
                
                //
                {  14,	 0,	 0 , 14, 0,	 0,	 5,	0,	0,	5,	0,	0,	  0,    0,	0	,0  },     // < 200
                {  0,	 14, 0,  0,	 14, 0,	 0,	5,	0,  0,	5,	0,	  0,    0,	0	,0  },    //  < 150 
                {  0,	 0,	 14,  0, 0,	 14, 0,	0,	5,  0,	0,	5,	  0,    0,	0	,0  },    //  < 130
                
                
                //integer 
                
                {  1,	 1,	 1,  1, 1,	 1, 1,	1,	1,  1,	1,	1,	  1,    1,	1	,1  }
                
                } ;
        double b[]= { 0, 0, 0, 0,  155, 230,  200,150,130 ,         NaN};
        double c[]= { 1466.5,	1435.5,	1323,	1544.5,	1513.5,	1342.5,	555.5,	553.5,	536,	601.5,	599.5,	547.5,	500,	500,	350	,  350   };  
 
        ConsType[] rel= { ConsType.LE,ConsType.LE,ConsType.LE,ConsType.LE,      ConsType.GE, ConsType.GE  ,    ConsType.LE,ConsType.LE,ConsType.LE,        ConsType.INT    };
 
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MIN);
 
        ArrayList< Constraint > constraints = new ArrayList< Constraint >();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
 
        MILP lp = new MILP(f,constraints); 
       // lp.setThreadNumber(MILPThreadsNumber.N_4);
        SolutionType solution_type=lp.resolve();
 
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            for(SolutionConstraint sol_constraint: solution.getSolutionConstraint()) {
                SscLogger.log("Vincolo "+sol_constraint.getName()+" : valore="+sol_constraint.getValue() + 
                              "[ "+sol_constraint.getRel()+"  "+sol_constraint.getRhs()+" ]" );
            }
            
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
    }
}