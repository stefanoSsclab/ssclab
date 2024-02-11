package org.ssclab.step;

import org.ssclab.context.SessionIPRIV;
import org.ssclab.ref.Input;
import org.ssclab.ref.OutputRefInterface;
import org.ssclab.step.exception.InvalidDichiarationOptions;
import org.ssclab.step.readdata.OptionsRead;
import org.ssclab.step.trasformation.OptionsTrasformationSort;
import org.ssclab.step.writedata.OptionsWrite;

class RSortProcess extends CoreDataSortStep implements SortStep {
	
	RSortProcess(OutputRefInterface new_dataset_output,	Input input_ref, SessionIPRIV parent_session) {
		this.output_ref = new_dataset_output;
		this.parent_session = parent_session;
		this.input_ref = input_ref;
		this.opt_read = new OptionsRead();
		this.opt_trasf = new OptionsTrasformationSort();
		this.opt_write = new OptionsWrite();
	}
	
	public void setDropVarOutput(String... name_field) {
		opt_write.setDropOutput(name_field);
	}

	public void setKeepVarOutput(String... name_field) {
		opt_write.setKeepOutput(name_field);
	}

	public void setMaxObsRead(long obs_read) throws InvalidDichiarationOptions {
		opt_read.setMaxObsRead(obs_read);
	}

	public void setVariablesToSort(String variables_to_order) {
		this.opt_trasf.setVariablesToSort(variables_to_order);
	}

	public void setMaxNumberRecordLoadInMemoryForSort(int max_dim_array) {
		this.opt_trasf.setDimensionArrayForSort(max_dim_array);
	}
	
	public void run() throws Exception {
		this.execute();
	}

}
