package org.ssclab.step.writedata;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.context.Config;
import org.ssclab.io.DataOutputStreamFormat;
import org.ssclab.io.UtilFile;
import org.ssclab.log.SscLogger;
import org.ssclab.metadata.MetaDataDatasetFMTInterface;
import org.ssclab.metadata.NameMetaParameters;
import org.ssclab.metadata.ReadMetaDataFMT;
import org.ssclab.metadata.UtilMetaData;
import org.ssclab.metadata.exception.IncompatibleTypeFMT;
import org.ssclab.pdv.PDVField;
import org.ssclab.pdv.PDVKeep;
import org.ssclab.ref.OutputRefFmt;
import org.ssclab.step.exception.InvalidDichiarationOptions;

class ManagementWritingFileFmt {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private boolean append;
	private MetaDataDatasetFMTInterface meta;
	private DataOutputStreamFormat file_out_data;
	private File file_fmt;
	private OutputRefFmt out_ref;
	
	ManagementWritingFileFmt(OutputRefFmt out_ref, OptionsWrite opt_write, ArrayList<PDVField<?>> list_field_pdv) throws FileNotFoundException, IOException, ClassNotFoundException, InvalidDichiarationOptions, IncompatibleTypeFMT, Exception {
		this.append=opt_write.isAppendOutput();
		this.file_fmt=out_ref.getFile();
		this.out_ref=out_ref;
		if(append==true && out_ref.isExistingDatasetComplete()) {
			//comparare che i field siano identici	(numero,posizione,lunghezza,tipo)
			//Se non lo sono -> Exception 
			meta=ReadMetaDataFMT.readAndClose(out_ref.getFileFMTMeta());
			UtilMetaData.isCompatibleInputPDVComparedWithMetaOutput(meta, list_field_pdv);
			file_fmt=new File(out_ref.getFile().getAbsolutePath()+Config.ESTENSION_COPY_TEMP_FMT);
			UtilFile.copyFile(out_ref.getFile(), file_fmt);
			
		}
		else if(append==true) {
			logger.log(Level.WARNING,"E' stato specificato l'append sul dataset "+out_ref.getNameComplete()+
					   " ma questo dataset non esiste o e' incompleto. Il dataset verra creato nuovo.");
			append=false;
		}
		
		BufferedOutputStream buff = new BufferedOutputStream(new FileOutputStream(file_fmt,append));
		this.file_out_data = new DataOutputStreamFormat(buff);
	}
	
	public DataOutputStreamFormat getDataOutpuStream() {
		return file_out_data;
	}
	
	public boolean isAppend() {
		return append;
	}
	
	public long getOldNumberObs() {
		return (Long)meta.getMapProperties().get(NameMetaParameters.NAME_META_PARAMETERS.NOBS_LONG);
	}
	
	public void close(boolean error, long obs, PDVKeep pdv ) throws Exception {
		if (file_out_data != null)	file_out_data.close();
		if(error) { 
			if(append && file_fmt.exists()) {
				file_fmt.delete();
			}
			return;
		}
		if(append) {
			out_ref.getFile().delete();
			file_fmt.renameTo(out_ref.getFile());
			out_ref.writeMetaData(meta,obs);
		}
		else {
			out_ref.writeMetaData(pdv,obs);
		}	
	}

}
