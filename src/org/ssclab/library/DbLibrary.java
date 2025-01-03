package org.ssclab.library;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.i18n.RB;
import org.ssclab.library.exception.DatasetExistingException;
import org.ssclab.library.exception.DatasetNotFoundException;
import org.ssclab.library.exception.DmlNotDefinedForDbException;
import org.ssclab.library.exception.InvalidLibraryException;
import org.ssclab.log.SscLogger;
import org.ssclab.ref.Input;
import org.ssclab.ref.InputRefDB;
import org.ssclab.ref.InputRefDBSQL;

public class DbLibrary extends GenericLibrary  implements PLibraryInterface {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private Connection connect;
	
	DbLibrary(String name_library, Connection conect, RFactoryLibraries fact_lib) throws SQLException {
		this.connect = conect;
		this.produtc_name=conect.getMetaData().getDatabaseProductName().toUpperCase();
		this.name = name_library;
		this.type = TYPE_LIBRARY.LIBRARY_DB;
		this.factrory_lib = fact_lib;
		logger.log(Level.INFO, 
				   RB.getString("it.ssc.library.library.DbLibrary.msg1")+" "+
				   name_library+" "+
				   RB.getString("it.ssc.library.library.DbLibrary.msg2")+" "+
				   this.produtc_name);
	}
	
	public void emptyLibrary() throws InvalidLibraryException {
		throw new InvalidLibraryException(
				RB.getString("it.ssc.library.library.DbLibrary.msg3")+" "+ 
		        this.name +" "+ 
		        RB.getString("it.ssc.library.library.DbLibrary.msg4"));
	}
	
	public int executeSQLUpdate(String sql) throws SQLException, InvalidLibraryException {
		this.generateExceptionOfLibraryClose();
		logger.log(Level.INFO,"DDL:"+sql);
		return connect.createStatement().executeUpdate(sql);
	}

	
	public void renameTable(String new_name,String old_name) throws Exception {
		
		this.generateExceptionOfLibraryClose();
		if(!this.existTable(old_name)) {
			throw new DatasetNotFoundException("ERRORE ! la tabella "+old_name+" non esiste.");
		}
		
		if(this.existTable(new_name)) {
			throw new DatasetExistingException(
					RB.getString("it.ssc.library.library.DbLibrary.msg5")+" "+ 
			        new_name+
			        RB.getString("it.ssc.library.library.DbLibrary.msg6"));
		}
		Statement stat=connect.createStatement();
		if(this.produtc_name.equals(PRODUCT_NAME.ORACLE.getValue())) {
			stat.executeUpdate("alter table "+old_name+" rename to "+new_name);
		}
		else if(this.produtc_name.equals(PRODUCT_NAME.POSTGRESQL.getValue())) {
			stat.executeUpdate("alter table "+old_name+" rename to "+new_name);
		}
		else if(this.produtc_name.startsWith(PRODUCT_NAME.DB2.getValue())) {
			stat.executeUpdate("rename table "+old_name+" to "+new_name);
		}
		else {
			throw new DmlNotDefinedForDbException("ERRORE ! Per questo db "+this.produtc_name +" non e' stata definita l'istruzione di rename.");
		}
		if(stat!=null) stat.close();
		logger.log(Level.INFO,
				   RB.getString("it.ssc.library.library.DbLibrary.msg7")+" "+ 
		           name+"."+old_name+" "+
		           RB.getString("it.ssc.library.library.DbLibrary.msg8")+" "+ 
		           name+"."+new_name);
		
	}
	
	public Input getInputFromSQLQuery(String sql) throws Exception {
		this.generateExceptionOfLibraryClose();
		return new InputRefDBSQL(this,sql);
	}
	
	
	public String getUrl() throws Exception {
		this.generateExceptionOfLibraryClose();
		return connect.getMetaData().getURL();
	} 
	
	
	public String getAbsolutePath() throws Exception  {
		this.generateExceptionOfLibraryClose();
		return getUrl();
	}

	public Input getInput(String name_table) throws Exception {
		this.generateExceptionOfLibraryClose();
		return new InputRefDB(this,name_table);
	}
	
	public Connection getConnection() throws InvalidLibraryException {
		this.generateExceptionOfLibraryClose();
		return connect;
	}
	
	public String[] getListTable() throws InvalidLibraryException, SQLException   {
		String[] type_table=new String[] {"TABLE","VIEW"};
		return this.getListTable(type_table);
	}
	

	public String[] getListTable(String[] type_table) throws InvalidLibraryException, SQLException   {
		this.generateExceptionOfLibraryClose();
		
		DatabaseMetaData dbmd = connect.getMetaData();
		ArrayList<String> list_table_mt=new ArrayList<String>();	 
		//possibile passarlo come ultimo argomento 
		//String[] types = {"TABLE"};
	    ResultSet resultSet = dbmd.getTables(null, null, "%", type_table);
	    String tableName=null;
	    // Get the table names
	    while (resultSet.next()) {
	        // Get the table name
	        tableName = resultSet.getString(3);
	        list_table_mt.add(tableName);

	    }
	    resultSet.close();
		String[] list_table_db=new String[list_table_mt.size()];
		return list_table_mt.toArray(list_table_db);

	}
	
	public boolean existTable (String name_table) throws Exception   {
		this.generateExceptionOfLibraryClose();
		for(String name_table_fmt:this.getListTable()) {
			if(name_table_fmt.equalsIgnoreCase(name_table))  { 
				return true;
			}
		}
		return false;
	}
	
	
	public void closeLibraryForTerminateSession() throws Exception  {
		if(this.is_close) return;
		//per chiudere la connessione al DB
		this.connect.close();
		this.is_close=true;
		this.connect=null;
	}
	
	public void close() throws Exception {
		if(this.is_close) return;
		//per chiudere la connessione al DB
		this.connect.close();
		this.factrory_lib.removeLibraryFromList(this.name);
		this.is_close=true;
		this.connect=null;
	}
	
	public void dropTable(String name_table) throws Exception {
		this.generateExceptionOfLibraryClose();
		if(existTable(name_table)) {
			Statement stat=this.connect.createStatement();
			stat.executeUpdate("DROP TABLE "+name_table);
			stat.close();
		}
		else {
			throw new DatasetNotFoundException(RB.getString("it.ssc.library.library.DbLibrary.msg5")+" "+ 
		                                       name+"."+name_table+
		                                       RB.getString("it.ssc.library.library.DbLibrary.msg9"));
		}
		logger.log(Level.INFO,
				   RB.getString("it.ssc.library.library.DbLibrary.msg7")+" "+ 
				   this.name+"."+name_table+
				   " e' stata eliminata");
	}
	
	public String getCorrectNameTable(String name) throws InvalidLibraryException, SQLException  {
		for(String name_on_db:getListTable() ) {
			if(name.equalsIgnoreCase(name_on_db)) return name_on_db;
		}
		return name;
	}
}

