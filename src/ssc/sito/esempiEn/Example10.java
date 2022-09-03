package ssc.sito.esempiEn;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
 
public class Example10 {
     
    public static void main(String[] args) throws Exception {
 
        String lp_string = 
                            "5  4   1    3   max     .   \n" +  
                            "4  3   1    1   ge      2   \n" +    
                            "1 -2   1   -1   le      2   \n" +          
                            "3  2   1  1.4   le      6   \n" +  
                            "9  8   4  1.7   le      7   \n" +  
                            "5  3  -1  2.4   le      9   \n" +  
                            "3 -2  -5    3   le      5      ";
             
 
        InputString lp_input = new InputString(lp_string); 
        lp_input.setInputFormat("V1-V4:double, TYPE:varstring(8), RHS:double"); 
 
        LP lp = new LP(lp_input); 
        SscLogger.log("Numero di iterazioni di default:"+lp.getNumMaxIteration());
        lp.setNumMaxIteration(5);
        lp.setJustTakeFeasibleSolution(true);  //imposto la ricerca di una soluzione ammissibile , 
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.FEASIBLE) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Variable  name :"+var.getName() + " value:"+var.getValue());
            }
            SscLogger.log("o.f. value of feasible solution:"+solution.getOptimumValue());
        }   
        else if(solution_type==SolutionType.VUOTUM) {
            SscLogger.log("Phase 1 of the simplex did not find feasible solutions:("+solution_type+")");
        }
        else if(solution_type==SolutionType.ILLIMITATUM) {
            SscLogger.log("The problem has great Unlimited:("+solution_type+")");
        }
        else if(solution_type==SolutionType.MAX_ITERATIUM) {
            SscLogger.log("Max iteration:("+solution_type+")");
        }
        else if(solution_type==SolutionType.OPTIMUM) { 
            // this section will never be reached as it has been set
            // setJustTakeFeasibleSolution (true), the simplex can only return feasible solutions
        }
         
    }
}