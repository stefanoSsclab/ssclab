package org.ssclab.pl.milp;

/**
 * This enumeration defines the different types of results that the two-phase method returns.
 * 
 * @author Stefano Scarioli 
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public enum SolutionType { 
	/**
	  * The problem has an optimal solution.
	 */
	OPTIMUM ("optimal solution"), 
	/**
	 * The problem has unlimited optimal solution.
	 */
	ILLIMITATUM ("Solution unlimited/optimal unlimited"), 
	/**
	 * The algorithm stopped because the maximum number of iterations was reached.
	 */
	MAX_ITERATIUM ("Reached the maximum number of iterations"), 
	/**
	 * The branch and bound algorithm stopped because the maximum number of simplexes was reached.
	 */
	MAX_NUM_SIMPLEX ("reached the maximum number of simplex"), 
	/**
	 * The problem has no feasible solutions. The set of feasible solutions is empty.
	 */
	VUOTUM ("no Solutions / Empty"),
	
	
	/**
	 *Phase ONE - The condition for the existence of feasible solutions is not satisfied 
	 *because |z| &gt; epsilon. The epsilon value can be adjusted using the setCEpsilon() method. 
	 */
	PHASE_ONE_GT_EPS ("Phase one simplex : |z| > epsilon"),
	
	
	
	/**
	  * The problem has feasible solutions.
	 */
	
	FEASIBLE ("Feasible solution")
	;  
	
	public static final SolutionType INFEASIBLE=VUOTUM;
	public static final SolutionType UNLIMITED=ILLIMITATUM;
	public static final SolutionType UNBOUNDED=ILLIMITATUM;
	public static final SolutionType OPTIMAL=OPTIMUM;
	public static final SolutionType MAX_ITERATIONS=MAX_ITERATIUM;
	
	/**
     * Constructor for SolutionType enumeration.
     * 
     * @param value The value associated with the enumeration.
     */
	
	private String value;
	private SolutionType(String value) { 
		this.value=value;
	}
	
	/**
     * Retrieves the value associated with the enumeration.
     * 
     * @return The value associated with the enumeration.
     */
	
	public String getValue() {
		return this.value;
	}
	
	 /**
     * Returns a string representation of the enumeration value.
     * 
     * @return A string representation of the enumeration value.
     */
	
	public String toString() {
		return this.value;
	}
}

