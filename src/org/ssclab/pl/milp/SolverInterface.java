package org.ssclab.pl.milp;
import org.ssclab.pl.milp.simplex.SimplexException;

 interface SolverInterface {

	public SolutionType resolve() throws Exception;
	public Solution getSolution() throws SimplexException;
	public double[] getValuesSolution() throws SimplexException;
	public boolean isJustTakeFeasibleSolution();
	public void setJustTakeFeasibleSolution(boolean isStopPhase2);

}
