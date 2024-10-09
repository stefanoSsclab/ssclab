package org.ssclab.pl.milp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ssclab.i18n.RB;
import org.ssclab.log.SscLogger;

import org.ssclab.pl.milp.ObjectiveFunction.TARGET_FO;
import org.ssclab.pl.milp.util.VectorsPL;

 final class PLProblem implements Costant , Cloneable, Serializable {
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger=SscLogger.getLogger();	
	private ObjectiveFunctionImpl fo;
	private ArrayList<InternalConstraint> list_constraint;  
	private Var[] array_var;
	private TARGET_FO target_fo= TARGET_FO.MAX;
	private ArrayList<SosGroup> sosGroup;
	
	
		
	public ArrayList<SosGroup> getSosGroup() {
		return sosGroup;
	}

	public void setSosGroup(ArrayList<SosGroup> sosGroup) {
		this.sosGroup = sosGroup;
	}

	public ObjectiveFunctionImpl getObjFunction() {
		return fo;                                
	}
	
	/**
	 * Crea un oggetto PLProblem, questo e' costituito da vincoli (InternalConstraint) intesi come 
	 * rappresentanti delle disequazioni/equazioni del problema (Aj,bj), un f.o.
	 * che rappresenta anche i coefficienti Ci, e degli oggetti Var che 
	 * rappresentano le caratteristiche delle variabili del problema.
	 * 
	 * @param dimension La dimensione iniziale N del problema intesa come numero di variabili 
	 * definite nel problema iniziale , ovvero numero delle variabili legittime dichiarate. 
	 * 
	 * */

	public PLProblem(int dimension) {
		list_constraint=new ArrayList<InternalConstraint>(); 
		initArrayVar(dimension);
		// il vettore C dei costi ha sempre dimensione N
		fo=new ObjectiveFunctionImpl(dimension);
	}
	
	/**
	 *Inizializza gli oggetti oggetti Var che 
	 * rappresentano le caratteristiche delle variabili del problema. Tante Var quanto 
	 * e' la dimensione N iniziale del problema. L'array Var verra' valorizzato in ordine come sono state 
	 * dichiarate le variabili nel problema iniziale 
	 * 
	 * @param dimension La dimensione iniziale N del problema intesa come numero di variabili 
	 * definite nel problema iniziale , ovvero numero delle variabili legittime dichiarate. 
	 * 
	 * */
	
	private void initArrayVar(int dimension) {
		array_var=new Var[dimension] ;
		for(int _j=0;_j< array_var.length;_j++) {
			array_var[_j]=new Var(); 
		}
	}
	
	public void setNameVar(int index,String name_var) {
		array_var[index].setName(name_var);
	}
	
	public void setCjOF(int index, Double value) {
		fo.setCj(index, value);
	}
	
	public void setTargetObjFunction(String target) {
		if(target.equalsIgnoreCase(MIN)) {
			fo.setType(ObjectiveFunctionImpl.TARGET_FO.MIN);
			target_fo= TARGET_FO.MIN;
		}
		else if(target.equalsIgnoreCase(MAX)) {
			fo.setType(ObjectiveFunctionImpl.TARGET_FO.MAX);
			target_fo= TARGET_FO.MAX;
		}	
	}
	
	public TARGET_FO getTarget_fo() {
		return target_fo;
	}

	public Var getVar(int index) {
		return array_var[index];
	}
	
	public Var[] getVariables() {
		return array_var; 
	}
	
	public Var[] getVariablesClone() {
		Var[]  clone_array_var=new 	Var[array_var.length];
		for(int _a=0;_a<clone_array_var.length;_a++) {
			clone_array_var[_a]=array_var[_a].clone();
		}
		return clone_array_var; 
	}

	public void addConstraint(InternalConstraint constraint) {
		list_constraint.add(constraint);
	}
	
	public VectorsPL standardize() {
		
		//standardizza la funzione obiettivo. Se MIN -> MAX
		//ma non cambia il valore this.target_fo
		fo.standardize(); 
		
		//Aggiorno i valori di b con gli lower bound
		//per avere variabili > 0
		{
			double aij,lower,cumulata;
		    for(InternalConstraint constrainte:list_constraint) {
		    	cumulata=0;
				for(int _a=0;_a<array_var.length;_a++) { 
					aij=constrainte.getAij(_a);
					lower=array_var[_a].getLower();
					//System.out.println("name :"+array_var[_a].getName() +" lower:"+lower);
					if(!Double.isInfinite(lower) && lower!=0.0) {
						cumulata+= (lower*aij);
					}  
				}
				double cum=constrainte.getBi()-cumulata;
				constrainte.setBi(cum);
				//System.out.println("cumulata :"+cum);
		    }
		}
		//Se lower o upper != null -> vincoli 
		for(int _j=0;_j< array_var.length;_j++) {
			double lower=array_var[_j].getLower();
			double upper=array_var[_j].getUpper();
			double appo_lower=0;
			if(!Double.isInfinite(lower) && lower!=0.0) {
				appo_lower=lower;
			}
			//solo sugli upper si crea vincolo, in quanto lower -> o traslazione o free
			if(!Double.isInfinite(upper)) {
				InternalConstraint constraint=InternalConstraint.createConstraintFromVar(
						array_var.length, _j, upper - appo_lower, InternalConstraint.TYPE_CONSTR.LE);
				//System.out.println("kkkk"+(upper - appo_lower));
				list_constraint.add(constraint);
			}
		}
		//da mettere alla fine , per evere valori di b positivi, per farlo cambia anche i valori di Aij
		for(InternalConstraint constraint: list_constraint) {
			constraint.standardize_b_positive();
		}
		/*
		for(InternalConstraint constraint: list_constraint) {
			constraint.aprint();
		}*/
		int new_dimension=newDimensionProblemToPhase1();
		VectorsPL vectors_pl=new VectorsPL();
		vectors_pl.B=getVectorB();
		vectors_pl.C=getVectorC(new_dimension);
		vectors_pl.A=getMatrixA(new_dimension);
		return vectors_pl;
	}
	
	
	private double []  getVectorC(int new_dimension) {
		double C[]=new double[new_dimension];
		int index_cj=0;
		for(int _a=0;_a<array_var.length;_a++) {
			double cj=fo.getCj(_a);
			C[index_cj]=cj;
			index_cj++;
			if(array_var[_a].isFree()) {
				if(cj!=0.0) C[index_cj]=-cj;
				else C[index_cj]=0.0;
				index_cj++;
			}
		}
		return C;
	}
	
	private double[][]  getMatrixA(int new_dimension) {  
		double Aij[][]=new double[list_constraint.size()][];
		int index_contr=0;
		int index_Ai=0;
		int index_slack=0;
		double aij;
		for(InternalConstraint constraint: list_constraint) { 
	    	Aij[index_contr]=new double[new_dimension];
	    	index_Ai=0;
			for(int _a=0;_a<array_var.length;_a++) { 
				aij=constraint.getAij(_a);
				Aij[index_contr][index_Ai]=aij;
				index_Ai++;
				if(array_var[_a].isFree()) {
					if(aij!=0) Aij[index_contr][index_Ai]=-aij;
					//else Aij[index_contr][index_Ai]=0.0;
					index_Ai++;
				}
			}
			if(index_slack==0) index_slack=index_Ai;
			if((constraint.getType()==InternalConstraint.TYPE_CONSTR.GE)) {
				Aij[index_contr][index_slack]=-1.0;
				index_slack++;
			}
			else if((constraint.getType()==InternalConstraint.TYPE_CONSTR.LE)) {
				Aij[index_contr][index_slack]=1.0;
				index_slack++;
			}
			constraint.setAi(null);
			index_contr++;
			//System.out.println(""+index_contr);
	    }
		return Aij;
	}
	
	private double [] getVectorB() {
		double B[]=new double[list_constraint.size()];
		int index_b=0;
		for(InternalConstraint constraint: list_constraint) {
			B[index_b]=constraint.getBi();
			index_b++;
		}
		return B;
	}
	
	private int newDimensionProblemToPhase1() {
		int N=array_var.length;
		int N_free=0;
		int N_slacks=0;
		for(int _j=0;_j< N;_j++) {
			if(array_var[_j].isFree()) N_free++;
		}
		for(InternalConstraint constraint: list_constraint) {
			if(constraint.getType()!=InternalConstraint.TYPE_CONSTR.EQ) N_slacks++;
		}
		return N+N_free+N_slacks;
	}
	
	
	public PLProblem clone() {
		
		PLProblem clone=null;
		try {
			clone=(PLProblem)super.clone();
			clone.array_var=array_var.clone();
			for(int _a=0;_a<clone.array_var.length;_a++) {
				clone.array_var[_a]=array_var[_a].clone();
			}
			
			clone.fo=fo.clone();
			clone.list_constraint=new ArrayList<InternalConstraint>();
			
			for(InternalConstraint constraint: list_constraint) {
				clone.list_constraint.add(constraint.clone());
			}
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.MilpProblem",e);
		}
		return clone;
	}
	
	protected void configureFree() throws LPException {
		for(Var var: array_var) {
			var.configureFree();
			//System.out.println("name:"+var.getName()+"  free:"+var.isFree());
		}
	}
	
	public void configureBinary() throws LPException {
		boolean is_present_upper_or_lower_in_var_binary=false; 
		for(Var var:array_var) {
			if(var.getType()==Var.TYPE_VAR.BINARY) {
				if(var.getLower()!= 0.0) {
					is_present_upper_or_lower_in_var_binary=true; 
				}
				else if(!var.isUpperInfinite() && var.getUpper()!= 1.0) {
					is_present_upper_or_lower_in_var_binary=true;
				}
				var.setUpper(1.0); //1.0
				var.setLower(0.0); //0.0
			}
		}
		if(is_present_upper_or_lower_in_var_binary) {
			logger.log(Level.WARNING,RB.getString("it.ssc.pl.milp.MilpProblem.msg1"));
		}
	}
	
	
	public void configureSemicont() throws LPException {
		for(Var var:array_var) {
			if(var.isSemicon()) { 
				double upper=var.getUpper();
				double lower=var.getLower();
				//var.resetUpperLower();
				var.resetLower();
				var.setUpperSemicon(upper);
				var.setLowerSemicon(lower);
				if((lower <= 0.0 || upper <= 0.0)) {
					throw new LPException(RB.format("it.ssc.pl.milp.MilpProblem.msg3", var.getName()));
				}
				//se semicontinua upper-lower non possono contenere lo zero
				if((Double.isInfinite(lower) || lower <= 0.0) && (Double.isInfinite(upper) || upper >= 0.0)) {
					throw new LPException(RB.format("it.ssc.pl.milp.MilpProblem.msg2", var.getName()));
				}
				
			}
		}
	}

	public ArrayList<InternalConstraint> getListConstraint() {
		return list_constraint;
	}
}
