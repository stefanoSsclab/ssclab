package org.ssclab.pl.milp;

/**
 * Enumeration used to define the type of relationship in a Constraint object.
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 */

public enum  ConsType {
	/**
	 * To define a constraint of the type = 
	 */
	EQ, 
	/**
	 * To define a constraint of the type &lt; = 
	 */
	LE, 
	/**
	 * To define a constraint of the type &gt; = 
	 */
	GE, 
	/**
	 * To define integer variables
	 */
	INT, 
	/**
	 * To define binary variables
	 */
	BIN, 
	/**
	 * For defining Upper bounds 
	 */
	UPPER, 
	/**
	 * For defining Lower bounds
	 */
	LOWER,
	/**
	 * To define semicontinuous variables
	 */
	SEMICONT
	
}
