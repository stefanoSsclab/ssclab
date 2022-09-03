package ssc.sito.esempi2;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.MILP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.InputString;
  
public class Esempio2_14 {
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
            Solution sol=milp.getSolution();
            Solution sol_relax=milp.getRelaxedSolution();
            Variable[] var_int=sol.getVariables();
            Variable[] var_relax=sol_relax.getVariables();
            for(int _i=0; _i< var_int.length;_i++) {
                SscLogger.log("Nome variabile :"+var_int[_i].getName() + " valore:"+var_int[_i].getValue()+ 
                              " ["+var_relax[_i].getValue()+"]");
            }
            SscLogger.log("valore ottimo:"+sol.getOptimumValue() +" ["+sol_relax.getOptimumValue()+"]"); 
        }   
    }
}

