package org.ssclab.pdv;

import org.ssclab.parser.exception.InvalidDateFormatException;
import org.ssclab.pdv.PDVField;

public interface PDVAll {
	
	public <T> PDVField<T> addNewField(String key, Class<T> type) throws InvalidDateFormatException   ;
	
	@SuppressWarnings("rawtypes")
	public PDVField getField(String key) ; 
	
	@SuppressWarnings("rawtypes")
	public PDVField getField(int index) ;
	
	public int getSize() ;

	public void setRecordDeleted(boolean record_deleted) ;
	
	public void resetAllVarToDropValue(boolean value) ;
	
	

}
