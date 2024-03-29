package org.ssclab.step.readdata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.io.DataInputStreamFormat;
import org.ssclab.log.SscLogger;
import org.ssclab.metadata.FieldInterface;
import org.ssclab.parser.exception.InvalidDateFormatException;
import org.ssclab.pdv.PDV;
import org.ssclab.pdv.PDVAll;
import org.ssclab.pdv.PDVField;
import org.ssclab.ref.Input;
import org.ssclab.ref.InputRefFmtMemory;
import org.ssclab.step.writedata.ManagerMissingValues;
import org.ssclab.util.GregorianCalendarFormat;

 class ReadDataFromFmtMemory implements ReadDataInterface  {
	 
	 private static final Logger logger=SscLogger.getLogger();
	 
	 private InputRefFmtMemory ref_fmt;
	 private OptionsRead options_read;

	 ReadDataFromFmtMemory(Input ref, OptionsRead options_read) {
		this.ref_fmt = (InputRefFmtMemory) ref;
		this.options_read=options_read;
	}
	 
	public SourceDataInterface getSourceData() throws Exception {
		return new SourceData(ref_fmt.getByteArrayInput());
	}
 
	public PDV createPDV() throws InvalidDateFormatException {

		ArrayList<FieldInterface> list_field = ref_fmt.getListField();
		int num_var = list_field.size();
		PDV pdv = new PDV();

		for (int _a = 0; _a < num_var; _a++) {
			String name_var = list_field.get(_a).getName();
			Class type_var = list_field.get(_a).getType();
			PDVField pdv_field = pdv.addNewField(name_var, type_var);
			pdv_field.lentgh_field = list_field.get(_a).getLenght();
			pdv_field.precision=list_field.get(_a).getPrecision();
			pdv_field.scale=list_field.get(_a).getScale();
			pdv_field.type_sql=list_field.get(_a).getTypeSql();
			
		}
		return pdv;
	}
	 
	private final class SourceData implements SourceDataInterface {
		private DataInputStreamFormat fmt_input_data;
		private Class type;
		private String name_var ;
		private int size_var;
		private byte[] arrray_missing_values; 
		private int num_var_read;
		private long obs_by_read;
		private long obs_lette;
		private boolean log_active;

		// SAREBBE OPPORTUNO PASSARE AL IO DI TIPO NIO ??????????????????
		private SourceData(ByteArrayInputStream buff) throws IOException {
			this.fmt_input_data = new DataInputStreamFormat(buff);
			this.obs_by_read=ref_fmt.getNumObsDs();
			this.obs_lette=0;
			this.log_active=true;
			long max_obs_read=options_read.getMaxObsRead();
			this.arrray_missing_values=ManagerMissingValues.createByteArray(ref_fmt.getListField().size());
			if(max_obs_read!=-1 && max_obs_read < this.obs_by_read) this.obs_by_read=max_obs_read; 
		}

		public void setLogActive(boolean active) {
			log_active=active;
		}

		public boolean readFromSourceWriteIntoPDV(PDVAll pdv) throws Exception {
			
			if(obs_by_read <= obs_lette) return  false;
			
			//commentato il 23/09/2012 poiche trasferito in trsformationData
			//pdv.setRecordDeleted(false);
		
			ManagerMissingValues.resetArray(arrray_missing_values);
			for(FieldInterface field:ref_fmt.getListField())  {
				
				type=field.getType();
				name_var=field.getName();
				size_var=field.getLenght();
				
				if (type == String.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readUTF();
				} 
				else if (type == StringBuffer.class) {
					pdv.getField(name_var).value_generics=new StringBuffer(fmt_input_data.readChars(size_var));
				}
				else if (type == Double.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readDouble();
				}
				else if (type == GregorianCalendar.class) {
					pdv.getField(name_var).value_generics= new GregorianCalendarFormat().setTimeInMillix(fmt_input_data.readLong());
				}
				else if (type == Integer.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readInt();
				}
				else if (type == Short.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readShort();
				}
				else if (type == Character.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readChar();
				}
				else if (type == Float.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readFloat();
				}
				else if (type == Byte.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readByte();
				}
				else if (type == Boolean.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readBoolean();
				}
				else if (type == Long.class) {
					pdv.getField(name_var).value_generics=fmt_input_data.readLong();
				}
				else {
					throw new Exception("Attenzione !!! Type sconosciuto");
				}
			}
			
			fmt_input_data.read(arrray_missing_values);
			
			num_var_read=0;
			for(FieldInterface field:ref_fmt.getListField())  {
				 name_var=field.getName();
				 num_var_read++;
				 if(ManagerMissingValues.isMissingFromArray(arrray_missing_values, num_var_read)) {
					 pdv.getField(name_var).is_null=true;
				 }
				 else {
					 pdv.getField(name_var).is_null=false;
				 }
			}
			obs_lette++;		
			return true;
		}
		
		
		public void readNullFromSourceWriteIntoPDV(PDVAll pdv)  {
			num_var_read=0;
			for(FieldInterface field:ref_fmt.getListField())  {
				 name_var=field.getName();
				 num_var_read++;
				 pdv.getField(name_var).is_null=true;
			}
			obs_lette++;		
		}
		
		
		
		public void close() throws IOException {
			if(log_active) logger.log(Level.INFO,"Numero di osservazioni lette da memoria :"+obs_lette);
			if (fmt_input_data != null) {
				fmt_input_data.close();
			}
		}
	}

}
