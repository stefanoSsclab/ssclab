package org.ssclab.pl.milp;

/**
 * This interface allows accessing the optimal value assumed by the j-th variable
 * of the LP problem (j=1..N). In addition to the optimal value, it is possible to retrieve
 * other characteristics. 
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 *
 */

public interface Variable {
	/**
	 * 
	 * Retrieves the name of the variable.
	 * 
	 *  @return The name of variable
	 */
	
	public String getName() ;
	/**
	 * 
	 * Retrieves the type of variable (integer, binary, real).
	 * 
	 * @return The type of variable
	 */

	public TYPE_VAR getType() ;
	/**
	 * 
	  * Retrieves its upper bound if set (by default it is + &infin;)
	  * 
	  *  @return The upper bound of variable
	 */

	public double getUpper() ;
	
	/**
	 * 
	* Retrieves its lower bound.
	* 
	* 
	*  @return The lower bound of variable
	 */

	public double getLower() ;
	
	/**
	 * 
	 * Retrieves true if the variable is free. To make a variable free,
     * it must have been defined for the variable either a negative upper bound,
     * or a negative lower bound, or indefinite (- &infin;).
     * 
     * @return True if the variable is free. 
	 */

	public boolean isFree() ;
	
	/**
     * Retrieves the optimal value assumed by the variable within the determined optimal solution.
     * 
     * @return The optimal value assumed by the variable within the determined optimal solution.
     */
	
	public double getValue() ;
	
	/**
     * Defines the type of variable: integer, binary, or real.
     * 
    */
	
	public enum TYPE_VAR {
		REAL("continuous"), 
		INTEGER("integer"), 
		BINARY("binary");
		
		private String value;
		private TYPE_VAR(String value) { 
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
		
		
		}; 

}
