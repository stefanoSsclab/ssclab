package org.ssclab.step;

import org.ssclab.ref.Input;
import org.ssclab.step.exception.InvalidDichiarationOptions;
import org.ssclab.step.parallel.Parallelizable;

public interface DataStep extends Parallelizable {
	
	// per togliere i formati come fare ????
	public void setFormat(String nome_campo, String nome_formato);

	public void setLabel(String nome_campo, String label);

	public void setAppendOutput(boolean append);

	public Object execute() throws Exception;

	public void setSourceCode(String source_code);

	public void declareNewVariable(String declare_new_var);
	
	public void declareJavaAttribute(String declare_java_var) ;

	public void setWhere(String where_condition);

	public void setDropVarOutput(String... name_field);

	public void setKeepVarOutput(String... name_field);

	public void setMaxObsRead(long obs_read) throws InvalidDichiarationOptions;
	
	public void setParameterStep(ParameterStepInterface obj) ;
	
	public Input getDataRefCreated() ;

}
