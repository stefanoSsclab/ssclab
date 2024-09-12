package org.ssclab.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The SscLogger class is a custom logging utility for the SSC application.
 * It provides a simplified interface to log messages with different levels of severity
 * to either the console or a file. It also supports custom log levels defined in {@link SscLevel}.
 */

public class SscLogger {
	
	private static Logger ssc_logger=null;
	
	static {
		initLogger();
	}
	
	 /**
     * Initializes the logger with the name "SSC" and sets the default log level to INFO.
     * Removes any existing handlers and sets the output to the console.
     */
	
	private static void  initLogger() {
		
		ssc_logger=Logger.getLogger("SSC");
		ssc_logger.setLevel(Level.INFO);
		/*rimuove gli handler per il logger SSC*/
		removeHandler();
		setLogToConsole();
	}
	
	/**
     * Returns the logger instance used by the application.
     *
     * @return the SSC logger instance
     */
	
	public static Logger getLogger() {
		return ssc_logger;
	}
	
	/**
     * Logs a message with a custom log level {@link SscLevel#LOG}.
     *
     * @param message the message to be logged
     */
	
	public static void log(String message) {
		ssc_logger.log(SscLevel.LOG,message);
	}
	
	/**
     * Logs an error message with a custom log level {@link SscLevel#ERROR}.
     *
     * @param message the error message to be logged
     */
	
	public static void error(String message) {
		ssc_logger.log(SscLevel.ERROR,message);
	}
	
	
	/**
     * Logs an informational message with the log level INFO.
     *
     * @param message the info message to be logged
     */
	public static void info(String message) {
		ssc_logger.info(message);
	}
	
	
	/**
     * Logs a detailed message with the log level FINE.
     *
     * @param message the fine-grained message to be logged
     */
	public static void fine(String message) {
		ssc_logger.fine(message);
	}
	
	 /**
     * Logs a warning message with the log level WARNING.
     *
     * @param message the warning message to be logged
     */
	
	public static void warning(String message) {
		ssc_logger.warning(message);
	}
	
	/**
     * Logs a timing message with a custom log level {@link SscLevel#TIME}.
     *
     * @param message the timing message to be logged
     */
	public static void time(String message) {
		ssc_logger.log(SscLevel.TIME,message);
	}
	
	  /**
     * Logs a configuration message with the log level CONFIG.
     *
     * @param message the configuration message to be logged
     */
	public static void config(String message) {
		ssc_logger.config(message);
	}
	
	 /**
     * Logs a note with a custom log level {@link SscLevel#NOTE}.
     *
     * @param message the note message to be logged
     */
	
	public static void note(String message) {
		ssc_logger.log(SscLevel.NOTE,message);
	}
	
	 /**
     * Sets the log level for the logger.
     *
     * @param level the new log level (e.g., INFO, WARNING, SEVERE)
     */
	
	public static void setLevel(Level level) {
		ssc_logger.setLevel(level);
	}
	
	 /**
     * Logs a formatted message using the {@link String#format(String, Object...)} method.
     *
     * @param message the format string
     * @param obj     the arguments to be used in the format string
     */
	
	
	public static void logFormatted(String message,Object... obj) {
		String new_message=String.format(message, obj);
		ssc_logger.log(SscLevel.LOG,new_message);
	}
	
	 /**
     * Sets the logger to output log messages to a file.
     *
     * @param file_name the name of the log file
	 * @throws IOException  if error read file 
	 * @throws SecurityException if error Security  
     */
	
	public static void setLogToFile(String file_name) throws SecurityException, IOException {
		setLogToFile(file_name,false);
	}
	
	/**
     * Sets the logger to output log messages to a file with an option to append to the file.
     *
     * @param file_name the name of the log file
     * @param append    if true, log messages will be appended to the existing file
	 * @throws IOException  if error read file 
	 * @throws SecurityException if error Security  
     */
	
	public static void setLogToFile(String file_name, boolean append) throws SecurityException, IOException {
		removeHandler();
		FileHandler new_handler=new FileHandler(file_name,append);
		new_handler.setLevel(Level.INFO);
		new_handler.setFormatter(new SscFormatter());
		ssc_logger.setUseParentHandlers(false);
		ssc_logger.addHandler(new_handler);
	}
	
	 /**
     * Sets the logger to output log messages to the console.
     */
	
	public static void setLogToConsole() {
		Handler new_handler=new ConsoleHandler();
		new_handler.setLevel(Level.ALL);
		new_handler.setFormatter(new SscFormatter());
		ssc_logger.setUseParentHandlers(false);
		ssc_logger.addHandler(new_handler);
		
	}
	
	private static void removeHandler() {
		Handler[] handlers = ssc_logger.getHandlers();
		for(Handler handler : handlers) {
			ssc_logger.removeHandler(handler);
		}
	}
}