

package org.ssclab.pl.milp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ssclab.context.Context;
import org.ssclab.context.Session;
import org.ssclab.datasource.DataSource;
import org.ssclab.i18n.RB;
import org.ssclab.log.SscLevel;
import org.ssclab.log.SscLogger;
import org.ssclab.pl.milp.util.A_DataMatrix;
import org.ssclab.pl.milp.util.A_Matrix;
import org.ssclab.pl.milp.util.LPThreadsNumber;
import org.ssclab.ref.Input;
import org.ssclab.pl.milp.util.VectorsPL;
import org.ssclab.pl.milp.scanjson.ScanConstraintFromJson;
import org.ssclab.pl.milp.scanjson.ScanSintaxJson;
import org.ssclab.pl.milp.scantext.CheckSintaxText;
import org.ssclab.pl.milp.scantext.ScanConstraintFromLine;
import org.ssclab.pl.milp.scantext.ScanFoFromLine;
import org.ssclab.pl.milp.scantext.ScanVarFromText;
import org.ssclab.pl.milp.simplex.Simplex;
import org.ssclab.pl.milp.simplex.SimplexInterface;
import org.ssclab.pl.milp.simplex.SimplexException;
import org.ssclab.pl.milp.FormatTypeInput.FormatType;
import org.ssclab.pl.milp.ObjectiveFunction.TARGET_FO;



/**
 * This class allows executing and solving formulations of linear programming problems.
 * The method used for solving such optimization problems is the simplex method.
 * 
 * @author Stefano Scarioli
 * @version 4.0
 * @see <a target="_new" href="http://www.ssclab.org">SSC Software www.sscLab.org</a>
 */

public final class LP /*implements FormatTypeInput*/ {
	
	public static double NaN=Double.NaN;
	private static final Logger logger=SscLogger.getLogger();
	private SolutionImpl solution_pl;
	private int num_max_iteration=100_000_000;
	private VectorsPL vectors_pl;
	private Session session;
	private final boolean isMilp=false;
	private boolean isParallelSimplex=false;
	private boolean toCloseSessionInternal=true;
	private A_DataMatrix amatrix;
	private PersistensePLProblem persistencePl;
	private LPThreadsNumber threadsNumber=LPThreadsNumber.N_1;
	private boolean isStopPhase2=false;
	private Epsilons epsilons=new Epsilons();
	private SolutionType type_solution;
	private TARGET_FO target;
	private Meta meta = new Meta();
	private String title;
	
	/*
	private EPSILON epsilon=EPSILON._1E_M10;
	private EPSILON cepsilon=EPSILON._1E_M8;
	*/
	
	static {
		logger.log(Level.INFO,  " ");
		logger.log(Level.INFO,  "##############################################");
		logger.log(Level.INFO,  RB.getString("it.ssc.context.Session_Impl.msg0"));
		logger.log(Level.INFO,  "##############################################");
		logger.log(Level.INFO,  " ");
	}
	
	
	
	
	/**
	* Constructor for use with the problem statement in text format stored in an String.
	* 
	*@param pl_text The text where the file containing the LP problem formulated with the text 
	*format is located
	*@throws Exception An exception is thrown if the problem is not correctly formulated or 
	*if the file does not exist
	*/
	public LP(String pl_text) throws Exception  { 
		BufferedReader br=null;
		ScanConstraintFromLine scan_const=null;
		LinearObjectiveFunction fo=null;
		ArrayList<String> list_var=null;
		this.session=Context.createNewSession();
		try {
			//System.out.println("Da file");
			//File file =new File(path);
			br= new BufferedReader(new StringReader(pl_text));
			String line_fo=new CheckSintaxText(br).getLineFO();
			br.close(); br=null;
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
			if (br != null) br.close();
		}
		
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		PLProblem pl_original=CreatePLProblem.create(fo,list_constraints,list_var,scan_const.getArraysProblem(),isMilp);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		//createStandartProblem(pl_original);
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Constructor for use with the problem statement in text format stored in an ArrayList.
	*
	* @param text An ArrayList (of String objects) containing the problem formulation in the 
	* form of text
	* @throws Exception An exception is thrown if the problem is not correctly formulated
	*/
	public LP(ArrayList<String> text) throws Exception  { 
		if(text==null || text.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		this.session=Context.createNewSession();
		//verifica che la sintassi sia giusta del formato testo
		//e ritorna la funzione obiettivo come stringa
		String line_fo=new CheckSintaxText(text).getLineFO();
		
		ArrayList<String> list_var=new ScanVarFromText(text).getListNomiVar();
		//for(String namev:list_var) System.out.println("name_ord :"+namev);
		ScanFoFromLine fo_from_string=new ScanFoFromLine(line_fo,list_var);
		LinearObjectiveFunction fo=fo_from_string.getFOFunction();
		ScanConstraintFromLine scan_const=new ScanConstraintFromLine(text,list_var);
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		PLProblem pl_original=CreatePLProblem.create(fo,list_constraints,list_var,scan_const.getArraysProblem(),isMilp);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		//createStandartProblem(pl_original); 
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	
	
	
	/**
	 * Constructor for use with the problem statement in text format stored in
	 *  an external file.
	* 
	*@param path The path where the file containing the LP problem formulated with the text 
	*format is located
	*@throws Exception An exception is thrown if the problem is not correctly formulated or 
	*if the file does not exist
	*/
	public LP(Path path) throws Exception  { 
		BufferedReader br=null;
		ScanConstraintFromLine scan_const=null;
		LinearObjectiveFunction fo=null;
		ArrayList<String> list_var=null;
		this.session=Context.createNewSession();
		try {
			//System.out.println("Da file");
			//File file =new File(path);
			br=Files.newBufferedReader(path);
			String line_fo=new CheckSintaxText(br).getLineFO();
			br.close(); br=null;
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
			if (br != null) br.close();
		}
		
		ArrayList<InternalConstraint> list_constraints=scan_const.getConstraints();
		PLProblem pl_original=CreatePLProblem.create(fo,list_constraints,list_var,scan_const.getArraysProblem(),isMilp);
		
		this.target=pl_original.getTarget_fo();
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		//createStandartProblem(pl_original);
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	
	
	
	
	
	/*
	 * Costruttore di un oggetto LP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. Il terzo parametro 
	 * &egrave; la lista dei vincoli che non sono di tipo EQ,LE,GE, ma UPPER e LOWER e vanno rappresentati come oggetti Constraint. 
	 * 
	 * @param inequality Lista dei vincoli di tipo EQ,GE,LE sotto forma di disequazioni contenute in stringhe
	 * @param constraints Lista dei vincoli di tipo UPPER e LOWER rappresentati come oggetti Constraint
	 * @param fo  Un oggetto LinearObjectiveFunction che rappresenta la funzione obiettivo
	 * @throws Exception  Viene generata una eccezione se il problema non &egrave; formulato correttamente
	 */
	
	/*
	public LP(ArrayList<String> inequality,ArrayList<Constraint> constraints,LinearObjectiveFunction fo) throws Exception  { 
		
		//qua sarebbe da implementare qualche controllino in piu'.
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		if(constraints==null ) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg13"));
		this.session=Context.createNewSession();
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality,constraints);
		ArrayList<Constraint> new_constraints=cfs.getConstraint();
		PLProblem pl_original=CreatePLProblem.create(fo,new_constraints,isMilp);
		createStandartProblem(pl_original);
	}
	*/
	
	
	/*
	 * Costruttore di un oggetto LP per la risoluzione di problemi formulati in formato a disequazioni contenute in stringhe. 
	 * In questo formato le variabili devono necessariamente chiamarsi X<sub>j</sub>, con l'indice j che parte da 1. Il terzo parametro 
	 * &egrave; la lista dei vincoli che non sono di tipo EQ,LE,GE, ma UPPER e LOWER e vanno rappresentati come oggetti Constraint. 
	 *  
	 * @param inequality
	 * @param constraints
	 * @param fo
	 * @throws Exception
	 */
	
	
	/*
	public LP(ArrayList<String> inequality,ListConstraints constraints,LinearObjectiveFunction fo) throws Exception  { 
		//qua sarebbe da implementare qualche controllino in piu'.
		if(inequality==null || inequality.isEmpty()) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg12"));
		this.session=Context.createNewSession();
		int dimension=fo.getC().length;
		ConstraintFromString cfs=new ConstraintFromString(dimension, inequality,constraints.getListConstraint());
		ArrayList<Constraint> new_constraints=cfs.getConstraint(); 
		PLProblem pl_original=CreatePLProblem.create(fo,new_constraints,isMilp);
		createStandartProblem(pl_original);
	}
	*/
	
	
	
	/**
	 * Constructor
	 * 
	 * Creates an LP object for solving problems expressed in matrix format.
	 * 
	 * @param fo A LinearObjectiveFunction object representing the objective function
	 * @param constraints The list of constraints expressed as an ArrayList of Constraint objects
	 * @throws Exception An exception is thrown if the problem is not correctly formulated
	 * 
	 */

	public LP(LinearObjectiveFunction fo,ArrayList<Constraint> constraints) throws Exception { 
		if(constraints==null ) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg13"));
		this.session=Context.createNewSession();
		PLProblem pl_original=CreatePLProblem.create(fo,constraints,isMilp,null);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		//createStandartProblem(pl_original);
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	
	
		
	/**
	*Constructor of an LP object for solving problems expressed in matrix format.
	*@param fo A LinearObjectiveFunction object representing the objective function
	*@param constraints The list of constraints as a ListConstraints object
	*@throws Exception An exception is thrown if the problem is not correctly formulated
	*/
	
	public LP(LinearObjectiveFunction fo,ListConstraints constraints) throws Exception  { 
		this.session=Context.createNewSession();
		PLProblem pl_original=CreatePLProblem.create(fo,constraints.getListConstraint(),isMilp,null);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		//createStandartProblem(pl_original);
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	
	
	
	

	/**
	*Constructor of an LP object for solving problems expressed in sparse format or coefficient format.
	*@param input The problem formulated in sparse format or coefficient format 
	*@param session An SSC working session
	*@param format Constant to express in which format the problem is formulated (FormatType.SPARSE or FormatType.COEFF)
	*@throws Exception An exception is thrown if the problem is not formulated correctly
	*/
	
	public LP(Input input,Session session, FormatType format) throws Exception {
		this.session=session;
		this.toCloseSessionInternal=false;
		DataSource milp_data_source=session.createDataSource(input);
		PLProblem pl_original=null;
		if(format==FormatType.SPARSE) pl_original=CreatePLProblem.createFromSparse(milp_data_source,isMilp);
		else if(format==FormatType.COEFF) pl_original=CreatePLProblem.create(milp_data_source, isMilp);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		
		//createStandartProblem(pl_original);
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	
	
	
	/**
	*Constructor of an LP object for solving problems expressed in either sparse or coefficient format.
	*@param input The problem formulated in sparse format
	*@param format Constant to express in which format the problem is formulated (FormatType.SPARSE or FormatType.COEFF)
	*@throws Exception An exception is thrown if the problem is not formulated correctly
	*/
	
	public LP(Input input,FormatType format) throws  Exception {
		
		this(input, Context.createNewSession(),format);
		this.toCloseSessionInternal=true;
	}
	
	/**
	*
	*Constructor of an LP object for solving problems expressed in coefficient format.
	*@param input The problem formulated in coefficient format
	*@throws Exception An exception is thrown if the problem is not formulated correctly
	*/

	public LP(Input input) throws Exception {
		
		this(input, Context.createNewSession());
		this.toCloseSessionInternal=true;
		//logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.LP.msg1")); 
		//session.close();
	}
	

	/**
	*
	*Constructor of an LP object for solving problems expressed in coefficient format.
	*@param input The problem formulated in coefficient format
	*@param session An SSC working session
	*@throws Exception An exception is thrown if the problem is not formulated correctly
	*/
	
	public LP(Input input,Session session) throws Exception {
		
		this.session=session;
		this.toCloseSessionInternal=false;
		DataSource milp_data_source=session.createDataSource(input);
		/*Crea im problema puro , cosi come dichiarato dall'utente A <=> b , f=C*/
		PLProblem pl_original=CreatePLProblem.create(milp_data_source, isMilp);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		persistencePl=new PersistensePLProblem(pl_original,path_work);
		//createStandartProblem(pl_original); 

		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	
		/*
		printTableAm(Amatrix);
		System.out.println("--------A");
		printTableA(vectors_pl.A);
		System.out.println("--------B");
		printTableV(vectors_pl.B);
		System.out.println("--------C");
		printTableV(vectors_pl.C);
		*/
	}
	
	/**
	*This method allows setting the epsilon value relative to the tolerance that intervenes in various contexts.
	*It is used in the following cases: <br>
	*During phase one, both in determining the entering variable and in determining the exiting variable with or without the Bland rule;
	*it is also used to determine if the base is degenerate. It is also used at the end of phase one: if there is an auxiliary variable in the base,
	*epsilon is used to determine if it is possible to eliminate the rows and columns of these on the extended table. <br>
	*During phase two, both in determining the entering variable and in determining the exiting variable with or without the Bland rule;
	*it is also used to determine if the base is degenerate.
	*@return the LP instance (this) on which the method call is being made
	*@param epsilon Tolerance used in various phases of the simplex. Default value 1E-10
	*/

	
	public LP setEpsilon(EPSILON epsilon)   {  
		this.epsilons.epsilon=epsilon;
		return this;
	}
	

	
	/**
	*
	*This method allows setting the epsilon value relative to the tolerance in determining if an optimal solution expressed by phase 1
	*is close to or equal to zero and thus gives rise to feasible solutions for the initial problem.
	*@return the LP instance (this) on which the method call is being made
	*@param epsilon Tolerance of phase 1 solution with respect to zero. Default value 1E-8
	*/
	
	public LP setCEpsilon(EPSILON epsilon)  { 
		this.epsilons.cepsilon=epsilon;
		return this;
	}
	
	
	public EPSILON getEpsilon() {
		return this.epsilons.epsilon;
	}



	public EPSILON getCEpsilon() {
		return this.epsilons.cepsilon;
	}


	/**
	*
	*This method allows limiting the maximum number of simplex iterations (phase 1 iterations + phase 2 iterations)
	*@param num_max_iteration The maximum number of iterations to be executed. Default value 100,000,000.
	*@return the LP instance (this) on which the method call is being made
	*@throws LPException If an incorrect number (zero or negative) is set
	*/
	public LP setNumMaxIteration(int num_max_iteration) throws LPException  { 
		if(num_max_iteration <= 0) throw new LPException("Il numero massimo di iterazioni deve essere un numero positivo");
		this.num_max_iteration=num_max_iteration;
		return this;
	}
	
	/**
	*
	*This method returns the maximum number of simplex iterations
	*@return The maximum number of iterations
	*/
	
	public int getNumMaxIteration()   { 
		return this.num_max_iteration;
	}
	
	/*
	private void createStandartProblem(PLProblem pl_original) throws InvalidSessionException, Exception {
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		vectors_pl=pl_original.standardize(); 
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}
	*/
	
	
	/**
	*
	*Executes the simplex (phase 1 + phase 2).
	*@return The type of solution found
	*@throws Exception If the execution process generates an error
	*/
	public SolutionType resolve() throws Exception {
		
		if(title!=null) logger.log(SscLevel.INFO,RB.format("it.ssc.pl.milp.MILP.msg13")+" \""+title+"\"");
		meta.put("threads", threadsNumber.getNumberThread());
		logger.log(SscLevel.INFO,RB.format("it.ssc.pl.milp.LP.msg11")+threadsNumber.getNumberThread());
		logger.log(Level.INFO,  "---------------------------------------------");
		
		//l'oggetto simplex crea la tabella estesa per fase I da A e poi la svuoto subito dopo 
		//aver creato la tabella
		SimplexInterface simplex =new Simplex(vectors_pl.A, vectors_pl.B, vectors_pl.C,epsilons);
		simplex.setNumIterationMax(num_max_iteration);
		simplex.setThreadsNumber(threadsNumber) ;
		
		long start_simplex=System.currentTimeMillis();
		type_solution=simplex.runPhaseOne();
		long end_phase_one=System.currentTimeMillis();
		long end_phase_two=end_phase_one;
		
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.LP.msg2", RB.getHhMmSsMmm((end_phase_one-start_simplex))));
		logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.LP.msg3")+simplex.getNumIterationPhaseOne());  
		//iterazioni una prima volta, e poi dopo aggiorno se fase due 
		meta.put("iterationsLP",simplex.getNumIterationPhaseOne());
		
		if(isStopPhase2 && type_solution==SolutionType.OPTIMUM) {
			type_solution=SolutionType.FEASIBLE;
			PLProblem pl_original=persistencePl.readObject();
			this.solution_pl=new SolutionImpl(type_solution,
											  pl_original,  //PRIMA PASSAVO UN CLONE ??? tolto .clone()
											  simplex.getFinalBasis(),
											  simplex.getFinalValuesBasis()
											 );
			
		}
		
		
		else if(type_solution==SolutionType.OPTIMUM) {
			type_solution =simplex.runPhaseTwo();
			end_phase_two=System.currentTimeMillis(); 
			logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.LP.msg4",RB.getHhMmSsMmm(end_phase_two-end_phase_one)));
			logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.LP.msg5")+simplex.getNumIterationPhaseTotal());
			meta.put("iterationsLP",simplex.getNumIterationPhaseTotal());
			
			PLProblem pl_original=persistencePl.readObject();
			this.solution_pl=new SolutionImpl(type_solution,
											  pl_original,  //PRIMA PASSAVO UN CLONE ??? tolto .clone()
											  simplex.getFinalBasis(),
											  simplex.getFinalValuesBasis()
											 );
			
		}	
	
		meta.put("optimizationDuration",RB.getHhMmSsMmm(end_phase_two-start_simplex));
		
		logger.log(SscLevel.TIME,RB.format("it.ssc.pl.milp.LP.msg6",RB.getHhMmSsMmm(end_phase_two-start_simplex)));
		if(type_solution==SolutionType.FEASIBLE || type_solution==SolutionType.OPTIMUM) {
			loggerAccurancy( amatrix, vectors_pl.B, simplex.getFinalBasis(),simplex.getFinalValuesBasis(),isStopPhase2);
		}
		closeAfterResolve() ;
		return type_solution;

	}
	
	
	
	/**
	 * This method returns the matrix A obtained after the process of reduction to
     * standard form  (max z , Ax + s=b, x &ge; 0, b &ge; 0) of the original linear programming problem.
	 * 
	 * @return The coefficient matrix A
     * @throws SimplexException If null matrix
     * @throws IOException if the problem has not been reduced to standard form
	 */
	public double[][] getStandartMatrixA() throws SimplexException, IOException {
		if(amatrix==null) throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg9"));
		return amatrix.getMatrix();
	}

	/**
	 *This method returns the vector b of the rhs values obtained after the process of reduction to
     * standard form(max z , Ax+s=b, x &ge; 0, b &ge; 0)  of the original linear programming problem.
     * 
     * @return The vector of RHS coefficients
	 */
	
	public double[] getStandartVectorB() {
		return vectors_pl.B.clone();
	}

	/**
	 * This method returns the vector c of the coefficients of the objective function after the process of reduction
     * to standard form (max z , Ax+s=b, x &ge; 0, b &ge; 0) of the original linear programming problem.
	 * 
	 * @return The vector c of the coefficients of the objective function.
	 */
	public double[] getStandartVectorC() {
		return vectors_pl.C.clone();
	}

	
	
	/**
	*
	*If the problem has an optimal solution, this method returns that optimal solution in the form of an object of the
	*Solution class.
	*@return The optimal solution of the problem
	*@throws SimplexException If the optimal solution is not present
	*/
	public Solution getSolution() throws SimplexException  {
		if(this.solution_pl==null)  throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg10"));
		return this.solution_pl.setFeasibleSolution(this.isStopPhase2);
	}
	
	
	
	/**
	*
	*If the problem has an optimal solution, this method returns that optimal solution in the form of
	*an array with the values of the variables.
	*@return The optimal solution of the problem as an array of double values
	*@throws SimplexException If the optimal solution is not present
	*/
	public double[] getValuesSolution() throws SimplexException  {
		if(this.solution_pl==null)  throw new SimplexException(RB.getString("it.ssc.pl.milp.LP.msg10"));
		return this.solution_pl.getValuesSolution();
	}
	
	
	
	/**
	* @return true if the parallelization with multiple threads of the simplex is active.
	*/
	public boolean isParallelSimplex() {
		return isParallelSimplex;
	}
	
	
	/**
	*
	*If the number of physical cores of the host on which SSc is running is 
	*greater than 4 and and if the number of variables constraints and hundreds, 
	*the performance of the simplex can be improved by executing 
	*the optimization processes in parallel on multiple threads. 
	*The number of threads is set to set value is AUTO, the system decides 
	*the number of threads to use.
	*@return the LP instance on which the method call is being made
	*@param isParallelSimplex True to activate parallelization
	*/
	public LP setParallelSimplex(boolean isParallelSimplex) {
		this.isParallelSimplex = isParallelSimplex;
		if(isParallelSimplex==true) threadsNumber=LPThreadsNumber.AUTO;
		return this;
	}
	
		
	
	/**
	*
	* @return the number of threads used in the execution. If the value is AUTO, the system decides the number of threads to use.
	*/
	public LPThreadsNumber getThreadsNumber() {
		return threadsNumber;
	}
	
	
	
	/**
	* If the set value is AUTO, the system decides the number of
	threads to use.
	* @param threadsNumber Sets the number of threads to use in the execution.
	*@return the LP instance (this) on which the method call is being made
	*/
	public LP setThreadsNumber(LPThreadsNumber threadsNumber) {
		isParallelSimplex=true;
		this.threadsNumber = threadsNumber;
		return this;
	}
	


	/**
	*
	*Returns true if only phase 1 execution is set to obtain a feasible solution.
	*@return true if active
	*/
	public boolean isJustTakeFeasibleSolution() {
		return isStopPhase2;
	}

	/**
	*
	* Setting to true allows interrupting the simplex at the end of phase 1, in order 
	* to determine not an optimal solution but only a feasible solution of the problem.
	* @param isStopPhase2 true to interrupt the simplex before phase 2.
	*@return the LP instance (this) on which the method call is being made
	*/
	public LP setJustTakeFeasibleSolution(boolean isStopPhase2) {
		this.isStopPhase2 = isStopPhase2;
		return this;
	}

	
	private void loggerAccurancy(A_DataMatrix matrix, double[] B,int basis[],double values[],boolean solo_ammissibile) throws IOException {
		double sum_b=0;
		double best_error=0;
		double[] array;
		int nCols=matrix.getnCol();
		double array_solutions[]=getArraySolution(nCols, basis,values);
		for(int i=0;i<matrix.getnRow();i++) {
			double b_=0;
			array=matrix.readArray(i);
			for(int j=0;j<nCols;j++) {
				b_=b_+ array[j]* array_solutions[j];
			}
			if(best_error < Math.abs(b_- B[i])) best_error=Math.abs(b_- B[i]);
			sum_b+=Math.abs(b_- B[i]);
		}
		double errore=sum_b/matrix.getnRow();
		if(solo_ammissibile) logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.LP.msg7bis"));
		else logger.log(Level.INFO,RB.getString("it.ssc.pl.milp.LP.msg7"));
		logger.log(Level.INFO,  "---------------------------------------------");
		if(solo_ammissibile) logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8bis"));
		else logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8"));
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8b"));
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8c"));
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8d")+errore);
		logger.log(Level.INFO,  RB.getString("it.ssc.pl.milp.LP.msg8e")+best_error);
		logger.log(Level.INFO,  "---------------------------------------------");
		
		meta.put("averageError", errore);
		meta.put("maxError", best_error);
		
		
		
	}
	
	private double[]  getArraySolution( int dim, int basis[],double value_bases[]) {
		double[] solutions=new double[dim];
		for (int index_var=0;index_var<dim;index_var++) 
			for (int i = 0; i < basis.length; i++) {
				if(index_var== basis[i]) {
					solutions[index_var]= value_bases[i];
				}
			}
		return solutions;
	}
	
	
	private void closeAfterResolve() throws Exception {
		if(toCloseSessionInternal) session.close();
		amatrix.close();
		amatrix=null;
		session=null;
	}
	
	@SuppressWarnings("unused")
	private void printTableAm(A_Matrix tabella) throws IOException {
		for(int _i=0;_i<tabella.getnRow();_i++) {
			System.out.println("");
			for(int _j=0;_j<tabella.getnCol();_j++) {
				double val=tabella.readArray(_i)[_j];
				System.out.printf("\t : %7.14f",val);
			}
		}
		System.out.println("");
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
	
	
	/**
	 * Constructor of a LP object for solving problems formulated in json 
	 * format.
	 * 
	 * @param pl_json JsonProblem object containing the problem in json format
	 * @throws Exception if the problem is not correctly formulated
	 */
	
	
	
	public LP(JsonProblem pl_json) throws Exception { 
	
		/*PArte nuova json*/
		BufferedReader br=null;
		ArrayList<String> list_var=null;
		LinearObjectiveFunction fo;
		ScanConstraintFromJson scanCons=null;
		try {
			br= pl_json.getBufferedReader();
			ScanSintaxJson scanJson=new ScanSintaxJson(br);
		    br.close();br=null;
			list_var=scanJson.getListNomiVar();
			fo=scanJson.getFo();
			br= pl_json.getBufferedReader();
			//for(String namev:list_var) System.out.println("name_ord :"+namev);
			scanCons=new ScanConstraintFromJson(br,list_var);
			
		}
		finally {
			if (br != null ) br.close();
		}
		ArrayList<Constraint> constraints=scanCons.getConstraints();
		
		/*PArte vecchia */
	
		if(constraints==null ) throw new LPException(RB.getString("it.ssc.pl.milp.LP.msg13"));
		this.session=Context.createNewSession();
		PLProblem pl_original=CreatePLProblem.create(fo,constraints,isMilp,list_var);
		
		//memorizza nella work il pl_original come oggetto prima di essere standardizzato. 
		//pl original , non e' memorizzato in LP , una volta terminato questo metodo, 
		//ogni riferimento e' perso. 
		//Questo oggetto non contiene i vincoli aggiuntivi degli upper/lower e le slacks, 
		//ne nessuna standardizzazione  
		persistencePl=new PersistensePLProblem(pl_original,session.getFactoryLibraries().getLibraryWork().getAbsolutePath());
		
		//createStandartProblem(pl_original);
		String path_work=session.getFactoryLibraries().getLibraryWork().getAbsolutePath();
		
		/*
		 * Nella fase di standardizzazione : 
		 * 
		 * a) Cambio segno alla funzione obiettivo se essa e MIN - > MAX e Cj = -Cj
		 * b) Essettuo traslazione del vincolo esistente  aggiornando bi, se esiste 
		 *    una o piu' variabili con lower != 0 o da -inf.
		 * c) Aggiungo nuovo vincolo nel caso esista un lower (Xj <= upper - appo_lower )  
		 * d) Rende tutti i termini noti b positivi , cambiando segno a tutta la riga
		 * 
		 * e) Calcola il nuovo valore new_dimension che sara' poi la dimensione delle colonne di A 
		 *    (la nuova matrice standard)
		 * f) Crea la nuova matrice A aggiungendo anche le variabili libere (x=y-z) e le slacks, 
		 *    e i vettori C e B
		 */
		
		vectors_pl=pl_original.standardize(); 
		
		//memorizza su disco la matrice A
		amatrix=new A_DataMatrix(vectors_pl.A,path_work);
	}	
	
	/**
	 * Allows you to give a title to the current elaboration related to the LP problem to be solved
	 * 
	 * @param title The title of the linear programming problem 
	 * @return This instance of LP
	 */
	
	public LP setTitle(String title) {
		this.title=title;
		return this;
	}
	
	/**
	 * Returns a {@link JsonSolution} object that represents the solution of the Linear Programming (LP) or 
	 * Mixed-Integer Linear Programming (MILP) problem in JSON format.
	 * 
	 * This method constructs the solution JSON, including optional sections based on the 
	 * {@link SolutionDetail} values provided as arguments. The solution can represent either 
	 * the optimal or feasible solution, depending on the problem's outcome.
	 * 
	 * The 'SolutionDetail' enum values can specify additional information to include in the JSON output:
	 * <ul>
	 *   <li>{@code INCLUDE_BOUNDS}: Includes the variable bounds (upper and lower limits) in the JSON.</li>
	 *   <li>{@code INCLUDE_CONSTRAINT}: Includes the Left-Hand Side (LHS) and Right-Hand Side (RHS) values for constraints in the JSON.</li>
	 *   <li>{@code INCLUDE_META}: Inserts metadata information like runtime, threads, iterations, etc.</li>
	 *   <li>{@code INCLUDE_TYPEVAR}: Shows the original type of each variable (e.g., integer, binary, continuous) in the JSON.</li>
	 * </ul>
	 * 
	 * @param option A variable number of {@link SolutionDetail} enum values to customize the JSON output.
	 * @return A {@link JsonSolution} object representing the problem's solution in JSON format.
	 * @throws SimplexException If there is an error in the Simplex execution or problem-solving process.
	 */
	
	public JsonSolution getSolutionAsJson(SolutionDetail... option) throws SimplexException {
		Solution[] solution= {null};
		meta.put("title", title);
		if (this.type_solution == SolutionType.OPTIMAL || this.type_solution == SolutionType.FEASIBLE) {
			 solution= new Solution[] {this.getSolution()};
		}
		return new JsonSolution(this.meta,this.target,type_solution,solution,option);
	}
	
	
	/**
	 * Solves the linear programming (LP) problem and returns the current instance of the LP object.
	 * This method is designed to allow method chaining by returning 'this' rather than a result type.
	 * 
	 * <p>By passing 'null' to this method, the standard resolve method is bypassed, and this
	 * version of the method is invoked. The 'nullable' parameter exists to differentiate 
	 * between the standard resolve method and this version, which enables method chaining.</p>
	 * 
	 * <p>For example, this method can be used as follows:</p>
	 * 
	 * <pre>{@code
	 * new LP(pl_string).resolve(null).getSolutionAsJson().saveToFile("solution.json");
	 * }</pre>
	 * 
	 * <p>In the above code, the 'resolve()' method solves the LP problem, and 'getSolutionAsJson()' 
	 * is chained to save the resulting solution to a JSON file.</p>
	 * 
	 * @param nullable an object that is intentionally ignored; passing 'null' to this parameter
	 *                 ensures that this version of the resolve method is invoked.
	 * @return the current instance of the LP object for method chaining.
	 * @throws Exception if an error occurs during the resolution process.
	 */
    public LP resolve(Object nullable) throws Exception {
        // Logica per risolvere il problema
        	this.resolve();
        	return this;
    }
}

