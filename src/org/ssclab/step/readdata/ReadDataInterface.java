package org.ssclab.step.readdata;

import org.ssclab.parser.exception.InvalidDateFormatException;
import org.ssclab.pdv.PDV;

public interface ReadDataInterface {
	
	public SourceDataInterface getSourceData() throws Exception;
	public PDV createPDV() throws InvalidDateFormatException; 
	
}
