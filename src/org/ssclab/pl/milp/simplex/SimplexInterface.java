package org.ssclab.pl.milp.simplex;


import org.ssclab.pl.milp.util.LPThreadsNumber;
import org.ssclab.pl.milp.SolutionType;


public interface SimplexInterface {
	
	public SolutionType runPhaseOne()  throws Exception ;
	public long getNumIterationPhaseOne();
	public long getNumIterationPhaseTotal() ;
	public SolutionType runPhaseTwo() throws Exception;
	public double[] getFinalValuesBasis() ;
	public int[] getFinalBasis();
	public void setNumIterationMax(long num_iteration_max);
	public void setMilp(boolean isMilp) ;
	public void setThreadsNumber(LPThreadsNumber isParallelSimplex) ;
	
}
