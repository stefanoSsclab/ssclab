package org.ssclab.metadata.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.ssclab.metadata.FieldInterface;
import org.ssclab.metadata.exception.ReadMetadataSqlException;
import org.ssclab.metadata.exception.TypeSqlNotSupported;

public class CreateFieldsFromResultset extends AbstractCreateFileds {
	private ArrayList<FieldInterface> list_fields;
	
	public CreateFieldsFromResultset(ResultSet rsmeta, String name_tabella) throws SQLException, TypeSqlNotSupported, ReadMetadataSqlException {
		 list_fields=new ArrayList<FieldInterface> ();
		 boolean entrato=false;
		 while(rsmeta.next()) 	{
			 entrato=true;
			 String name=rsmeta.getString("COLUMN_NAME").toUpperCase();
			 int size=rsmeta.getInt("COLUMN_SIZE");
			 String type_s_sql=rsmeta.getString("TYPE_NAME");
			 int type_sql=rsmeta.getInt("DATA_TYPE");
			 int scale =rsmeta.getInt("DECIMAL_DIGITS");
			 
			 //System.out.println("medi--> "+name+" "+size + " "+type_sql+ " "+type_s_sql);
			 //System.out.println("medi--> Precision:"+size ); //per i numerici rappresenta la precisione 
			 //System.out.println("medi--> Scale:"+scale); 
			 
			 
			 list_fields.add(createSingleField(name,size,type_sql,type_s_sql,size,scale));
		 }
		 if(!entrato) throw new ReadMetadataSqlException("ERRORE ! Non sono riuscito a leggere i metadati della tabella "+name_tabella +" o nome tabella inesistente");
	}
	public ArrayList<FieldInterface> getFieldsdCreated() {
		return list_fields;
	}	

}
