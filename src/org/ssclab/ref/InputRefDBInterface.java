package org.ssclab.ref;

import java.util.ArrayList;

import org.ssclab.metadata.FieldInterface;

public interface InputRefDBInterface extends Input {
	public ArrayList<FieldInterface> getListField() ;
	public java.sql.ResultSet getResultSet() ;
	public String getSql();
	public void executeQuery() throws Exception ;
	
	//public void setResultSetToNull() ;
}
