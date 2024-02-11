package org.ssclab.step.readdata;

import org.ssclab.pdv.PDVAll;
import org.ssclab.step.exception.ErrorStepInvocation;

public interface SourceDataInterface  {
	
	public boolean readFromSourceWriteIntoPDV(PDVAll pdv)  throws Exception;
	public void close() throws Exception;
	public void setLogActive(boolean active);
	public void readNullFromSourceWriteIntoPDV(PDVAll pdv)  throws ErrorStepInvocation; 
}
