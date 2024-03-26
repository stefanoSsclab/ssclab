package org.ssclab.pl.milp;

import org.ssclab.i18n.RB;

/**
 * This class allows instantiating objects representing the objective function in linear
 *  programming problems expressed in matrix notation.
 * 
 * @author Stefano Scarioli
 * @version 1.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a> 
 *
 */

public final class LinearObjectiveFunction {
	
	private GoalType type;
	private double[] C;
	
	/**
	 * 
	 * @return  The type of optimization (MAX or MIN))
	 */
	
	public GoalType getType() {
		return type;
	}
	/**
	 * 
	 * @return The vector of coefficients of the objective function
	 */

	public double[] getC() {
		return C;
	}

	/**
	*Constructor
	*@param C The vector of coefficients of the objective function
	*@param type The type of optimization (MAX or MIN) as an instance of the GoalType enumeration
	*@throws LPException If the parameters are incongruent with the problem
	*/

	public LinearObjectiveFunction(double[] C, GoalType type) throws  LPException {
		if(type==null) throw new LPException(RB.getString("it.ssc.pl.milp.LinearObjectiveFunction.msg1"));
		this.type=type;
		if(C==null) throw new LPException(RB.getString("it.ssc.pl.milp.LinearObjectiveFunction.msg2"));
		this.C=C;
	}

}
