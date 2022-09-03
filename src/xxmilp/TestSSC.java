package xxmilp;


import it.ssc.pl.milp.*;

import java.util.ArrayList;

public class TestSSC {

    private double[][] A;
    private double[] b;
    private double[] c;
    private ConsType[] rel;

    public TestSSC(double[][] A, double[] b, double[] c, ConsType[] rel) {
        this.A = A;
        this.b = b;
        this.c = c;
        this.rel = rel;
    }

    public Variable[] LinearProgram() throws Exception {
        LinearObjectiveFunction f = new LinearObjectiveFunction(c, GoalType.MIN);
        ArrayList<Constraint> constraints = new ArrayList<>();
        for(int i = 0; i < A.length; i++) {
            constraints.add(new Constraint(A[i], rel[i], b[i]));
        }
        MILP lp = new MILP(f,constraints);
        SolutionType solution_type = lp.resolve();
        if(solution_type == SolutionType.OPTIMUM) {
            Solution solution = lp.getSolution();
            return solution.getVariables();
        }
        else {
            return null;
        }
    }

}

