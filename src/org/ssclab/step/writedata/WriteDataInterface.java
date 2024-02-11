package org.ssclab.step.writedata;


import org.ssclab.pdv.PDVKeep;
import org.ssclab.ref.Input;

public interface WriteDataInterface {

	public void readFromPDVWriteOutput(PDVKeep pdv) throws Exception;
	
	public void close(boolean data_step_error,PDVKeep pdv) throws Exception;
	
	public Input getDataRefCreated() throws Exception;
   
}
