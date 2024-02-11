package org.ssclab.ref;

import org.ssclab.metadata.CreateMetadataFMT;
import org.ssclab.metadata.MetaDataDatasetFMTSerializable;
import org.ssclab.metadata.NameMetaParameters;
import org.ssclab.pdv.PDVKeep;

public class OutputRefFmtMemory implements OutputRefInterface {
	
	private static final TYPE_REF type_ref = TYPE_REF.REF_FMT_MEMORY;
	private MetaDataDatasetFMTSerializable meta;
	
	
	/*
	public OutputRefFmtMemory() {
	
	}
	*/
	
	public void createMetaData(PDVKeep pdv, Long obs) throws Exception {
		CreateMetadataFMT w_meta = new CreateMetadataFMT();
		w_meta.setField(pdv);
		w_meta.setProperties(NameMetaParameters.NAME_META_PARAMETERS.NOBS_LONG,	obs);
		meta=w_meta.getMetaData();
	}
	
	public MetaDataDatasetFMTSerializable getMeta() {
		return meta;
	}


	public TYPE_REF getTypeRef() {
		return type_ref;
	}

}
