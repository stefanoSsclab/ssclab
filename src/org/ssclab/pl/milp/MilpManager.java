package org.ssclab.pl.milp;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.context.Session;
import org.ssclab.context.exception.InvalidSessionException;
import org.ssclab.datasource.DataSource;
import org.ssclab.i18n.RB;
import org.ssclab.log.SscLogger;
import org.ssclab.pl.milp.FormatTypeInput.FormatType;
import org.ssclab.pl.milp.ObjectiveFunction.TARGET_FO;
import org.ssclab.pl.milp.Variable.TYPE_VAR;
import org.ssclab.pl.milp.util.VectorsPL;
import org.ssclab.ref.Input;
import org.ssclab.step.parallel.Parallelizable;
import org.ssclab.pl.milp.simplex.Simplex;
import org.ssclab.pl.milp.simplex.SimplexInterface;
import org.ssclab.pl.milp.simplex.SimplexException;


 final class MilpManager implements Cloneable,  Parallelizable,  Comparable<MilpManager> { 
	
	public enum VERSUS_SEPARATION { MINOR , MAJOR , ZERO , INTERVAL}; 
	
	private static final Logger logger=SscLogger.getLogger();
	private volatile static int static_counter=1; 
	private int id;
	private PLProblem pl_current;
	private static PLProblem pl_original_zero;
	private int num_iteration;
	private final boolean isMilp=true;

	private EPSILON epsilon;
	private EPSILON iepsilon; 
	private EPSILON cepsilon;
	
	private SolutionImpl solution_pl;
	private SolutionType solutionType;
	private MILP father;
	
	
	MilpManager(Input input_sparse,Session session, FormatType format, MILP father) throws InvalidSessionException, Exception {
		id=createId();  
		DataSource milp_data_source=session.createDataSource(input_sparse);
		if(format==FormatType.SPARSE) pl_current=CreatePLProblem.createFromSparse(milp_data_source,isMilp);
		else if(format==FormatType.COEFF) pl_current=CreatePLProblem.create(milp_data_source,isMilp);
		pl_current.configureInteger();
		pl_current.configureSemicont();
		
		this.father=father;
		this.father.father_pl_original_zero=pl_current.clone(); 
	}
	
	
	/*
	 * Il MilpManager è la classe che gestisce il MILP. 
	 * Durante la fase del costruttore genera un primo PLProblem chiamato pl_current
	 * 
	 * 
	 */
	
	
	
	MilpManager(LinearObjectiveFunction f,ArrayList<Constraint> constraints, MILP father) throws InvalidSessionException, Exception {
		id=createId(); 
		pl_current=CreatePLProblem.create(f,constraints,isMilp);
		pl_current.configureInteger();
		pl_current.configureSemicont();
		
		this.father=father;
		this.father.father_pl_original_zero=pl_current.clone(); 
	}
	
	
	MilpManager(LinearObjectiveFunction f,ArrayList<InternalConstraint> constraints,ArrayList<String> nomi_var,ArrayProblem arrayProb,  MILP father) throws InvalidSessionException, Exception {
		id=createId(); 
		pl_current=CreatePLProblem.create(f,constraints,nomi_var,arrayProb,isMilp);  
		pl_current.configureInteger();
		pl_current.configureSemicont();
		
		this.father=father;
		this.father.father_pl_original_zero=pl_current.clone(); 
	}
	
	
	MilpManager(Input milp_input,Session session, MILP father) throws InvalidSessionException, Exception {
		id=createId(); 
		DataSource milp_data_source=session.createDataSource(milp_input);
		pl_current=CreatePLProblem.create(milp_data_source,isMilp);
		pl_current.configureInteger();
		pl_current.configureSemicont();
		
		this.father=father;
		this.father.father_pl_original_zero=pl_current.clone(); 
	}
	
	void setEpsilon(EPSILON epsilon) {
		this.epsilon=epsilon;
	}
	
	void setIEpsilon(EPSILON epsilon) {
		this.iepsilon=epsilon; 
	}
	
	void setCEpsilon(EPSILON epsilon) {
		this.cepsilon=epsilon; 
	}
	
	void setMaxIteration(int num_iteration) throws SimplexException  {
		this.num_iteration=num_iteration;
	}
	
	public void run() throws Exception {
		resolve();
	}
	
	SolutionType resolve() throws Exception {
		
		//necessario clonare ?  Si il pl_current non viene mai eseguito, ne standardizzato, 
		//ma viene standardizzato ed eseguito un suo clone, quindi il MilpManager ha un riferimento integro 
		//del problema di programmazione lineare da risolvere senza stravolgimenti dovuti alla
		//standardizzazione. 
		PLProblem lp_standard=pl_current.clone();  
		
		/*
		 * pl_original_zero viane inizializzato la prima volta se e a null. 
		 */
		
		 pl_original_zero=father.father_pl_original_zero;    //forse non occorre clonare ????? TOLTO clone 19/03/2024
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornado bi se esiste una o piu' variabili con lower != 0 o da .
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A (matrice standard)
		 */
		
		VectorsPL vectors_pl=lp_standard.standardize(); 
		
		
		/*
		printTableA( vectors_pl.A);
		printTableV( vectors_pl.B);
		printTableV( vectors_pl.C);
		*/
			
		SimplexInterface simplex=new Simplex(vectors_pl.A, vectors_pl.B, vectors_pl.C,epsilon,cepsilon);
		simplex.setNumIterationMax(num_iteration);
		simplex.setMilp(true);
		
		this.solutionType=simplex.runPhaseOne();
		if(this.solutionType==SolutionType.OPTIMUM) { 
			this.solutionType =simplex.runPhaseTwo();
			this.solution_pl=new SolutionImpl(this.solutionType,
											  pl_current, //dovevo passare un clone in quanto modifiva l'array di Var
											  simplex.getFinalBasis(),
											  simplex.getFinalValuesBasis(),
											  pl_current.getVariables());
		}	
		
		return this.solutionType;
	}
	
	public double getOptimumValue() {
		 return solution_pl.getOptimumValue();
	}
	
	public TARGET_FO getTargetFoOriginal() {
		return pl_original_zero.getTarget_fo();
	}
	
	
	 MilpManager getCloneBySeparationContinus(int index_var,VERSUS_SEPARATION versus) throws CloneNotSupportedException, LPException {
		
		MilpManager clone_separation=clone();
		Var variable=clone_separation.pl_current.getVariables()[index_var];
		variable.setSemicon(false);
		if(versus==VERSUS_SEPARATION.ZERO)  {
			int num_tot_var= this.solution_pl.getVariables().length;
			//System.out.println(index_var+":ZERO-ZERO"+"   ID_CLONE"+clone_separation.getId() );
			InternalConstraint constraint=InternalConstraint.createConstraintFromVar(num_tot_var, index_var, 0.0, InternalConstraint.TYPE_CONSTR.LE);
			clone_separation.pl_current.addConstraint(constraint);
			variable.setZeroSemicontVar(true);
		}
		else {
			variable.setUpper(variable.getUpperSemicon());
			variable.setLower(variable.getLowerSemicon());
			variable.configureFree();
		}
		return clone_separation;
	}
	
	 MilpManager getCloneBySeparationInteger(int index_var,VERSUS_SEPARATION versus) throws CloneNotSupportedException, LPException {
		double value=this.solution_pl.getVariables()[index_var].getValue();
		MilpManager clone_separation=clone();
		Var varc= clone_separation.pl_current.getVariables()[index_var];
		double upper=varc.getUpper();
		double lower=varc.getLower();
		varc.resetUpperLower();
		if(versus==VERSUS_SEPARATION.MINOR)  {
			//5 =Math.floor(5.5);
			value=Math.floor(value);
			//constraint=InternalConstraint.createConstraintFromVar(num_tot_var, index_var, value, InternalConstraint.TYPE_CONSTR.LE);
			//PUO CAPITARE ? Forse si. Se il lower e' per esempio 5.2 -> Xj < 5 e Xj > 5.2 -> return null
			if(lower > value)  {
				return null;
			}
			varc.setLower(lower);
			varc.setUpper(value);
		}
		else {
			//6 =Math.floor(5.5);
			value=Math.ceil(value);
			//constraint=InternalConstraint.createConstraintFromVar(num_tot_var, index_var, value, InternalConstraint.TYPE_CONSTR.GE);
			if(upper < value) {
				return null;
			}
			varc.setLower(value);
			varc.setUpper(upper);
		}
		//clone_separation.pl_current.addConstraint(constraint);
		varc.configureFree();
		return clone_separation;
	 }
	 
	 static public void  populateArrayListBySeparation(ArrayList<MilpManager> listMangerMilp, MilpManager milp_current2) throws CloneNotSupportedException, LPException {
			MilpManager milp_sotto2,milp_sopra2;
			if(!milp_current2.isProblemSemiContinusAmmisible()) {
				int index_var_not_cont2=milp_current2.getIndexVarToBeSemiContinus();
				milp_sotto2=milp_current2.getCloneBySeparationContinus(index_var_not_cont2, VERSUS_SEPARATION.ZERO);
				milp_sopra2=milp_current2.getCloneBySeparationContinus(index_var_not_cont2, VERSUS_SEPARATION.INTERVAL);
			}
			else {
				int index_var_not_integer2=milp_current2.getIndexVarToBeInteger();
				//System.out.println("DIVIDO PROBLEMA ID:"+milp_current.getId()+" z:"+milp_current.getOptimumValue());
				milp_sotto2=milp_current2.getCloneBySeparationInteger(index_var_not_integer2, VERSUS_SEPARATION.MINOR);
				milp_sopra2=milp_current2.getCloneBySeparationInteger(index_var_not_integer2, VERSUS_SEPARATION.MAJOR);
			}	
			if(milp_sotto2!=null) listMangerMilp.add(milp_sotto2);
			if(milp_sopra2!=null) listMangerMilp.add(milp_sopra2);
		}
	
	 int getIndexVarToBeSemiContinus() {
		int index =0;
		Var[] variables= this.pl_current.getVariables();
		for(Var variable:variables) {
			if(variable.isSemicon() ) {
				return index;
			}
			index++;
		}
		return -1;
	}
	 
	 
	public boolean existVarToBeIntegerOrSemicon() {
		Var[] variables= this.pl_current.getVariables();
		for(Var variable:variables) {
			//System.out.println(variable.getType());
			if(variable.getType()== TYPE_VAR.BINARY || variable.getType()== TYPE_VAR.INTEGER ) {
				return true;
			}
			if(variable.isSemicon()) return true;
		}
		return false;
	}
	
	private int getIndexVarToBeInteger() {
		int index =0;
		Var[] variables= this.solution_pl.getVariables();
		for(Var variable:variables) {
			if(variable.getType()== TYPE_VAR.BINARY || variable.getType()== TYPE_VAR.INTEGER ) {
				double value=variable.getValue();
				if(!isInteger(value)) {
					return index;
				}
			}
			index++;
		}
		return -1;
	}
	
	
	/*
	@SuppressWarnings("unused")
	private int getIndexVarToBeIntegerNew2Test() {
		int index =0;
		int index_bono=-1;
		double fraction=0.0;
		Var[] variables= this.solution_pl.getVariables();
		for(Var variable:variables) {
			if(variable.getType()== TYPE_VAR.BINARY || variable.getType()== TYPE_VAR.INTEGER ) {
				double value=variable.getValue();
				if(!isInteger(value)) {
					if(index_bono==-1) {
						index_bono= index;
						fraction= value % 1;
					}
					else if(fraction < (value % 1)) {
						index_bono= index;
						fraction= value % 1;
					}
				}
			}
			index++;
		}
		return index_bono;
	}
	*/
	
	public  boolean isProblemSemiContinusAmmisible()  {
		boolean is_continus_ammisible=true;
		Var[] variables= this.pl_current.getVariables();
		for(Var variable:variables) {
			if(variable.isSemicon()) is_continus_ammisible=false;
		}
		return is_continus_ammisible;
	}
	
	public boolean isSolutionIntegerAmmisible()  {
		boolean is_integer_ammisible=true;
		Var[] variables= this.solution_pl.getVariables();
		for(Var variable:variables) {
			if(variable.getType()== TYPE_VAR.BINARY || variable.getType()== TYPE_VAR.INTEGER ) {
				double value =variable.getValue();
				if(!isInteger(value)) { 
					is_integer_ammisible=false;
				}
			}
		}
		return is_integer_ammisible;
	}
	
	public void setIntegerIfOptimal() {
		Var[] variables= this.solution_pl.getVariables();
		for(Var variable:variables) {
			if(variable.getType()== TYPE_VAR.BINARY || variable.getType()== TYPE_VAR.INTEGER ) {
				double value =variable.getValue();
				value=Math.rint(value);
				variable.setValue(value);
			}
		}
	}
	
	
	private  boolean isInteger(double d) { 
		  // Note that Double.NaN is not equal to anything, even itself.
		  return  !Double.isInfinite(d) && 
		          ( Math.abs(d - Math.rint(d))  <= iepsilon.getValue()  ) ;
	}
	
	
	public Solution getSolution()  {
		return this.solution_pl;
	}
	
	
	public int  getId() {
		return id;
	}
	
	private static synchronized  int createId() {
		return static_counter++;
	}
	
	
	protected MilpManager clone() throws CloneNotSupportedException {
		MilpManager milp_clone=null;
		try {
			milp_clone = (MilpManager)super.clone();
			milp_clone.pl_current=this.pl_current.clone();
			milp_clone.solution_pl=null;
			milp_clone.solutionType=null;
			milp_clone.id=MilpManager.createId();
		} 
		catch (CloneNotSupportedException e) {
			logger.log(Level.SEVERE,"Clonazione it.ssc.pl.milp.ManagerMILP",e);
			throw e;
		}
		return milp_clone;
	}

	public SolutionType getSolutionType() {
		return solutionType;
	}

	@Override
	public int compareTo(MilpManager arg0) {
		final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

		if( arg0.getOptimumValue() == this.getOptimumValue()) return EQUAL;
		else if( arg0.getOptimumValue() > this.getOptimumValue()) return AFTER;
		else return BEFORE;
	}

	@Override
	public int hashCode() {
		return (31 + id);
	}

	@Override
	public boolean equals(Object obj) {
		MilpManager other = (MilpManager) obj;
		if (id != other.id) return false;
		return true;
	}
	
	
	@SuppressWarnings("unused")
	private void printTableA(double[][] tabella) {
		for(int _i=0;_i<tabella.length;_i++) {
			System.out.println("");
			for(int _j=0;_j<tabella[0].length;_j++) {
				double val=tabella[_i][_j];
				System.out.printf("\t : %7.14f",val);
			}
		}
		System.out.println("");
	}
	
	@SuppressWarnings("unused")
	private void printTableV(double[] vector) {
		for(int _j=0;_j<vector.length;_j++) {
			double val=vector[_j];
			System.out.printf("\t : %7.14f",val);
		}
		
		System.out.println("");
	}
	
}

