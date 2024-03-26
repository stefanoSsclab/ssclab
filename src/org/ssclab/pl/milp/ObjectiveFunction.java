package org.ssclab.pl.milp;

/**
 * An interface representing an objective function in linear programming.
 */

interface ObjectiveFunction {
	
	 /**
     * Enum for specifying the target of the objective function (MAX or MIN).
     */
	
	public enum TARGET_FO {MAX, MIN}; 
	
	/**
     * Gets the coefficient of the variable at index j in the objective function.
     * 
     * @param j The index of the variable
     * @return The coefficient of the variable at index j
     */
	public double getCj(int j) ;
	
	/**
     * Gets the target type of the objective function (MAX or MIN).
     * 
     * @return The target type of the objective function
     */
	
	public TARGET_FO getType() ;
	
	/**
     * Creates a deep copy of the objective function.
     * 
     * @return A new instance of ObjectiveFunction with the same values as this instance
     */
		
	public ObjectiveFunctionImpl clone() ;


}
