package org.ssclab.pdv;

import java.util.ArrayList;

import org.ssclab.pdv.PDVField;
import org.ssclab.step.exception.InvalidDichiarationOptions;

public interface PDVKeep {
	
	public int getSizeFieldKeep()  throws InvalidDichiarationOptions;
	
	public ArrayList<PDVField<?>> getListFieldKeep() throws InvalidDichiarationOptions;
	
	public boolean isRecordDeleted() ;

}
