package org.ssclab.ref;

import java.sql.Connection;

import org.ssclab.library.DbLibrary;
import org.ssclab.library.Library;
import org.ssclab.library.exception.InvalidLibraryException;

public class OutputRefDB implements OutputRefInterface {
	
	 private static final TYPE_REF type_ref=TYPE_REF.REF_DB;  
	 private DbLibrary library;
	 private String name_table; 

	OutputRefDB(Library library, String name_ds) {
		this.library =(DbLibrary) library;
		this.name_table = name_ds;
	}
	 
	 public TYPE_REF getTypeRef() {
		  return type_ref;
	 }
	 
	 public Connection getConnection() throws InvalidLibraryException {
		 return library.getConnection();
	 }
	 
	public String getNameTable() {
		return name_table;
	}

	public String getNameLibrary() {
		return library.getName();
	}

	public Library getLibrary() {
		return this.library;
	}

}
