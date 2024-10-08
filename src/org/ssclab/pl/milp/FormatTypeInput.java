package org.ssclab.pl.milp;
/**
* This interface contains an enumeration that allows you to define the type 
* of format present in the Input object, passed as an argument to the LP or MILP 
* objects: Sparse or coefficient.
*/

public interface FormatTypeInput {
	/**
	 * Enumeration used to define the type of format utilized. 
	 * This enumeration allows you to define the type of format present in 
	 * the Input object passed as an argument - together with one of the two 
	 * values ​​of this enumeration, to indicate the format used in formulating 
	 * the problem: Sparse or Coefficients.
	 */
	
	public enum FormatType {
	
		/**
		 *  The type of format present in the Input object is sparse 
		 */
		SPARSE,
		/**
		 *  The type of format present in the Input object is coefficients 
		 */
		COEFF
		
	}

}
