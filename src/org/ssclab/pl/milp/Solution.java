package org.ssclab.pl.milp;

/**
 * 
 * 
 *  This interface allows accessing the values taken by the n variables of the optimal solution.
 * 
 * @author Stefano Scarioli 
 * @version 1.0 
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */



public interface Solution {
	
	/**
     * Retrieves an array of Variable objects from which to obtain the characteristics
     * and the optimal value assumed by each variable.
     * 
     * @return An array of Variable objects from which to obtain the characteristics and the optimal value assumed by each variable.
     */
	public Variable[] getVariables();
	/**
     * Retrieves the optimal value assumed by the objective function.
     * 
     * @return The optimal value assumed by the objective function.
     */
	public double getOptimumValue();
	/**
     * Retrieves the type of solution obtained.
     * 
     * @return The type of solution obtained.
     */
	public SolutionType getTypeSolution(); 
	/**
     * Retrieves an array of SolutionConstraint objects from which to obtain the value that
     * each constraint assumes by substituting the optimal solution for the unknown variables.
     * 
     * @return An array of SolutionConstraint objects from which to obtain the value that each constraint assumes by substituting the optimal solution for the unknown variables.
     */
	public SolutionConstraint[] getSolutionConstraint();
	
	 /**
     * Retrieves the value assumed by the objective function.
     * 
     * @return The value assumed by the objective function.
     */
	public double getValue();
	
	/**
     * Retrieves the values of the variables in an array.
     * 
     * @return The values of the variables in an array.
     */
	public double[] getValuesSolution();
	
}
