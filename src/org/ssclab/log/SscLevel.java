package org.ssclab.log;

import java.util.logging.Level;

/**
 * The SscLevel class defines custom logging levels for the SSC application.
 * These levels extend the standard logging levels provided by {@link java.util.logging.Level}.
 * 
 * <p>Custom log levels include:
 * <ul>
 *   <li>LOG - General logging level (priority 811)</li>
 *   <li>TIME - Logging level for timing-related messages (priority 821)</li>
 *   <li>NOTE - Logging level for notes and annotations (priority 831)</li>
 *   <li>ERROR - Logging level for error messages (priority 990)</li>
 * </ul>
 */

public class SscLevel extends Level {

	 /**
     * Custom log level for general messages.
     * It has a priority value of 811.
     */
	public static final Level LOG=new SscLevel("LOG",811);
	
	 /**
     * Custom log level for timing-related messages.
     * It has a priority value of 821.
     */
	public static final Level TIME=new SscLevel("TIME",821);
	
	 /**
     * Custom log level for notes or annotations.
     * It has a priority value of 831.
     */
	public static final Level NOTE=new SscLevel("NOTE",831);
	 /**
     * Custom log level for error messages.
     * It has a priority value of 990.
     */
	public static final Level ERROR=new SscLevel("ERROR",990);

	
	/**
     * Constructs a new SscLevel object with a specified name and integer priority.
     *
     * @param name  the name of the log level (e.g., "LOG", "TIME", "NOTE", "ERROR")
     * @param value the integer priority value for this log level
     */
	protected SscLevel(String name, int value) {
		super(name, value);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
