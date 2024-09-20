package org.ssclab.pl.milp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.ssclab.i18n.RB;

/**
 * The JsonProblem class represents a problem in JSON format that can be loaded
 * either from a file or a string. It provides functionality to retrieve a 
 * BufferedReader to access the problem's content.
 */
public class JsonProblem {
	
	private String json=null;
	private Path path;
	
	 /**
     * Constructs a JsonProblem from a file path. The problem will be loaded
     * from the specified file.
     * 
     * @param path the path to the JSON file containing the problem
     */
	public JsonProblem(Path path) {
		this.path=path;
	}
	
	/**
     * Constructs a JsonProblem from a JSON string. The problem will be 
     * represented by the provided JSON content.
     * 
     * @param json the JSON string representing the problem
     */
	public JsonProblem(String  json) {
		this.json=json;
	} 
	
	/**
     * Retrieves a BufferedReader to read the problem's content. If the problem
     * was loaded from a string, a StringReader is used. If it was loaded from
     * a file, a BufferedReader from the file is returned.
     * 
     * @return a BufferedReader for reading the problem's content
     * @throws IOException if an I/O error occurs when reading from a file
     * @throws LPException if neither a JSON string nor a valid file path 
     *         is provided
     */
	BufferedReader getBufferedReader() throws IOException, LPException {
		if(json==null) {
			if( this.path!=null ) return Files.newBufferedReader(path);
			else throw new LPException(RB.getString("org.ssclab.pl.milp.json.msg1"));
		}
		else return new BufferedReader(new StringReader(json));
	}
}
