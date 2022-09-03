package xxmilp;

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
 
public class Esempio1_9 {
     
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
                    SscLogger.log("Nome variabile :"+var.getName() + " valore:"+var.getValue());
                }
                SscLogger.log("Valore ottimo:"+solution.getOptimumValue());
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

/*
 * 
insert into TAB_PL_PROBLEM values( "MAX",     null,    "costo",      null   );
 insert into TAB_PL_PROBLEM values( "GE" ,     null ,   "row1" ,      null  );
 insert into TAB_PL_PROBLEM values( "LE" ,     null ,   "row2" ,      null  );
 insert into TAB_PL_PROBLEM values( "EQ" ,     null ,   "row3" ,      null  ); 
 insert into TAB_PL_PROBLEM values( "UPPER",   null   , "lim_sup",    null  ); 
 insert into TAB_PL_PROBLEM values( "LOWER",   null  ,  "lim_inf",    null   );     
 insert into TAB_PL_PROBLEM values(  null      X1    costo      1   );
 insert into TAB_PL_PROBLEM values(  null      X1    row1       1    );
 insert into TAB_PL_PROBLEM values( null      X1    row2       1   );
 insert into TAB_PL_PROBLEM values(  null      X1    row3      -5   );
 insert into TAB_PL_PROBLEM values(  null      X1    lim_sup    1   );
 insert into TAB_PL_PROBLEM values( null      X1    lim_inf   -1   );       
 insert into TAB_PL_PROBLEM values( null      X2    costo      3   );
 insert into TAB_PL_PROBLEM values( null      X2    row1       1   ); 
 insert into TAB_PL_PROBLEM values( null      X2    row2     1.4   );
 insert into TAB_PL_PROBLEM values( null      X2    row3       3   );
 insert into TAB_PL_PROBLEM values( null      X2    lim_sup    null   );
 insert into TAB_PL_PROBLEM values( null      X2    lim_inf    null   );      
 insert into TAB_PL_PROBLEM values( null      RHS   row1       1    );
 insert into TAB_PL_PROBLEM values( null      RHS   row2       6   );
 insert into TAB_PL_PROBLEM values( null      RHS   row3       5   );
 * 
 * 
 */
 

