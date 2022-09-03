package ssc.sito.esempiEn;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
  
public class Example2_14 {
    public static void main(String[] args) throws Exception {
  
        String milp_string=
                  
                        "-2 -1   min        ."   +"\n"+
                        "-1 -1   ge        -5"   +"\n"+
                        "1  -1   ge         0"   +"\n"+
                        "-6 -2   ge       -21"   +"\n"+
                        "4   3  upper       ."   +"\n"+
                        "1   1  integer     ."   +"\n" ;  
  
        InputString milp_input = new InputString(milp_string);
        milp_input.setInputFormat("X1-X2:double, TYPE:varstring(20),  RHS:double");
  
        MILP milp=new MILP(milp_input);
        milp.setJustTakeFeasibleSolution(true);
        SolutionType solution_type= milp.resolve();
  
        if(solution_type==SolutionType.FEASIBLE) { 
            Solution solution=milp.getSolution();
            for(Variable var:solution.getVariables()) {
                SscLogger.log("Variable name :"+var.getName() + " Value:"+var.getValue());
            }
            SscLogger.log("O.F. Value:"+solution.getOptimumValue());
        }   
    }
}
