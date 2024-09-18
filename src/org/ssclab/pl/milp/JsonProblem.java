package org.ssclab.pl.milp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.ssclab.i18n.RB;

public class JsonProblem {
	
	private String json=null;
	private Path path;
	
	public JsonProblem(Path path) {
		this.path=path;
	}
	
	public JsonProblem(String  json) {
		this.json=json;
	} 
	
	BufferedReader getBufferedReader() throws IOException, LPException {
		if(json==null) {
			if( this.path!=null ) return Files.newBufferedReader(path);
			else throw new LPException(RB.getString("org.ssclab.pl.milp.json.msg1"));
		}
		else return new BufferedReader(new StringReader(json));
	}
}
