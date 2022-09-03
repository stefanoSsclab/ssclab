package ssc.sito.esempiEn;

import it.ssc.context.Context;
import it.ssc.context.Session;
import it.ssc.library.Library;
import it.ssc.log.SscLogger;
import it.ssc.pl.milp.FormatTypeInput.FormatType;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;
import it.ssc.ref.Input;
import java.sql.Connection;
import oracle.jdbc.pool.OracleDataSource;
  
public class Example9 {
      
    public static void main(String[] args) throws Exception {
          
        Session session = null;
        try {
            session = Context.createNewSession();
            Library lib_ora=session.addLibrary("DB_ORACLE", connOracle());
          
            Input pl_oracle=lib_ora.getInput("TAB_PL_PROBLEM");
            LP lp = new LP(pl_oracle,session,FormatType.SPARSE); 
            SolutionType solution_type=lp.resolve();
              
            if(solution_type==SolutionType.OPTIMUM) { 
                Solution solution=lp.getSolution();
                for(Variable var:solution.getVariables()) {
                    SscLogger.log("Variable name :"+var.getName() + " value:"+var.getValue());
                }
                SscLogger.log("o.f. value:"+solution.getOptimumValue()); 
            }   
        } 
        finally {
            session.close();
        }
    }
      
      
    private static Connection connOracle() throws Exception {
        OracleDataSource ods = new OracleDataSource();
        String URL = "jdbc:oracle:thin:@//192.168.243.134:1521/XE";
        ods.setURL(URL);
        ods.setUser("user_pl");
        ods.setPassword("ora655"); 
        return ods.getConnection();
    }
}