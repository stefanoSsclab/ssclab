package org.ssclab.pl.milp;

import org.ssclab.i18n.RB;

/**
 * This class allows you to build objects, each of which represents a constraint for a linear 
 * programming problem expressed in matrix notation 
 * 
 * @author Stefano Scarioli 
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 */



public final class Constraint {
	private double[] Aj;
	private ConsType rel;
	private double bi;
	private String name;
	
	/**
	*
	*@param Aj The LHS part of the j-th constraint of the problem
	*@param rel The type of constraint/relation (EQ, LE, GE, UPPER, LOWER, INT, BIN, SEMICONT)
	*@param rhs The RHS part of the constraint or coefficient bj
	*@throws LPException If the constraint is not congruent
	*/
	
	public Constraint(double[] Aj, ConsType rel, double rhs) throws LPException {
		if(Aj==null) throw new LPException(RB.getString("it.ssc.pl.milp.Constraint.msg1"));
		this.Aj=Aj;
		if(rel==null) throw new LPException(RB.getString("it.ssc.pl.milp.Constraint.msg2"));
		this.rel=rel;
		this.bi=rhs;
	}
	
	
	/**
	*
	*@param Aj The LHS part of the j-th constraint of the problem
	*@param rel The type of constraint/relation (EQ, LE, GE, UPPER, LOWER, INT, BIN, SEMICONT)
	*@param rhs The RHS part of the constraint or coefficient bj
	*@param name The name of the constraint 
	*@throws LPException If the constraint is not congruent
	*/
	
	public Constraint(double[] Aj, ConsType rel, double rhs,String name) throws LPException {
		this(Aj,rel, rhs);
		this.name=name;
	}
	
	
	

   /**
    * @return The LHS part of the j-th constraint of the problem
    */

	public double[] getAj() {
		return Aj;
	}
	
	/**
	 * 
	 * @return  The type of constraint (relation) defined (EQ, LE, GE)
	 */

	public ConsType getRel() {
		return rel;
	}
	
	/**
	 * 
	 * @return The RHS part of the constraint or coefficient bj
	 */

	public double getRhs() {
		return bi;
	}


	public String getName() {
		return name;
	}


	public void  setName(String name) {
		this.name = name;
	
	}
}
