package org.ssclab.pl.milp;

import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.context.Context; 
import org.ssclab.context.Session;
import org.ssclab.i18n.RB;
import org.ssclab.log.SscLevel;
import org.ssclab.log.SscLogger;
import org.ssclab.pl.milp.ObjectiveFunction.TARGET_FO;
import org.ssclab.pl.milp.scanjson.ScanConstraintFromJson;
import org.ssclab.pl.milp.scanjson.ScanSintaxJson;
import org.ssclab.pl.milp.util.MILPThreadsNumber;
import org.ssclab.ref.Input;
import org.ssclab.step.parallel.Task;
import org.ssclab.pl.milp.simplex.SimplexException;
import org.ssclab.pl.milp.scantext.CheckSintaxText;
import org.ssclab.pl.milp.scantext.ScanConstraintFromLine;
import org.ssclab.pl.milp.scantext.ScanFoFromLine;
import org.ssclab.pl.milp.scantext.ScanVarFromText;



/**
 * This class allows executing and solving formulations of mixed integer linear programming problems,
 * binary or semicontinuous. The method used for solving such optimization problems 
 * is the simplex method combined with the Branch and Bound method.
 * 
 * @author Stefano Scarioli
 * @version 4.2
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a> 
 */

public final class MILP implements FormatTypeInput {
	
	public static double NaN=Double.NaN;
	
	private Epsilons epsilons=new Epsilons();
	private static final Logger logger=SscLogger.getLogger(); 
	
	private MilpManager milp_initiale;
	private LB lb=new LB();;
	private int num_max_simplex  =1_000_000;
	private int num_max_iteration=10_000_000;
	private boolean isJustTakeFeasibleSolution;
	private MILPThreadsNumber threadNumber=MILPThreadsNumber.N_1;
	//private double stepDisadvantage=0.0;
	protected PLProblem father_pl_original_zero;

	{
		logger.log(Level.INFO,  "##############################################");
		logger.log(Level.INFO,  RB.getString("it.ssc.context.Session_Impl.msg0"));
		logger.log(Level.INFO,  "##############################################");
	}
	
	
	/**
	 * Constructor of a MILP object for solving problems formulated in inequality format contained in
	 * an external file.
	 * 
	 * @param pl_text String containing the problem in text format
	 * @throws Exception if the problem is not correctly formulated
	 */
	
	public MILP(String pl_text) throws Exception  { 
		
		BufferedReader br=null;
		ScanConstraintFromLine scan_const;
		ArrayList<String> list_var;
		LinearObjectiveFunction fo;
		try {
			br= new BufferedReader(new StringReader(pl_text));
			String line_fo=new CheckSintaxText(br).getLineFO();
		    br.close();br=null;
		    br= new BufferedReader(new StringReader(pl_text));
			list_var=new ScanVarFromText(br).getListNomiVar();
			br.close();br=null;
			br= new BufferedReader(new StringReader(pl_text));
			//for(String namev:list_var) System.out.println("name_ord :"+namev);
			ScanFoFromLine fo_from_string=new ScanFoFromLine(line_fo,list_var);
			fo=fo_from_string.getFOFunction();
			scan_const=new ScanConstraintFromLine(br,list_var);
		}
		finally {
			if (br != null ) br.close();
		}
		
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		this.milp_initiale = new MilpManager(fo,list_constraints,list_var,scan_const.getArraysProblem(),this);
		setAllEpsilon();
	}
	
	
	/**
	 * Constructor of a MILP object for solving problems formulated in inequality format contained in
	 * an ArrayList of Strings.
	 * 
	 * @param inequality The ArrayList with the problem
	 * @throws Exception if the ArrayList is null or empty
	 */
	public MILP(ArrayList<String> inequality) throws Exception  { 
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		
	    String line_fo=new CheckSintaxText(inequality).getLineFO();
		ArrayList<String> list_var=new ScanVarFromText(inequality).getListNomiVar();
		//for(String namev:list_var) System.out.println("name_ord :"+namev);
		ScanFoFromLine fo_from_string=new ScanFoFromLine(line_fo,list_var);
		LinearObjectiveFunction fo=fo_from_string.getFOFunction();
		ScanConstraintFromLine scan_const=new ScanConstraintFromLine(inequality,list_var);
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		this.milp_initiale = new MilpManager(fo,list_constraints,list_var,scan_const.getArraysProblem(),this);
		setAllEpsilon();
	}
	
	
	/**
	 * Constructor of a MILP object for solving problems formulated in inequality format contained in
	 * an external file.
	 * 
	 * @param path Path of the file containing the problem in inequality format
	 * @throws Exception if the problem is not correctly formulated
	 */
	
	public MILP(Path path) throws Exception  { 
		
		BufferedReader br=null;
		ScanConstraintFromLine scan_const;
		ArrayList<String> list_var;
		LinearObjectiveFunction fo;
		try {
			br=Files.newBufferedReader(path);
		    String line_fo=new CheckSintaxText(br).getLineFO();
		    br.close();br=null;
		    br=Files.newBufferedReader(path);
			list_var=new ScanVarFromText(br).getListNomiVar();
			br.close();br=null;
			br=Files.newBufferedReader(path);
			//for(String namev:list_var) System.out.println("name_ord :"+namev);
			ScanFoFromLine fo_from_string=new ScanFoFromLine(line_fo,list_var);
			fo=fo_from_string.getFOFunction();
			scan_const=new ScanConstraintFromLine(br,list_var);
		}
		finally {
			if (br != null ) br.close();
		}
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		this.milp_initiale = new MilpManager(fo,list_constraints,list_var,scan_const.getArraysProblem(),this);
		setAllEpsilon();
	}
	
	
	
	/**
	 * Constructor of a MILP object for solving problems expressed in matrix format.
	 * 
	 * @param fo A LinearObjectiveFunction object representing the objective function
	 * @param constraints The list of constraints
	 * @throws Exception An exception is thrown if the problem is not correctly formulated
	 */
	
	public MILP(LinearObjectiveFunction fo,ArrayList<Constraint> constraints) throws  Exception {
		this.milp_initiale = new MilpManager(fo, constraints,this,null);
		setAllEpsilon();
	}
	
	

	
	/**
	 * Constructor of a MILP object for solving problems expressed in matrix format.
	 * 
	 * @param fo A LinearObjectiveFunction object representing the objective function
	 * @param constraints The list of constraints
	 * @throws Exception An exception is thrown if the problem is not correctly formulated
	 */
	
	public MILP(LinearObjectiveFunction fo,ListConstraints constraints) throws  Exception {
		this.milp_initiale = new MilpManager(fo, constraints.getListConstraint(),this,null);
		setAllEpsilon();
	}
	
	
	
	
	
	
	
	

	/**
	 * Constructor of a MILP object for solving problems expressed in coefficient format.
	 * 
	 * @param input The problem formulated in coefficient format
	 * @throws Exception An exception is thrown if the problem is not formulated correctly
	 */
	
	public MILP(Input input) throws  Exception {
		Session session=Context.createNewSession();
		this.milp_initiale = new MilpManager(input, session, this); 
		logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.MILP.msg1"));
		session.close();
		setAllEpsilon();
	}
	
	/**
	 * Constructor of a MILP object for solving problems expressed in coefficient format.
	 * 
	 * @param input The problem formulated in coefficient format
	 * @param session An SSC working session 
	 * @throws Exception An exception is thrown if the problem is not formulated correctly
	 */
	
	public MILP(Input input,Session session) throws  Exception {
		this.milp_initiale = new MilpManager(input, session,this); 
		setAllEpsilon();
	}
	
	
	/**
	 * Constructor of a MILP object for solving problems expressed in either sparse or coefficient format.
	 * 
	 * @param input The problem formulated in sparse or coefficient format
	 * @param session An SSC working session 
	 * @param format The type of format used (FormatType.SPARSE or FormatType.COEFF)
	 * @throws Exception An exception is thrown if the problem is not formulated correctly
	 */
	
	public MILP(Input input,Session session, FormatType format) throws Exception {
		this.milp_initiale = new MilpManager(input, session,format,this); 
		setAllEpsilon();
	}
	
	/**
	 * Constructor of a MILP object for solving problems expressed in sparse or coefficient format.
	 * 
	 * @param input The problem formulated in sparse format
	 * @param format The type of format used (FormatType.SPARSE or FormatType.COEFF)
	 * @throws Exception An exception is thrown if the problem is not formulated correctly
	 */
	
	public MILP(Input input,FormatType format) throws  Exception {
		Session session=Context.createNewSession();
		this.milp_initiale = new MilpManager(input, session,format,this);
		logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.MILP.msg1"));
		session.close();
		setAllEpsilon();
	}
	
	/**
	 * 
	 * @return the maximum number of iterations that each simplex can execute
	 */
	

	public int getNumMaxIterationForSingleSimplex() {
		return num_max_iteration;
	}
	
	/**
	 * Method to set the number of iterations for each individual simplex.
	 * 
	 * @param num_max_iteration The maximum number of iterations that each simplex can execute
	 * @throws SimplexException If an error occurs during the process
	 * @return the MILP instance (this) on which the method call is being made
	 */

	public MILP setNumMaxIterationForSingleSimplex(int num_max_iteration) throws SimplexException {
		if(num_max_iteration <= 0) throw new SimplexException(RB.getString("it.ssc.pl.milp.MILP.msg7"));
		this.num_max_iteration = num_max_iteration;
		return this;
	}
	
	/**
	 * 
	 * @return the maximum number of simplexes executable in the Branch and Bound procedure
	 */

	public int getNumMaxSimplexs() {
		return num_max_simplex;
	}
	
	/**
	 * Method to set the maximum number of simplexes.
	 * 
	 * @param num_max_simplex the maximum number of simplexes executable in the Branch and Bound procedure
	 */

	public void setNumMaxSimplexs(int num_max_simplex) {
		this.num_max_simplex = num_max_simplex;
	}

	private void setAllEpsilon() {
		this.milp_initiale.setEpsilons(epsilons);
	
	}
	
	/**
	 * This method allows setting the epsilon value relative to the tolerance that intervenes in various aspects of the simplex. It is used in the following cases: <br>
	 * 
	 * 1) During phase one, both in determining the entering variable and in determining the exiting variable with or without the Bland rule.
	 *    Also to determine if the base is degenerate. It is also used at the end of phase one: if there is an auxiliary variable in the base, 
	 *    epsilon is used to determine if it is possible to eliminate the rows and columns of these on the extended table. <br>
	 * 2) During phase two, both in determining the entering variable and in determining the exiting variable with or without the Bland rule.
	 *    Also to determine if the base is degenerate.
	 * 
	 * @param epsilon Tolerance used in various phases of the simplex. Default value 1E-10
	 * @return the MILP instance (this) on which the method call is being made
	 */
	
	public MILP setEpsilon(EPSILON epsilon) {
		epsilons.epsilon= epsilon;
		return this;
	}
	
	/**
	 * This method allows setting the epsilon value relative to the tolerance for
	 * determining if an optimal solution of phase 1 of the simplex is close to or equal to zero and thus gives rise to
	 * feasible solutions for the problem.
	 * 
	 * @param cepsilon Tolerance of phase 1 solution with respect to zero. Default value 1E-8
	 * @return the MILP instance (this) on which the method call is being made
	 */
	
	public MILP setCEpsilon(EPSILON cepsilon) {
		epsilons.cepsilon= cepsilon;
		return this;
	}
	
		
	/**
	 * This method allows setting the epsilon value relative to the tolerance for
	 * determining if a number should be considered integer or not. This check
	 * occurs when at the end of the simplex the solution found is evaluated to satisfy the integer condition
	 * on the variables that must be integers. Let x be a number and Int(x) the nearest integer to x, if
	 *  | Int(x) - x | &lt; epsilon -&gt; x &#x2208; Z
	 * 
	 * @param iepsilon Tolerance to consider a number as integer. Default value 1E-10
	 * @return the MILP instance (this) on which the method call is being made
	 */
	
	public MILP setIEpsilon(EPSILON iepsilon) {
		 epsilons.iepsilon= iepsilon; 
		 return this;
	}
	
	/*
	 * Executes the Branch and Bound algorithm.
	 * 
	 * @return The type of solution found
	 * @throws Exception If the execution process generates an error
	 */
	
	/*
	public SolutionType resolve() throws Exception {
		
		logger.log(SscLevel.INFO,RB.format("it.ssc.pl.milp.MILP.msg10")+threadNumber.getThread());
		
		//if(threadNumber==MILPThreadsNumber.N_1) return resolveSingleThread();
		// else 
		 return resolve2() ;
	}
	*/
	
	/*
	private SolutionType resolveSingleThread() throws Exception {
		
		int num_simplex_resolved=1;
		long start=System.currentTimeMillis();
		
		SolutionType type_solution=SolutionType.VUOTUM;
		ArrayList<MilpManager> listMangerMilpToRun=null;
		
		MilpManager milp_current=milp_initiale;    //INIZIALMENTE E' QUELLO INIZIALE (!)
		milp_current.setMaxIteration(num_max_iteration);
		
		SolutionType type_solution_initial=milp_current.resolve(); 
		TARGET_FO target=milp_initiale.getTargetFoOriginal();
		TreeV3 tree=new TreeV3(target);
		
		if(target==TARGET_FO.MAX)  lb.value=Double.NEGATIVE_INFINITY;    //per il max 
		if(target==TARGET_FO.MIN)  lb.value=Double.POSITIVE_INFINITY;    //per il min 
		
		if(type_solution_initial==SolutionType.OPTIMUM) {
			tree.addNode(milp_current);
		}	 
		
		while(!tree.isEmpty()) {
			
			milp_current=tree.getMilpBestUP();  
			if(milp_current.isSolutionIntegerAmmisible() && milp_current.isProblemSemiContinusAmmisible()) {
				milp_current.setIntegerIfOptimal();
							
				if(  (target==TARGET_FO.MAX && lb.value < milp_current.getOptimumValue())     //max 
				  || (target==TARGET_FO.MIN && lb.value > milp_current.getOptimumValue())) {  //questo vale per il min
					
					lb.value= milp_current.getOptimumValue();
					lb.milp=milp_current;
					type_solution=SolutionType.OPTIMUM; 
				}
			}
			else {
				listMangerMilpToRun = new ArrayList<>();
				MilpManager.populateArrayListBySeparation(listMangerMilpToRun,milp_current);
				
				for(MilpManager milp:listMangerMilpToRun) {
					milp.resolve();
					if(milp.getSolutionType()==SolutionType.OPTIMUM) {
						tree.addNode(milp);
					}
					num_simplex_resolved+=1;	
				}
				tree.deleteNodeWhitUPnotValide(lb.value); 
			}
			
			if(num_simplex_resolved >= num_max_simplex) { 
				logger.log(SscLevel.WARNING,RB.format("it.ssc.pl.milp.MILP.msg8")+num_max_simplex);
				logger.log(SscLevel.NOTE,RB.format("it.ssc.pl.milp.MILP.msg9"));
				return SolutionType.MAX_NUM_SIMPLEX;
			}
		}
		
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.MILP.msg2",RB.getHhMmSsMmm((end-start))));
		logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg3")+num_simplex_resolved);
		if(type_solution==SolutionType.OPTIMUM) logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg4"));
		return type_solution;
	}
	*/
	
	/**
	 * Executes the Branch and Bound algorithm.
	 * 
	 * @return The type of solution found
	 * @throws Exception If the execution process generates an error
	 */
	
	public SolutionType resolve() throws Exception {
		
		logger.log(SscLevel.INFO,RB.format("it.ssc.pl.milp.MILP.msg10")+threadNumber.getThread());
		
		int num_simplex_resolved=1;
		long start=System.currentTimeMillis();
		SolutionType type_solution=SolutionType.VUOTUM;
		//MilpManager milp_current=milp_initiale;        //commentato il 19/03/2024 DUPLICATO INUTILE
		milp_initiale.setMaxIteration(num_max_iteration);
		
		if(!milp_initiale.existVarToBeIntegerOrSemicon()) {
			throw new LPException(RB.format("it.ssc.pl.milp.MILP.msg12"));
		}
		
		SolutionType type_solution_initial=milp_initiale.resolve(); 
		
		//lo chiamo dopo resolve perche' il target lo recupera dal milp_zero, che e' valorizzato dopo il resolve
		TARGET_FO target=milp_initiale.getTargetFoOriginal();
		TreeV3 tree=new TreeV3(target);
		
		if(target==TARGET_FO.MAX)  lb.value=Double.NEGATIVE_INFINITY;    //per il max 
		if(target==TARGET_FO.MIN)  lb.value=Double.POSITIVE_INFINITY;    //per il min 
		//System.out.println("lb:"+milp_current.getOptimumValue());
		
		if(type_solution_initial==SolutionType.OPTIMUM) {
			if(milp_initiale.isSolutionIntegerAmmisible() && milp_initiale.isProblemSemiContinusAmmisible()) {
				//System.out.println("intera:"+milp.getOptimumValue());
				if(  (target==TARGET_FO.MAX && lb.value < milp_initiale.getOptimumValue())     //max 
				  || (target==TARGET_FO.MIN && lb.value > milp_initiale.getOptimumValue())) {  //questo vale per il min

					milp_initiale.setIntegerIfOptimal();
					lb.value= milp_initiale.getOptimumValue();
					lb.milp=milp_initiale;
					type_solution=SolutionType.OPTIMUM; 
					//se devo cercare solo una soluzione ammissibile , ma non ottima
					if(isJustTakeFeasibleSolution) {
						type_solution=SolutionType.FEASIBLE;
					}
				}
			}
			else tree.addNode(milp_initiale);
		}	 
		
		CyclicBarrier cb =null;
		Thread tgroup0 = null;
		
		ArrayList<MilpManager> list_best_candidate=null;
		ArrayList<MilpManager> scarti_to_separe=null;
		ArrayList<MilpManager> listMangerMilpToRun=null;
		
		b:	{
			while(!tree.isEmpty()) {

				list_best_candidate=tree.getMilpBestUP(threadNumber);  
				scarti_to_separe=new ArrayList<MilpManager> ();
				for(MilpManager lp_curent:list_best_candidate) {
					scarti_to_separe.add(lp_curent);
				}

				if(!scarti_to_separe.isEmpty()) {
					listMangerMilpToRun = new ArrayList<MilpManager>();

					//prende il milp lp_curent
					for(MilpManager lp_curent:scarti_to_separe) {
						MilpManager.populateArrayListBySeparation(listMangerMilpToRun,lp_curent);
					}
					//gestione a piu thread
					if(threadNumber!=MILPThreadsNumber.N_1) {

						//Gestione Thread
						cb = new CyclicBarrier(listMangerMilpToRun.size());
						for(MilpManager lp_run:listMangerMilpToRun) {
							//milp.resolve();
							(tgroup0 = new Thread(new Task(cb, lp_run ))).start();
						}
						tgroup0.join();
					}
					//gestione a singolo thread
					else {
						//System.out.println("un trhea");
						for(MilpManager milp:listMangerMilpToRun) {
							milp.resolve();
						}
					}
					//RISULTATO
					//dopo esecuzione si valuta risultato
					for(MilpManager milp:listMangerMilpToRun) {
						if(milp.getSolutionType()==SolutionType.OPTIMUM) {
							//System.out.println("VALUE:"+milp.getOptimumValue());
											
							if(milp.isSolutionIntegerAmmisible() && milp.isProblemSemiContinusAmmisible()) {
								//System.out.println("intera:"+milp.getOptimumValue());
								if(  (target==TARGET_FO.MAX && lb.value < milp.getOptimumValue())     //max 
								  || (target==TARGET_FO.MIN && lb.value > milp.getOptimumValue())) {  //questo vale per il min

									milp.setIntegerIfOptimal();
									lb.value= milp.getOptimumValue();
									lb.milp=milp;
									type_solution=SolutionType.OPTIMUM; 
									//se devo cercare solo una soluzione ammissibile , ma non ottima
									if(isJustTakeFeasibleSolution) {
										type_solution=SolutionType.FEASIBLE;
										break b;
									}
								}
							}
							else tree.addNode(milp);
						}
						num_simplex_resolved+=1;	
					}
					tree.deleteNodeWhitUPnotValide(lb.value); 
				}

				if(num_simplex_resolved >= num_max_simplex) { 
					logger.log(SscLevel.WARNING,RB.format("it.ssc.pl.milp.MILP.msg8")+num_max_simplex);
					logger.log(SscLevel.NOTE,RB.format("it.ssc.pl.milp.MILP.msg9"));
					return SolutionType.MAX_NUM_SIMPLEX;
				}
			}
		}
		long end=System.currentTimeMillis();
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.MILP.msg2",RB.getHhMmSsMmm((end-start))));
		logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg3")+num_simplex_resolved);
		if(type_solution==SolutionType.OPTIMUM) logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.MILP.msg4"));
		return type_solution;

	}
	
	
	/**
	 * This method returns the solution of the problem by removing the integer constraints (relaxed solution).
	 * If there are binary variables, only the constraint from assuming integer values is removed, but
	 * the binary variable still has the constraint of being between zero and one.
	 * 
	 * @return returns the relaxed solution, i.e., the solution of the problem without integer constraints.
	 */
	
	public Solution getRelaxedSolution()  {
		if(milp_initiale!=null) return milp_initiale.getSolution();
		else return null;
	}
	
	/**
	 * This method returns, if it exists, the optimal integer, mixed-integer, or binary solution.
	 * 
	 * @return the optimal integer, mixed-integer, or binary solution
	 * @throws SimplexException if the problem has not solution 
	 */
	
	public Solution getSolution()  throws SimplexException {
		if(lb.milp==null)  throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg10"));
		return lb.milp.getSolution();
		
	}
	
	/**
	 * If the problem has an optimal solution, this method returns that optimal solution in the form
	 * of an array with the values of the variables.
	 * 
	 * @return The optimal solution of the problem as an array of values
	 * @throws SimplexException If the optimal solution is not present
	 */
	
	public double[] getValuesSolution() throws SimplexException   {
		if(lb.milp==null)  throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg10"));
		return lb.milp.getSolution().getValuesSolution();
		
	}
	
	/**
	 * 
	 * @return the number of threads set for solving the problem
	 */
	
	public MILPThreadsNumber getThreadNumber() {
		return threadNumber;
	}
	
	/**
	 * This method allows setting the number of threads to use for executing the Branch and Bound.
	 * 
	 * @param lthreadNumber Enumeration for setting the number of Threads
	 * @return the MILP instance (this) on which the method call is being made
	 */
	public MILP setThreadsNumber(MILPThreadsNumber lthreadNumber) {
		threadNumber = lthreadNumber;
		return this;
	}
	@Deprecated
	public MILP setThreadNumber(MILPThreadsNumber lthreadNumber) {
		threadNumber = lthreadNumber;
		return this;
	}
	
	/**
	 * 
	 * @return true if the mode of returning a feasible solution instead of an optimal one is set
	 */


	public boolean isJustTakeFeasibleSolution() {
		return isJustTakeFeasibleSolution;
	}

	/**
	 * Setting it to true allows interrupting the Branch and Bound in order to determine
	 * not an optimal solution but only a feasible solution to the problem.
	 * @param isJustTakeFeasibleSolution true to interrupt the Branch and Bound and obtain only a feasible solution.
	 * @return the MILP instance (this) on which the method call is being made
	 */

	public MILP setJustTakeFeasibleSolution(boolean isJustTakeFeasibleSolution) {
		this.isJustTakeFeasibleSolution = isJustTakeFeasibleSolution;
		return this;
	}

	public  EPSILON getEpsilon() {
		return epsilons.epsilon;
	}

	public  EPSILON getIepsilon() {
		return epsilons.iepsilon;
	}

	public  EPSILON getCepsilon() {
		return epsilons.cepsilon;
	}
	
	

	/*
	 * Permette di impostare uno step (valore di tolleranza sacrificabile sul valore della 
	 * funzione obiettivo, per cui questo valore &egrave; da esprimere nelle stesse unit&agrave; di misura con cui si esprime la f.o.) 
	 * per accelerare la ricerca e minimizzare l'uso della memoria. Pi&ugrave; nel dettaglio se si individua una 
	 * soluzione intera (non ottima), di norma nell'operazione di potatura , vengono tagliati tutti i rami con un valore sulla f.o.  
	 * <= al valore z della soluzione trovata (se il problema &egrave; di max) , impostando questo valore vengono potati tutti 
	 * i rami con un valore <=  z + valore_step
	 * 
	 * 
	 * E' da esprimere sempre in valore assoluto 
	 * (non possono essere inseriti valori negativi). 
	 * @param tolerableDisadvantage
	 * @throws LPException 
	 */

	/*

	public void setStepDisadvantage(double stepDisadvantage) {
		if(stepDisadvantage<0) throw new LPException(RB.getString("it.ssc.pl.milp.MILP.msg11"));
		this.stepDisadvantage = stepDisadvantage;
	}
	
	
	public double getStepDisadvantage() {
		return stepDisadvantage;
	}
	
	*/
	
	/**
	 * Constructor of a MILP object for solving problems formulated in json 
	 * format.
	 * 
	 * @param pl_json JsonProblem object containing the problem in json format
	 * @throws Exception if the problem is not correctly formulated
	 */
	
	
	public MILP(JsonProblem pl_json) throws Exception  { 
		
		/*PArte nuova json*/
		BufferedReader br=null;
		ArrayList<String> listVar=null;
		LinearObjectiveFunction fo;
		ScanConstraintFromJson scanCons=null;
		try {
			br= pl_json.getBufferedReader();
			ScanSintaxJson scanJson=new ScanSintaxJson(br);
		    br.close();br=null;
		    listVar=scanJson.getListNomiVar();
			fo=scanJson.getFo();
			br= pl_json.getBufferedReader();
			//for(String namev:list_var) System.out.println("name_ord :"+namev);
			scanCons=new ScanConstraintFromJson(br,listVar);
		}
		finally {
			if (br != null ) br.close();
		}
		
		/*PArte vecchia*/
		this.milp_initiale = new MilpManager(fo, scanCons.getConstraints(),this,listVar);
		setAllEpsilon();
	}
}
