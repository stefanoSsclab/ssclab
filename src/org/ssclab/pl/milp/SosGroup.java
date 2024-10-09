package org.ssclab.pl.milp;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.log.SscLogger;
import org.ssclab.pl.milp.Variable.TYPE_VAR;

public class SosGroup {
	public TYPE_VAR typeVar;
	public TYPE_SOS_GROUP typeSos;
	public ArrayList<String> listNomiVar=new ArrayList<String>();
	private static final Logger logger=SscLogger.getLogger();	
	public SosGroup(TYPE_VAR typeVar,TYPE_SOS_GROUP typeSos) {
		this.typeSos=typeSos;
		this.typeVar=typeVar;
	}
	
	
	public enum TYPE_SOS_GROUP {
		SOS1, 
		SOS2, 
	}	
	
	
	@SuppressWarnings("unchecked")
	public SosGroup clone() {
		SosGroup clone=null;
		try {
			clone=(SosGroup)super.clone();
			clone.listNomiVar=(ArrayList<String>)listNomiVar.clone();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.MilpProblem",e);
		}
		return clone;
	}
	
}
