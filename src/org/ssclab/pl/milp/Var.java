package org.ssclab.pl.milp;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.i18n.RB;
import org.ssclab.log.SscLogger;

final class Var implements Cloneable, Variable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//QUESTA NON VA SERIALIZZATA ... VERIFICARE
	private static final Logger logger=SscLogger.getLogger();
	private String name;
	private TYPE_VAR type=TYPE_VAR.REAL;
	private double upper;
	private double lower;
	private boolean is_free;
	private double value;
	//Questa variabile mi serve per capire se il lower e stato modificato, 
	//ovvero se esiste un valore lower definito dall'utente pwer questa variabile
	//in quanto se lower non e' stato definito e upper viene settato a  < 0 -> variabile libera 
	//e lower = -INFINITO
	private boolean is_lower_modified;
	private boolean isZeroSemicontVar;
	
	//parte per variabili semi-continue
	private double upperSemicon; 
	private double lowerSemicon; 
	private boolean isSemicon;
	//sta ad indicare che questa variabile e stata elaborata per la suddivisione, 
	//altrimenti risulta senza nessun limite. 
	//private boolean isElaborated; non serve
	
	/*
	public boolean isElaborated() {
		return isElaborated;
	}

	public void setElaborated(boolean isElaborated) {
		this.isElaborated = isElaborated;
	}
	*/
	public Var() {
		lower=0.0;   
		upper=Double.POSITIVE_INFINITY;
		lowerSemicon=0.0;
		is_lower_modified=false;
		isZeroSemicontVar=false;
		is_free=false;
	
	}
	
	public boolean isZeroSemicontVar() {
		return isZeroSemicontVar;
	}
	public void setZeroSemicontVar(boolean isZeroSemicontVar) {
		this.isZeroSemicontVar = isZeroSemicontVar;
	}
	
	
	public void resetUpperLower() {
		lower=0.0;   
		upper=Double.POSITIVE_INFINITY;
		is_free=false;
		is_lower_modified=false;
	}
	
	public double getUpperSemicon() {
		return upperSemicon;
	}

	public void setUpperSemicon(Double upperSemicon) {
		this.upperSemicon = upperSemicon;
	}

	public double getLowerSemicon() {
		return lowerSemicon;
	}

	public void setLowerSemicon(Double lowerSemicon) {
		this.lowerSemicon = lowerSemicon;
	}

	public boolean isSemicon() {
		return isSemicon;
	}

	public void setSemicon(boolean isSemicon) {
		this.isSemicon = isSemicon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public TYPE_VAR getType() {  
		return type;
	}
	
	

	public void setType(TYPE_VAR type) {
		this.type = type;
	}

	public double getUpper() {
		return upper;
	}
	
	public boolean isUpperInfinite() {
		return Double.isInfinite(upper);
	}
	
	public boolean isLowerInfinite() {
		return Double.isInfinite(lower);
	}
	
	public void setLower(Double lower) throws LPException {
		
		if(lower==null || Double.isNaN(lower)) lower=Double.NEGATIVE_INFINITY;
		this.lower = lower;
		//per verificare che se upper < 0 , e l'utente ha definito un lower questo deve essere 
		//minore dell'upper, altrimenti, se non ha definito nulla, lower = -INFINITO
		this.is_lower_modified=true;
	}
	

	public void setUpper(Double upper) throws LPException {
		if(upper==null || Double.isNaN(upper)) upper=Double.POSITIVE_INFINITY;
		this.upper = upper;
	}
	
	public void configureFree() throws LPException {
		//se si dichiara solo l'upper, e questo e' < 0 , automaticamente la variabile e' 
		//libera e lower = -inf (vedi quattro righe sotto)
		if(this.is_lower_modified) {
			if(this.lower > this.upper) throw new LPException(RB.format("it.ssc.pl.milp.Var.msg1", lower,upper));
		}
		else {
			if(this.upper < 0.0) {
				//imposto il lower a -inf
				this.lower=Double.NEGATIVE_INFINITY;
			}
		}
		if(Double.isInfinite(this.lower)) this.is_free=true; 
	}

	public double getLower() {
		return this.lower;
	}
	
	
	public boolean isFree() {
		return is_free;
	}

	public Var clone() {
	
		Var clone=null;
		try {
			clone=(Var)super.clone();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.Var",e);
		}
		return clone;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double val) {
		this.value=val;
	}
}
