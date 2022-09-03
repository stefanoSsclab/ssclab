package xxmilp;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.pl.milp.util.LPThreadsNumber;
import it.ssc.ref.InputString;
 
public class Esempio1_10 {
     
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
        //lp.setParallelSimplex(false);
        //lp.setThreadsNumber(LPThreadsNumber.N_1);  
        SolutionType solution_type=lp.resolve();
         
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=lp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
        else if(solution_type==SolutionType.VUOTUM) {
            SscLogger.log("La fase 1 del simplesso non ha trovato soluzioni ammissibili:("+solution_type+")");
        }
        else if(solution_type==SolutionType.ILLIMITATUM) {
            SscLogger.log("Ottimo illimitato:("+solution_type+")");
        }
        else if(solution_type==SolutionType.MAX_ITERATIUM) {
            SscLogger.log("Raggiunto il numero massimo di iterazioni:("+solution_type+")");
        }
    }
}

