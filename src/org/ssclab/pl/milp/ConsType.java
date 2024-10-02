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
	EQ("eq"), 
	/**
	 * To define a constraint of the type &lt; = 
	 */
	LE("le"), 
	/**
	 * To define a constraint of the type &gt; = 
	 */
	GE("ge"), 
	/**
	 * To define integer variables
	 */
	INT("integer"), 
	/**
	 * To define binary variables
	 */
	BIN("binary"), 
	/**
	 * For defining Upper bounds 
	 */
	UPPER("upper"), 
	/**
	 * For defining Lower bounds
	 */
	LOWER("lower"),
	/**
	 * To define semicontinuous variables
	 */
	SEMICONT("semicont");
	
	/**
     * Constructor for ConsType enumeration.
     * 
     * @param value The value associated with the enumeration.
     */
	
	private String value;
	private ConsType(String value) { 
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
