package org.ssclab.context;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.context.exception.InvalidSessionException;
import org.ssclab.datasource.DataSource;
import org.ssclab.formats.FactoryFormats;
import org.ssclab.formats.RFactoryFormats;
import org.ssclab.i18n.RB;
import org.ssclab.io.DirectoryNotFound;
import org.ssclab.library.FactoryLibraries;
import org.ssclab.library.Library;
import org.ssclab.library.RFactoryLibraries;
import org.ssclab.library.exception.InvalidLibraryException;
import org.ssclab.log.SscLogger;
import org.ssclab.ref.Input;
import org.ssclab.step.DataStep;
import org.ssclab.step.FactorySteps;
import org.ssclab.step.FileStep;
import org.ssclab.step.RFactorySteps;
import org.ssclab.step.SortStep;


class SessionIMPL implements SessionIPRIV {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private final Integer id;
	private boolean active;
	private RFactoryLibraries factory_libraries;
	private RFactorySteps factory_processes;
	private RFactoryFormats factory_formats;
	private Config config;
	

	/**
	 * Costruttore sessione SSC con id e config specificato
	 * 
	 * @throws InvalidSessionException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */

	 SessionIMPL(Integer id, Config config) throws DirectoryNotFound, InvalidLibraryException, InvalidSessionException, ClassNotFoundException, SQLException {
		this.id=id;
		this.active=true;
		
		logger.log(Level.INFO,
				   RB.getString("it.ssc.context.Session_Impl.msg1")+" "+
		           this.id);
		
		this.config=config;
		this.factory_libraries = new RFactoryLibraries(this);
		this.factory_formats   = new RFactoryFormats(this);
		this.factory_processes = new RFactorySteps(this);
		onClose();
		
	}
	 
	private void onClose() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (isClose()) return;
				try {
					close();
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	 
	/**
	 * Medodo che genera eccezione se la sessione e' chiusa
	 */
	public void generateExceptionOfSessionClose() throws InvalidSessionException {
		if (this.active == false) {
			throw new InvalidSessionException();
		}
	}		
	
	/**
	 * Restituisce l' oggetto config relativo alla sessione corrente. Questo
	 * metodo scaturisce dall'implemetazione dell'interfaccia privata di
	 * FMTSession
	 * 
	 * 
	 * @return l'ogetto Config relativo alla sessione
	 * @throws InvalidSessionException 
	 * @see org.ssclab.context.ConfigIMPL
	 */
	public Config getConfig() throws InvalidSessionException {
		this.generateExceptionOfSessionClose();
		return this.config;
	}
	
	/**
	 * Restituisce l'id della sessione 
	 * 
	 * @return id di sessione
	 * @throws InvalidSessionException 
	 */
	
	public int getId() throws InvalidSessionException {
		this.generateExceptionOfSessionClose();
		return this.id;
	}
	
	
	/**
	 * Chiude la sessione di lavoro , disallocando le librerie e eliminando i
	 * formati. Non e' piu' possibile fare operazioni di datastep O ALTRE AZIONI sulla sessione.
	 * 
	 * @throws Exception  
	 */

	public synchronized void close() throws Exception {
		if(isClose()) return;
		 //svuota e cancella la directory dei compilati
		this.deleteDirectoryCompiler(); 
		//chiude anche le connessioni ai DB
		this.factory_libraries.closeAllLibrary();
		this.factory_formats.clearAllFormat();
		//imposta il flag a false
		this.active=false;
		logger.log(Level.INFO,
				   RB.getString("it.ssc.context.Session_Impl.msg2")+" "+
		           this.id);
	}
	
	public boolean isClose(){
		return (!this.active);
	}

	/**
	 * 
	 * 
	 * @return FactoryFormats
	 */

	public FactoryFormats getFactoryFormats() throws InvalidSessionException {
		this.generateExceptionOfSessionClose();
		return this.factory_formats;
	}

	/**
	 * Ritorna il gestore FactoryLibraries delle librerie 
	 */
	
	public FactoryLibraries getFactoryLibraries() throws InvalidSessionException {
		this.generateExceptionOfSessionClose();
		return this.factory_libraries;
	}
	
	/**
	 * Ritorna il gestore FactoryStep deei passi di datastep 
	 * 
	 */
	
	public FactorySteps getFactorySteps() throws InvalidSessionException {
		this.generateExceptionOfSessionClose();
		return this.factory_processes;
	}
	
	/**
	 * Ritorna il path dei sorgenti compilati 
	 * @throws DirectoryNotFound 
	 */
	public String getPathCompiler() throws InvalidSessionException, DirectoryNotFound {
		this.generateExceptionOfSessionClose();
		return this.factory_libraries.getPathCompiler();
	}
	
	
	/**
	 * Ritorna il path di work per l'appoggio dei file utilizzati per l'ordinamento 
	 * @throws DirectoryNotFound 
	 */
	public String getPathSorting() throws InvalidSessionException, DirectoryNotFound {
		this.generateExceptionOfSessionClose();
		return this.factory_libraries.getPathSorting();
	}
	
	
	/**
	 * Svuota e cancella la directory dei compilati 
	 * @throws DirectoryNotFound 
	 */
	
	private   void deleteDirectoryCompiler() throws DirectoryNotFound {
		String path_c = this.factory_libraries.getPathCompiler();
		if(path_c==null) throw new DirectoryNotFound("PathCompiler is null");
		File dir_lib = new File(path_c);
		if (dir_lib.exists()) {
			File[] files = dir_lib.listFiles();
			if(files!=null) {
				for (File file_lib : files) {
					boolean isDeleted=file_lib.delete();
					if(!isDeleted) logger.log(Level.WARNING,"File in directory compiler '"+file_lib.getName()+"' not deleted");
				}
			}	
			boolean isDeleted=dir_lib.delete();
			if(!isDeleted) logger.log(Level.WARNING,"Directory '"+dir_lib.getAbsolutePath()+"' not deleted");
		}
	}
	
	public Library addLibrary(String name_library, java.sql.Connection connect) throws Exception {
		return factory_libraries.addLibrary(name_library, connect);
	}

	public Library addLibrary(String path_library, String name_library)
			throws InvalidSessionException, DirectoryNotFound,InvalidLibraryException {
		return factory_libraries.addLibrary(path_library, name_library);
	}

	public DataSource createDataSource(Input input_reference) throws Exception {
		return factory_processes.createDataSource(input_reference);
	}

	public DataSource createDataSource(String name_input_dataset)
			throws Exception {
		return factory_processes.createDataSource(name_input_dataset);
	}

	public DataStep createDataStep(String new_dataset,String name_input_dataset) 
			throws Exception {
		return factory_processes.createDataStep(new_dataset,	name_input_dataset);
	}

	public DataStep createDataStep(String new_dataset,Input input_reference) 
			throws Exception {
		return factory_processes.createDataStep(new_dataset, input_reference);
	}

	public FileStep createFileStep(String path_file,String lib_dot_idataset) 
			throws Exception {
		return factory_processes.createFileStep(path_file, lib_dot_idataset);
	}

	public FileStep createFileStep(String path_file, Input input_reference)
			throws Exception {
		return factory_processes.createFileStep(path_file, input_reference);
	}
	
	public SortStep createSortStep(String new_dataset,String name_input_dataset) 
			throws Exception {
		return factory_processes.createSortStep(new_dataset,	name_input_dataset);
	}

	public SortStep createSortStep(String new_dataset,Input input_reference) 
			throws Exception {
		return factory_processes.createSortStep(new_dataset, input_reference);
	}
	
	public Session startLocalDB()  throws Exception {
		factory_libraries.startLocalDB();
		return this;
	}
	
}
