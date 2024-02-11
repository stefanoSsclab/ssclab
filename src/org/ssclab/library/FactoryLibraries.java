package org.ssclab.library;

import org.ssclab.context.exception.InvalidSessionException;
import org.ssclab.io.DirectoryNotFound;
import org.ssclab.library.exception.InvalidLibraryException;
import org.ssclab.library.exception.LibraryNotFoundException;


public interface FactoryLibraries {
	
	  public Library[] getLibraryList()  throws InvalidSessionException;
	  
	  public Library getLibraryWork()  throws InvalidSessionException;
	  
	  public Library addLibrary(String path_library, String name_library)  throws  InvalidSessionException, DirectoryNotFound, InvalidLibraryException;
	  
	  public void emptyWork() throws InvalidSessionException, InvalidLibraryException;
	  
	  public Library getLibrary(String name_library)  throws InvalidSessionException,LibraryNotFoundException;
	  
	  public Library addLibrary(String name_library,java.sql.Connection connection)  throws Exception;
	  
	  public boolean existLibrary(String name_library)  throws InvalidSessionException;
	  
}
