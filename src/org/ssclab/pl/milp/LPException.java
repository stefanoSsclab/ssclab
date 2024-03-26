package org.ssclab.pl.milp;

/**
 * This class represents an exception specific to Linear Programming (LP) operations.
 * It extends the Exception class.
 * 
 * @author Stefano Scarioli
 */

public class LPException  extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	/**
     * Constructs an LPException with the specified detail message.
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */

	public LPException(String message) {
		super(message);
}

}
