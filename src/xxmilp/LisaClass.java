package xxmilp;
import it.ssc.pl.milp.*;

public class LisaClass {
	
	  public static void main(String[] args) throws Exception {
	        double[][] A1 = {{0.01, 0.0, 0.0},
	                {0.0, 0.01, 0.0},
	                {1.0, 1.0, 1.0},
	                {1.0, 1.0, 1.0}};
	        double[] b1 = {0.0, 0.06, 1.0, Double.NaN};
	        double[] c1 = {0.0, 0.0, 0.1};
	        ConsType[] rel1 = {ConsType.LE, ConsType.LE, ConsType.GE, ConsType.BIN};

	        double[][] A2 = {{0.03, 0.01, 0.0, 0.0, 0.0, 0.0},
	                {0.0, 0.0, 0.03, 0.01, 0.0, 0.0},
	                {1.0, 0.0, 1.0, 0.0, 1.0, 0.0},
	                {0.0, 1.0, 0.0, 1.0, 0.0, 1.0},
	                {1.0, 1.0, 1.0, 1.0, 1.0, 1.0}};
	        double[] b2 = {0.0, 0.06, 1.0, 1.0, Double.NaN};
	        double[] c2 = {0.0, 0.0, 0.0, 0.0, 0.1, 0.1};
	        ConsType[] rel2 = {ConsType.LE, ConsType.LE, ConsType.GE, ConsType.GE, ConsType.BIN};

	        TestSSC tester = new TestSSC(A1, b1, c1, rel1);
	        Variable[] result = tester.LinearProgram();
	        System.out.println(result.length);

	        TestSSC tester2 = new TestSSC(A2, b2, c2, rel2);
	        Variable[] result2 = tester2.LinearProgram();
	        System.out.println(result2.length);
	    }

}
