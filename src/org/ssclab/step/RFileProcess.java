package org.ssclab.step;

import java.util.Locale;

import org.ssclab.context.SessionIPRIV;
import org.ssclab.ref.Input;
import org.ssclab.ref.OutputRefInterface;
import org.ssclab.step.exception.InvalidDichiarationOptions;
import org.ssclab.step.readdata.OptionsRead;
import org.ssclab.step.trasformation.OptionsTrasformation;
import org.ssclab.step.writedata.OptionsWrite;


public class RFileProcess extends CoreProcess implements FileStep {
	
	RFileProcess(OutputRefInterface output_ref, Input input_ref,	SessionIPRIV parent_session) {
		this.output_ref = output_ref;
		this.parent_session = parent_session;
		this.input_ref = input_ref;
		this.opt_read = new OptionsRead();
		this.opt_trasf = new OptionsTrasformation();
		this.opt_write = new OptionsWrite();
	}
	
	public void printHeader() {
		this.opt_write.setHeaderPrintfFile(PRINT_ALL_HEADER);
	}
	
	public void setLocale(Locale l) {
		this.opt_write.setLocale(l);
	}

	public void printHeader(String header)  {
		this.opt_write.setHeaderPrintfFile(header);
	}
	
	public void printAllVar() {
		this.opt_write.setPrintfOptions(PRINT_ALL, new String[] {});
	}

	public void setFormatVar(String nome_campo, String nome_formato) {

	}
	
	public void setParameter(ParameterStepInterface obj) {
		opt_trasf.setParameterStep( obj);
	}
	
	public FileStep setOutputMissing(String token_miss) {
		this.opt_write.setOutputTokenMissing(token_miss);
		return this;
	}
	
	public FileStep printf(String string_of_record, String... var) {
		this.opt_write.setPrintfOptions(string_of_record, var);
		return this;
	}
	
	public void declareNewVar(String string_var_chichiaration) {
		opt_trasf.setDeclareNewVar(string_var_chichiaration);
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