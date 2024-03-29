package org.ssclab.formats;

import org.ssclab.ref.Input;

public interface FactoryFormats {
	
	 /**
	   * Carica in memoria i formati da un catalogo 
	   * 
	   * @param lib_dot_name_file_format libreria.nome_file_formati dove si trovano i formati FMT 
	   * contenuti in un file in formato nativo. Se questi hanno uno spazio dei nomi viene mantenuto. 
	   * Se esiste in memoria un formato con lo stesso nomespazio.nome, deve dare warning che quello 
	   * vecchio viene sostituito  
	   */

	  public void loadFormat(String lib_dot_name_file_format); 
	  
	  
	  /**
	   * salva su catalogo tutti formati contenuti in memoria. Puo solo sovrescrivere , non andare in append
	   * 
	   * @param lib_dot_name_file_format libreria.nome_file_formati dove si salvano i formati FMT 
	   * che verranno salvati  in un file in formato nativo. Se questi hanno uno spazio dei nomi viene mantenuto
	   */

	  public void saveFormat(String lib_dot_name_file_format); 
	  
	  
	  
	  /**
	   * salva su catalogo i formati contenuti in memoria. Puo solo sovrescrivere , non andare in append
	   * 
	   * @param space_name salva solo i formati appartenenti allo spazio dei nomi specificato, 
	   * se null o "" , i formati salvati sono solo quelli dello spazio  default. 
	   * @param lib_dot_name_file_format libreria.nome_file_formati dove si salvano i formati FMT 
	   * che verranno salvati  in un file in formato nativo. Se questi hanno uno spazio dei nomi viene mantenuto
	   */

	  public void saveFormat(String space_name, String lib_dot_name_file_format); 
	  
	  
	 
	  /**
	   * 
	   * @return Un oggetto dal quale accedere ai formati di una sessione FMT 
	   */
	  
	  public FormatInterface[] getFormatList();
	  
	  /**
	   * Cancella tutti i formati dalla memoria 
	   */

	  public void clearAllFormat();
	  
	  /**
	   * Cancella i formati dalla memoria 
	   * 
	   * @param name_format Nome dei formati. Se ha uno spazio dei nomi occorre mettere 
	   * nome_spazio.nome_formato
	   */
	  
	  
	  public void clearFormatByName(String... name_format);
	  
	  /**
	   * Cancella dalla memoria tutti i formati appartenenti allo spazio dei nomi indicato 
	   * 
	   * @param space_name
	   */

	  public void clearAllFormatBySpaceName(String space_name);
	  
	  /**
	   * Verifica se esiste il formato. Se ha uno spazio dei nomi occorre mettere 
	   * nome_spazio.nome_formato
	   * 
	   * @param name_format
	   * @return vero se esiste il formato 
	   */
	  
	  public boolean existFormat(String name_format) ;
	  
	  public FormatsStep getFormatStep(String name_input_dataset) throws Exception; 
	  
	  public FormatsStep getFormatStep(Input input_reference) throws Exception; 
	  
	  
}
