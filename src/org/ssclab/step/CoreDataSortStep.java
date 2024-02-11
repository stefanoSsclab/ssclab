package org.ssclab.step;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.context.SessionIPRIV;
import org.ssclab.log.SscLevel;
import org.ssclab.log.SscLogger;
import org.ssclab.pdv.PDV;
import org.ssclab.ref.Input;
import org.ssclab.ref.OutputRefInterface;
import org.ssclab.step.exception.ErrorStepInvocation;
import org.ssclab.step.readdata.OptionsRead;
import org.ssclab.step.readdata.ReadData;
import org.ssclab.step.readdata.SourceDataInterface;
import org.ssclab.step.sort.MyIterator;
import org.ssclab.step.sort.SortSetRecordV2;
import org.ssclab.step.trasformation.OptionsTrasformationSort;
import org.ssclab.step.trasformation.TrasformationDataSort;
import org.ssclab.step.writedata.OptionsWrite;
import org.ssclab.step.writedata.WriteData;

public class CoreDataSortStep {
	
	private static final Logger logger=SscLogger.getLogger();
	
	protected Input input_ref; 
	protected OptionsRead opt_read;
	protected OptionsWrite opt_write;
	protected OptionsTrasformationSort opt_trasf;
	protected SessionIPRIV parent_session;
	protected OutputRefInterface output_ref;
	protected boolean execute=false;
	private Input ref_created;
	
	public void execute() throws Exception {
		
		this.parent_session.generateExceptionOfSessionClose(); 
		if(execute==false )  {
			execute=true;
		}
		else {
			throw new ErrorStepInvocation("ERRORE ! Questo passo di data e' stato gia' invocato. ");
		}
		long start=System.currentTimeMillis();

		ReadData read_data = new ReadData(input_ref, opt_read);
		//Crea il pdv 
		PDV pdv = read_data.createPDV();
		TrasformationDataSort trasf_data = new TrasformationDataSort(pdv,opt_trasf,parent_session.getPathCompiler());
		
		WriteData write_data = null;
		SourceDataInterface source = null;
		boolean data_step_error=false; 
		SortSetRecordV2 sortset=new SortSetRecordV2(opt_trasf.getDimensionArrayForSort(),parent_session.getPathSorting(),parent_session.getPathCompiler()); 
		
		try {
			write_data = new WriteData(pdv,output_ref,opt_write); 
			source = read_data.getSourceData();
			while (source.readFromSourceWriteIntoPDV(pdv)) {
				sortset.add(trasf_data.loadRecord(pdv)); 
			}
			
			sortset.flushCacheSort(); 
			MyIterator iter=sortset.iterator();
			
			while(iter.hasNext()) {
				trasf_data.inizializePDV(pdv);
				trasf_data.uploadRecord(iter.next(),pdv);   
				write_data.readFromPDVWriteOutput(pdv);   
			}	
			if (sortset!=null) sortset.close();
		} 
		
		catch(OutOfMemoryError oome)   {
			data_step_error=true;
			logger.log(Level.SEVERE,"OutOfMemoryError, ridurre la dimensione del numero di record tenuti in memoria con il metodo setMaxNumberRecordLoadInMemoryForSort()");
			throw oome;
		}
		catch(Exception e) {
			data_step_error=true;
			throw e;
		}
		finally { 
			if (source != null) source.close();
			if (write_data != null) write_data.close(data_step_error,pdv);
		}
		
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,"Durata ordinamento dataset in "+(end-start)+" millisecondi.");
		ref_created=write_data.getDataRefCreated();
		
	}
	
	public Input getDataRefCreated() {
		return ref_created;
	}

}
