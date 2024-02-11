package org.ssclab.context;

import org.ssclab.context.exception.InvalidSessionException;
import org.ssclab.io.DirectoryNotFound;

/*Interfaccia privata e di utilizzo del sistema */

public interface SessionIPRIV extends Session {
	
	public Config getConfig() throws InvalidSessionException;
	public String getPathCompiler() throws InvalidSessionException, DirectoryNotFound;
	public String getPathSorting() throws InvalidSessionException, DirectoryNotFound;
	public void generateExceptionOfSessionClose() throws InvalidSessionException;

}
