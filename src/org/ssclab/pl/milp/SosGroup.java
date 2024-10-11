package org.ssclab.pl.milp;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.log.SscLogger;
import org.ssclab.pl.milp.Variable.TYPE_VAR;

public class SosGroup {

	public TYPE_SOS_GROUP typeSos;
	public ArrayList<GroupVar> listVar;
	private static final Logger logger=SscLogger.getLogger();	
	
	public SosGroup(TYPE_SOS_GROUP typeSos) {
		this.typeSos=typeSos;
		listVar=new ArrayList<GroupVar>();
	}
	
	public enum TYPE_SOS_GROUP {
		SOS1, 
		SOS1_BIN, 
		SOS1_BIN_FORCE, 
		SOS1_INT, 
		SOS2, 
		SOS2_BIN, 
		SOS2_BIN_FORCE, 
		SOS2_INT
	}	
	
	public void addVar(String name, int index) {
		listVar.add(new GroupVar(name,index));
	}
	
	public int size() {
		return listVar.size();
	}
	
	
	@SuppressWarnings("unchecked")
	public SosGroup clone() {
		SosGroup clone=null;
		try {
			clone=(SosGroup)super.clone();
			clone.listVar=(ArrayList<GroupVar>)listVar.clone();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.SosGroup",e);
		}
		return clone;
	}
	
	static public class GroupVar {
		public String name;
		public int index;
		public GroupVar(String name, int index) {
			this.name=name;
			this.index=index;
		}
	}
}
