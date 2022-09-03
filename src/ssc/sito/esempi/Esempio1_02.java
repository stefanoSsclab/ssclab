package ssc.sito.esempi;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.ConsType;
import it.ssc.pl.milp.Constraint;
import it.ssc.pl.milp.GoalType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.LinearObjectiveFunction;
import it.ssc.pl.milp.ListConstraints;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionConstraint;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
 
 
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
 
        ListConstraints constraints=new ListConstraints();
        for(int i=0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
 
        LP lp = new LP(fo,constraints); 
        //lp.setParallelSimplex(true);
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
    
    
    /*
     * Studio della matrice A durante il simplesso per verificare eventualli possibili risparmi di memoria. 
     * 
     * 
     * 1) I vettori riga A[i] vengono passati agli oggetti Constraint per la generazione del vincolo riga. 
     *    Questi vettori vengono utilizzati senza copia , ovvero sono gli stessi. 
     * 2) Metto gli oggetti Constraint in un arrayList e li passo al costruttore della classe LP
     *   
     * 3) il costruttore della classe LP utilizza l'oggetto f.o.  e la lista di vincoli per creare il PLProblem. 
     * 
     * 4) Il PLProblem (milp_original di dimensione N) viene creato dalla classe CreatePLProblem con metodo statico create(). 
     * 
     * 
     * */
    
    
    
    
    
    
}
