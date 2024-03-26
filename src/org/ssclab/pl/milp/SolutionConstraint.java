package org.ssclab.pl.milp;

/**

 * * This interface allows accessing a constraint of the LP problem where each unknown variable has been assigned
 * the optimal value. It is possible to obtain the LHS value of the constraint based on the optimal solution.
 * By LHS component we mean the linear equation that occupies the left-hand side of the constraint. 
 * Given a constraint X1 + 3X2 &#x2265; 7, the LHS component is given by the part X1 + 3X2.
 * 
 * 
 
 * @author Stefano Scarioli 
 * @version 1.0 
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public interface SolutionConstraint {
	
	/**
     * Retrieves the fixed component Rhs of the constraint.
     * 
     * @return The fixed component Rhs of the constraint.
     */
	public double getRhs() ;
	 /**
     * Retrieves the value that the LHS component assumes by substituting the unknown variables
     * with the determined optimal solution.
     * 
     * @return The value that the LHS component assumes by substituting the unknown variables with the determined optimal solution.
     */
	public double getValue() ;
	
	/**
     * Retrieves the type of constraint (GE, LE, EQ).
     * 
     * @return The type of constraint (GE, LE, EQ).
     */
	public ConsType getRel() ;
	/**
     * Retrieves the name of the constraint.
     * 
     * @return The name of the constraint.
     */
	public String getName() ;

}
