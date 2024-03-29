package org.ssclab.ref;

import org.ssclab.context.SessionIPRIV;
import org.ssclab.dataset.exception.InvalidNameDataset;
import org.ssclab.library.Library;
import org.ssclab.library.exception.InvalidLibraryException;
import org.ssclab.library.exception.LibraryNotFoundException;
import org.ssclab.util.ParseLibDotDs;
import org.ssclab.util.TestValidName;

public class FactoryInputRefFromLibrary {
	
public static Input createInputRef(String lib_dot_dataset,SessionIPRIV parent_session) throws Exception {
		
		//A seconda del tipo di libreria crea un OutputrefDb o un outputreffmt
		//ALLA LIBRERIA LOCICA VERRA SOSTITUITO IL PATH FISOCO DI QUELLA LIBRERIA
		// E DAL NOME DEL DATASET IL NOME DEL FILE CON L'ESTENSIONE 
		// fa i controlli sull'esistenza della libreira 
		
		ParseLibDotDs pars_lib_dot_ds=new ParseLibDotDs(lib_dot_dataset);
		pars_lib_dot_ds.parse();
		String name_library=pars_lib_dot_ds.getLibrary();
		String name_dataset=pars_lib_dot_ds.getNameDs();	
		
		boolean exist_lib=parent_session.getFactoryLibraries().existLibrary(name_library);
		if(!exist_lib) throw new  LibraryNotFoundException(name_library);
		boolean test_name_ds=TestValidName.isValidNameDataset(name_dataset);
		if(!test_name_ds) throw new  InvalidNameDataset(name_dataset);
		
		Library library=parent_session.getFactoryLibraries().getLibrary(name_library);
		if(library.isFmtLibrary()) {
			return new InputRefFmt(library, name_dataset); 
		}
		else if(library.isDbLibrary()) {
			return new InputRefDB(library, name_dataset); 
		}
		else {
			throw new InvalidLibraryException("ERRORE. Tipologia di libreria non considerata.");
		}
	}
}
