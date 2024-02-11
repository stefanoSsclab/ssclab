package org.ssclab.step;

import org.ssclab.context.SessionIPRIV;
import org.ssclab.ref.Input;
import org.ssclab.ref.OutputRefInterface;
import org.ssclab.step.exception.InvalidDichiarationOptions;
import org.ssclab.step.readdata.*;
import org.ssclab.step.trasformation.OptionsTrasformation;
import org.ssclab.step.writedata.*;

/**
 * L'oggetto <code>DataStep</code> ......
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @since version 1.0
 */

class RDataProcess extends CoreProcess implements DataStep {


	RDataProcess(OutputRefInterface new_dataset_output,	Input input_ref, SessionIPRIV parent_session) {
		this.output_ref = new_dataset_output;
		this.parent_session = parent_session;
		this.input_ref = input_ref;
		this.opt_read = new OptionsRead();
		this.opt_trasf = new OptionsTrasformation();
		this.opt_write = new OptionsWrite();
	}


	public void setDropVarOutput(String... name_field) {
		opt_write.setDropOutput(name_field);
	}

	public void setKeepVarOutput(String... name_field) {
		opt_write.setKeepOutput(name_field);
	}

	public void declareNewVariable(String declare_new_var) {
		opt_trasf.setDeclareNewVar(declare_new_var);
	}
	
	public void declareJavaAttribute(String declare_java_var) {
		opt_trasf.setDeclareJavaAttribute(declare_java_var);
	}
	
	public void setParameterStep(ParameterStepInterface obj) {
		opt_trasf.setParameterStep( obj);
	}

	public void setFormat(String nome_campo, String nome_formato) {
		opt_write.setFormat(nome_campo, nome_formato);
	}

	public void setLabel(String nome_campo, String label) {
		opt_write.setLabel(nome_campo, label);
	}
	
	public void renameVar(String current_name, String new_name) {
		opt_write.renameVar(current_name,new_name);
	}
	
	public void setAppendOutput(boolean append) {
		opt_write.setAppendOutput(append);
	}
	
	public void setMaxObsRead(long obs_read) throws InvalidDichiarationOptions {
		opt_read.setMaxObsRead(obs_read);
	}
	
	public void setWhere(String where_condition) {
		opt_trasf.setWhereCondition(where_condition);
	}

	public void setSourceCode(String source_code) {
		this.opt_trasf.setSourceUserCode(source_code);
	}
	
	public void run() throws Exception {
		this.execute();
	}

}