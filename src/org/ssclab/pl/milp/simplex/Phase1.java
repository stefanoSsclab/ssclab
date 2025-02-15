package org.ssclab.pl.milp.simplex;



import java.util.ArrayList;
import java.util.logging.Logger;

import org.ssclab.i18n.RB;
import org.ssclab.log.SscLevel;
import org.ssclab.log.SscLogger;
import org.ssclab.pl.milp.util.LPThreadsNumber;
import org.ssclab.util.Tuple2;
import org.ssclab.vector_spaces.Matrix;
import org.ssclab.vector_spaces.MatrixException;
import org.ssclab.vector_spaces.Vector;
import org.ssclab.pl.milp.EPSILON;
import org.ssclab.pl.milp.SolutionType;


final class Phase1 extends Phase {
	
	private static final Logger logger=SscLogger.getLogger();
	
	private final int n;
	private final int m;
	private  int n_aux;
	private boolean isMilp=false;
	
	
	public Phase1(Matrix A, Vector B,EPSILON epsilon,EPSILON cepsilon) throws SimplexException, MatrixException {
		
		super(epsilon, cepsilon);
		this.m=A.getNrow();
		this.n=A.getNcolumn();
		if(m!=B.lenght()) {
			throw new SimplexException("Il numero di righe di A (matrice dei coefficienti) non si adatta al numero di componenti del vettore B dei termini noti");
		}
		Tuple2<Integer,double[][]> ctr_phase1=createTablePhase1(A.getMatrix(),B.getVector());
		this.n_aux=ctr_phase1._0;
		this.TBEX=ctr_phase1._1;
		this._N=this.n + this.n_aux; //numero ausiliarie n_aux + n
		this._M=this.m;
	}
	
	public void setMilp(boolean isMilp) {
		this.isMilp = isMilp;
	}

	
	@SuppressWarnings("unchecked")
	private Tuple2<Integer,double[][]> createTablePhase1(double[][] A_,double[] B_) throws MatrixException {
		
		basis = new int[m];
		//questo vettore dovra' determinare se nella riga iesima esiste gia una variabile 
		//che puo' essere utilizzata per far diventare il sistema in forma canonica ( Aij =1)
		//se non le trova, vengono create con valore tupla a false
		Tuple2<Integer,Boolean>[] var_canonical=new Tuple2[m];
		
		//vedo se in ogni riga esiste una variabile con un Aij =1  
		double sum_Aij;
		for (int row = 0; row < m ; row++) {  //spazzolo le righe 
			for (int j = 0; j < n ; j++) {    //su una righa vedo se sulle diverse colonne ci sono aij=1
				if(A_[row][j] == 1.0) {       //se si , controllo che gli altri valori sulla colonna siano a zero 
					sum_Aij=0;
					for (int k = 0; k < m ; k++) { 
						sum_Aij+=Math.abs(A_[k][j]);
					}
					if(sum_Aij==1.0) { 
						var_canonical[row]=new Tuple2<Integer,Boolean>(j,true);
						//si potrebbe metttere un continue !!!
						//continue;
					}
				}
			}
		}
		
		//calcolo il numero variabili ausiliarie da introdurre
		n_aux=0;
		//determino la dove occorre inserire una variabile ausiliaria
		for (int row = 0; row < m ; row++) { 
			 if(var_canonical[row]==null)  { 
				 var_canonical[row]=new Tuple2<Integer,Boolean>(n + n_aux,false); 
				 n_aux++;
			 }
		}
		
		double[] C_=calcNewCoefficienti(A_,var_canonical);
		double z_init= calcNewValueZ(B_,var_canonical);
			
		//Matrix table = new Matrix(m+1,n+n_aux+1);
		double[][] table_exended_loc=new double[m+1][];
		
		//La matrice originale viene annullata per creare quella estesa
		for (int i = 0; i < m ; i++) {
			table_exended_loc[i]=new double[n+n_aux+1];
			for (int j = 0; j < n ; j++) {
				table_exended_loc[i][j]=A_[i][j];
			}
			A_[i]=null;
		}  
		A_=null;
		
		
		//VARIABILI SLACKS - B - BASE 
		for (int i = 0; i < m ; i++) {
			for (int j = n; j < n+ n_aux ; j++) {
				if(var_canonical[i]._0==j && !var_canonical[i]._1) table_exended_loc[i][j]=1.0;
			}
			table_exended_loc[i][n_aux + n]=  B_[i]; //new 
			setBases(i,var_canonical[i]._0); 
		}
		
		table_exended_loc[m]=new double[n+n_aux+1];
		
		for (int j = 0; j < n ; j++) {
			table_exended_loc[m][j]=C_[j];
		}
		
		table_exended_loc[m][n_aux+n]=z_init; 
		return new Tuple2<Integer,double[][]>(n_aux,table_exended_loc);
	}
	
	private double calcNewValueZ(double[]  B_,Tuple2<Integer,Boolean>[] var_canonical) {
		double init_z=0.0;
		for (int i = 0; i < m ; i++) {
			if(!var_canonical[i]._1) init_z=init_z + B_[i];
		}
		return init_z;
	}
	
	
	private double[] calcNewCoefficienti(double[][] A_,Tuple2<Integer,Boolean>[] var_canonical) {
		double[] C2= new double[n] ;
		for (int i = 0; i < m ; i++) {
			if(!var_canonical[i]._1) {
				for (int j = 0; j < n ; j++) {
					C2[j]=C2[j]+A_[i][j];
				}
			}
		}   
		return C2;
	}
	
	public SolutionType resolve(long num_iteration) throws InterruptedException  {
		
		int var_incoming=0,row_var_outgoing=0;
		SolutionType solution=SolutionType.MAX_ITERATIUM; 
		
		while(this.iteration < num_iteration) {
			
			/*
			logger.log(SscLevel.NOTE,"PRIMA TABELLA:");
			printTable2();
			printBases();
			*/
			
			
			if(isBaseDegenerate()) var_incoming = test_var_incoming_bland();
			//MODIFICA IL 10/04/2024 PASSO TRUE E N_AUX PER NON CONSIDERARE LE 
			//VARIABILI ARTIFICIALI COME CANDIDATE NEL RIENTRARE IN BASE. 
			else var_incoming = test_var_incoming(true,this.n_aux);
			
			if (var_incoming == -1) {	
				solution= SolutionType.OPTIMUM; 
				break;
			} 
			
			if ((row_var_outgoing = test_var_outgoing(var_incoming)) == -1) { 
				solution= SolutionType.ILLIMITATUM;
				break;
			}
			
			if(threadsNumber==LPThreadsNumber.AUTO) pivotingParallel(row_var_outgoing,var_incoming);
			else if(threadsNumber!=LPThreadsNumber.N_1) pivotingParallelCyclic(row_var_outgoing,var_incoming);
			else pivoting(row_var_outgoing,var_incoming);
			
			setBases(row_var_outgoing,var_incoming);
			this.iteration++;
		}
		
		if(solution== SolutionType.MAX_ITERATIUM) logger.log(SscLevel.WARNING,"Raggiunto il massimo numero di iterazioni "+(num_iteration));
		double z=getValueZ();
		if(!isMilp) {
			logger.log(SscLevel.INFO,RB.getString("it.ssc.pl.milp.Phase1.msg1")+z);
		}
		
		if(solution== SolutionType.OPTIMUM && ( Math.abs(z) > this.cepsilon )) {
			if(!isMilp)  {
				logger.log(SscLevel.WARNING,RB.getString("it.ssc.pl.milp.Phase1.msg2")+cepsilon);
				logger.log(SscLevel.WARNING,RB.getString("it.ssc.pl.milp.Phase1.msg3"));
			}
			//solution= SolutionType.PHASE_ONE_GT_EPS;
			solution= SolutionType.VUOTUM;
		}
		else if(solution== SolutionType.ILLIMITATUM) {
			if(!isMilp) logger.log(SscLevel.WARNING,"Fase Uno non ha raggiunto convergenza - Ottenuto ottimo illimitato. ");
		}
		return solution;
	}
	

	 
	
	 //a fronte dell'indice della riga i dove la variabile ausiliaria e' in base, vedo se c'e qualche Aij =! 0 
	 private int existVarOrigOutBase(int index_aux) {
		   for (int j = 0; j < n ; j++) {
			   if ( Math.abs(TBEX[index_aux][j]) > epsilon )  {
				   return j;
			   }
		   }
		   return -1;
	 }
	 
	 //se ce in base una variabile con indice  >=n e' ausiliaria
	 private int existAuxBaseCorr(int start) {
		 //System.out.println("riparto da:"+(start));
		   for (int i = start; i < basis.length; i++) {
				if(basis[i] >= n) {
					//System.out.println("trovata al rigo:"+i +" var:"+basis[i]);
					return i ;
				}
			}
		   return -1;
	 }
 
	public Matrix pulish() throws MatrixException { 
		//se c'e' una variabile ausiliaria in base (naturalmente degenere) si fa uscire 
		//se quelle presenti hanno zero sulle variabili reali.  Si tolgono le righe 
		// si cancellano le colonne relative alle ausiliaria  
		
		//System.out.println("dimensione n:"+this.n +" aux:"+this.n_aux);
		Pulish pulish=new Pulish();
		pulish.exitAuxFromBase();
		double[][] table_pulish=pulish.deleteRowAux(TBEX);
		TBEX=null;
		return pulish.clearColumnAux(table_pulish);
		//return pulish.deleteColumnAux(table_pulish);
	}
	
	//AGGIUNTO IL 15/10/2018
	
	public double[] getValuesBases() {
		double[] values=new double[_M];
		for(int _a=0;_a <this.m;_a++) {
			values[_a]= TBEX[_a][ _N];
			//System.out.println("VALUESSSE:"+values[_a]);
		}
		return values;
	}
 

 private final class Pulish {
	   
	   /*
	   double[][] deleteRowAux_old(double[][] table_pulish) {
			int index_aux_out = -1;
			while (((index_aux_out = existAuxBaseCorr(index_aux_out+1)) != -1) && ifAllCoeffZeroAuxCaz(index_aux_out)) {
				System.out.println("delete row:"+index_aux_out);
				table_pulish=deleteSingleRowAux(index_aux_out,table_pulish);
				updateBase(index_aux_out);
			}
		    return table_pulish;
	   }
	   */
	   
	   double[][] deleteRowAux(double[][] table_pulish) {
			int index_aux_out = -1;
			ArrayList<Integer> lista_del=new ArrayList<Integer>();
			while (((index_aux_out = existAuxBaseCorr(index_aux_out+1)) != -1) && ifAllCoeffZeroAuxCaz(index_aux_out)) {
				//System.out.println("delete row_new:"+index_aux_out);
				lista_del.add(index_aux_out);
			}
			
			int new_dimension=table_pulish.length - lista_del.size();
			double[][] new_table=new double[new_dimension][] ;
			int new_basis[]=new int[basis.length - lista_del.size()];
			int index=0;
			for(int i=0;i<table_pulish.length;i++) {
				if(!lista_del.contains(i))  { 
					new_table[index]=table_pulish[i];
					if(i!=table_pulish.length-1) new_basis[index]=basis[i] ;
					index++;
				}
				//else System.out.println("saltato:"+i);
			}
			basis=new_basis;
		    return new_table;
	   }
	   /*
	   void updateBase(int row_canc ) {
		   int new_basis[]=new int[basis.length-1];
		   int index_row = 0;
		   for (int i = 0; i < basis.length; i++) {
			   if (row_canc==i) continue;
			   new_basis[index_row]=basis[i] ;
			   index_row++;
		   }
		   basis=new_basis;
	   }*/
	    
	    private boolean ifAllCoeffZeroAuxCaz(int index) {
	 	   for (int j = 0; j < n ; j++) {
	 		   if( Math.abs(TBEX[index][j]) > epsilon  ) {
	 			  logger.log(SscLevel.WARNING,"Esiste alla fine di Fase 1, una variabile artificiale in base che non e' stata eliminata ! : ");
	 			  return false;
	 		   }
	 	   }
	 	   return true;
	    }
	    
	    //non tocca numero righe o colonne
	    private void exitAuxFromBase() {
	 	 
	 	   //se esiste una variabile ausiliaria in base e se sulla riga di questa c'e un Aij =! 0 , faccio pivoting per farla uscire
	 	   //finche non escono tutte. 
	    	int index_aux_out = -1, index_orig_in = 0;
	    	while ((index_aux_out = existAuxBaseCorr(index_aux_out+1)) != -1) {
	    		//verifico se esiste qualche Aij !=0 (anche negativo) sul rigo della aux e 
	    		//relativo ad una variabile legittima
	    		if ((index_orig_in = existVarOrigOutBase(index_aux_out)) != -1) {
	    			//printTable2();
			 		//System.out.println("PULIZIA: riga aux uscente:"+index_aux_out);
			 		//System.out.println("PULIZIA: indice var entrante:"+index_orig_in);
			 		   
			 		pivoting(index_aux_out, index_orig_in);
			 		setBases(index_aux_out,index_orig_in);
			 		Phase1.this.iteration++;
			 		index_aux_out = -1;
	    		}   
		 		//else  System.out.println("No pulizia,tutte zero:"+index_aux_out);
	    	}
	 	}
	    
	    /*
	     * versione da testare , con annullamento riga
	     */
	    
	    @SuppressWarnings("unused")
		private Matrix deleteColumnAuxOld(double[][]  A_all_column) throws MatrixException {
			   int n_row = A_all_column.length;
			   double[][] table=new double[n_row][] ;

			   for (int i = 0; i < n_row; i++) {
				   int index_col = 0;
				   table[i]=new double[ n + 1];
				   for (int j = 0; j <= n + n_aux; j++) {
					   if (!(j < n || j == n + n_aux)) continue;
					   table[i][index_col]= A_all_column[i][j]; 
					   index_col++;
				   }
				   A_all_column[i]=null;  //elimino riga per riga per liberare spazio il prima possibile
			   }
			   A_all_column=null;
			   return new Matrix(table);
		   }
	    
	    
	    /*
	     * Versione con riutilizzo della stessa matrice
	     * @param A_all_column
	     * @return
	     * @throws MatrixException
	     */
	    
	    
	    /*
		@SuppressWarnings("unused")
		private Matrix deleteColumnAux(double[][]  A_all_column) throws MatrixException {
			   int n_row = A_all_column.length;
			  
			   for (int i = 0; i < n_row; i++) {
				   int index_col = 0;
				   double[] subarray=new double[n + 1];
				   for (int j = 0; j <= n + n_aux; j++) {
					   if (!(j < n || j == n + n_aux)) continue;  //QUI SI PUO OTTIMIZZARE
					   subarray[index_col]= A_all_column[i][j]; 
					   index_col++;
				   }
				   A_all_column[i]=subarray;  
			   }
			 
			   return new Matrix(A_all_column);
		   }
		   */
	    
	    
	    /**
	     * Questo metodo sostituisce deleteColumnAux. 
	     * Uso la stessa matrice mettendo a NaN sulle colonne aux non piu'
	     * utilizzate. 
	     * 
	     * @param A_all_column
	     * @return
	     * @throws MatrixException
	     */
	    
	    private Matrix clearColumnAux(double[][]  A_all_column) throws MatrixException {
			   int n_row = A_all_column.length;
			   for (int i = 0; i < n_row; i++) {
				   
				   /* La nuova matrice deve avere n+1 colonne.
				    * Sulla colonna n+1, occorre scrivere i valori rhs (colonna n + n_aux) della matrice originaria. 
				    * Fatto questo tutti i valore da n+2 in poi vanno messi a NaN
				    */
				   A_all_column[i][n]=  A_all_column[i][n + n_aux];
				   for (int j = (n+1); j <= n + n_aux; j++) {
					   A_all_column[i][j]=Double.NaN;
				   }
			   }
			   Matrix newA= new Matrix(A_all_column); 
			   newA.setCustomnNcolumn(n + 1);
			   //A_all_column=null;
			   return newA;
		   }
	    
	    
	   
	   //versione nuove da Testare. Fatta copia riga per riga.   ???????????????
	   
	   /* 
	   double[][] deleteSingleRowAux(int row_canc ,double[][] table_pulish) {
			int n_row = table_pulish.length;  
			double[][] table=new double[n_row-1][] ;

			int index_row = 0;
			for (int i = 0; i < n_row; i++) {
				if (row_canc==i) continue;
				table[index_row]= table_pulish[i];
				index_row++;
			}
			return table;
	   }*/
 }  
}

