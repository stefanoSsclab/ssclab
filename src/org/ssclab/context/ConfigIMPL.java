package org.ssclab.context;


import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.log.SscLogger;


class ConfigIMPL implements Config, Cloneable {
	
	private static final Logger logger=SscLogger.getLogger();
	//private String pathWorkArea=System.getProperty("user.dir");
	private String pathWorkArea=createTempWorkDirectory();
	private String pathLocalDb=System.getProperty("user.dir");
	private String pathFileConfig=null; 
	
	
	ConfigIMPL() {
		//deve leggere il file di configurazione di default e caricare le informazioni
		//eventuali connessioni o librerie da allocare, vengono solo lette come informazioni. 
		//deve poi pensarci la classe Session a utilizzare le informazioni per allocare librerie. 
		loadFileConfig();
		
	}
	
	
	ConfigIMPL(String pathFileConfig) {
		this.pathFileConfig=pathFileConfig;
		//deve leggere il file di configurazione specificato nel path e caricare le informazioni
		loadFileConfig();
	}
	
	private void loadFileConfig() {
		//da implementare per leggere file xml di configurazione
	}
	
	private static String createTempWorkDirectory() {

		String directoryTemp=null;
		try {
			directoryTemp= Files.createTempDirectory("ssc_").toAbsolutePath().toString();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, "warning creation directory temp :", e);
			directoryTemp= System.getProperty("java.io.tmpdir");
		}
		//System.out.println(directoryTemp);
		return directoryTemp;
	}

	/**
	 * Ritorna la directory di work contenitrice di tutte le directory di work create 
	 * con nomi randomici relative alle diverse sessioni fmt 
	 */
	public String getPathWorkArea()  {
		return pathWorkArea;
	}
	
	public void setPathWorkArea(String path_work) {
		this.pathWorkArea=path_work;
	}
	
	public String getPathLocalDb() {
		return pathLocalDb;
	}

	public void setPathLocalDb(String path_root_db_derby) {
		this.pathLocalDb = path_root_db_derby;
	}
	
	public String getPathFileConfig() {
		return pathFileConfig;
	}
	
	@Override
	public Config clone() {
		ConfigIMPL clone=null;
		try {
			clone=(ConfigIMPL)super.clone();	
		} 
		catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"Clonazione Config",e);
		}
		return clone;
	}
}
