package xxmilp;



import it.ssc.log.SscLogger;
import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.pl.milp.util.MILPThreadsNumber;
import it.ssc.ref.InputString;
 
public class Esempio2_1 {
    public static void main(String[] args) throws Exception { 
    	
 
        String milp_string=
                 
                        "3 1 4 7 8 min      . "  +"\n"+
                        "5 2 0 3 0 le       9 "  +"\n"+
                        "3 1 1 0 5 ge       12"  +"\n"+
                        "6 3 4 5 0 ge       124" +"\n"+
                        "1 3 0 3 6 ge       854" +"\n"+
                        "0 1 1 0 0 lower    . "  +"\n"+
                        ". 6 . . . upper    . "  +"\n"+
                        "0 1 1 1 1 integer  . "  +"\n" ;  
 
        InputString milp_input = new InputString(milp_string);
        milp_input.setInputFormat("X1-X5:double, TYPE:varstring(20),  RHS:double");
 
        MILP milp=new MILP(milp_input);
		//milp.setThreadNumber(MILPThreadsNumber.N_2);
        SolutionType solution_type= milp.resolve();
 
        if(solution_type==SolutionType.OPTIMUM) { 
            Solution solution=milp.getSolution();
            for(Variable var:solution.getVariables()) { 
                SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
            }
            SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
        }   
    }
}

