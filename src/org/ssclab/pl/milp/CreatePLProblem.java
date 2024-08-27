package org.ssclab.pl.milp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.ssclab.datasource.DataSource;
import org.ssclab.datasource.DataSourceException;
import org.ssclab.i18n.RB;
import org.ssclab.log.SscLevel;
import org.ssclab.log.SscLogger;
import static org.ssclab.pl.milp.InternalConstraint.TYPE_CONSTR;

 final class CreatePLProblem implements Costant  {
	 
	 private static final Logger logger=SscLogger.getLogger();
	 
	 protected static PLProblem create(LinearObjectiveFunction f,
			 ArrayList<InternalConstraint> constraints, ArrayList<String> nomi_var,ArrayProblem arrayProb,
			 boolean isMilp) throws Exception  {

		 //checkDimensionProblem(f,constraints);

		 double[] C=f.getC();
		 int N=C.length;
		 PLProblem lp_original=new PLProblem(N);
		 int _x=0;
		 for(String nome:nomi_var)  {
			 lp_original.setNameVar(_x++, nome);
		 }

		 if(f.getType()==GoalType.MAX) lp_original.setTargetObjFunction(MAX);
		 else if(f.getType()==GoalType.MIN) lp_original.setTargetObjFunction(MIN);
		 for(int _j=0;_j<N; _j++)  {
			 lp_original.setCjOF(_j, C[_j]);
		 }


		 for(InternalConstraint cons:constraints)  lp_original.addConstraint(cons);

		 Var xj;
		 Double upper_val,lower_val;
		 for(int _a=0;_a<N; _a++)  {
			 xj=lp_original.getVar(_a);
			 lower_val=arrayProb.array_lower[_a];
			 upper_val=arrayProb.array_upper[_a]; 
			 if(upper_val!=null)  xj.setUpper(upper_val);
			 if(lower_val!=null)  xj.setLower(lower_val);
		 }

		 if(!isMilp && arrayProb.isMilp) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg5"));


		 for(int _j=0;_j<N; _j++)  {

			 xj=lp_original.getVar(_j);

			 //System.out.println("VAR  "+_j +"  "+type);
			 if(arrayProb.array_int[_j]==1) { 
				 if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg9", (_j+1)));
				 xj.setType(Var.TYPE_VAR.INTEGER);
			 }
			 if(arrayProb.array_bin[_j]==1) { 
				 if(xj.getType()==Var.TYPE_VAR.INTEGER) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg10", (_j+1)));
				 xj.setType(Var.TYPE_VAR.BINARY);
			 }

			 if(arrayProb.array_sec[_j]==1) { 
				 if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg11", (_j+1)));
				 xj.setSemicon(true);;
			 }
		 }
		 
		 lp_original.configureFree();
		 return lp_original;
	 }

	 
	 protected static PLProblem create(LinearObjectiveFunction f,
			 						ArrayList<Constraint> constraints,
			 						boolean isMilp) throws Exception  {

		 boolean exist_integer_var=false;
		 boolean is_set_row_upper=false;
		 boolean is_set_row_lower=false;
		 boolean is_set_row_binary=false;
		 boolean is_set_row_integer=false;
		 boolean is_set_row_semicont=false;
		 
		 checkDimensionProblem(f,constraints);
		
		 double[] C=f.getC();
		 int N=C.length;
		 PLProblem lp_original=new PLProblem(N);
		 for(int _j=0;_j<N; _j++)  {
			 lp_original.setNameVar(_j, "X"+(_j+1));
		 }

		 if(f.getType()==GoalType.MAX) lp_original.setTargetObjFunction(MAX);
		 else if(f.getType()==GoalType.MIN) lp_original.setTargetObjFunction(MIN);
		 for(int _j=0;_j<N; _j++)  {
			 lp_original.setCjOF(_j, C[_j]);
		 }
		 
		 ConsType rel=null;
		 double double_b=0.0;
		 double[] Aj=null;
		 
		 for(Constraint constraint:constraints) {

			 rel=constraint.getRel();

			 if(rel==ConsType.EQ || rel==ConsType.GE || rel==ConsType.LE) {
				 InternalConstraint constraint_i=new InternalConstraint(N); 
				 String name=constraint.getName();
				 if(name!=null) constraint_i.setName(name);
				 
				 double_b=constraint.getRhs();
				 if(Double.isNaN(double_b)) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg1"));
				 constraint_i.setBi(double_b);
				 
				 Aj=constraint.getAj();
				 for(int _j=0;_j<N; _j++)  {
					 if(Double.isNaN(Aj[_j])) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg2"));
				 }
				 constraint_i.setAi(Aj);

				 if(rel==ConsType.LE) constraint_i.setType(InternalConstraint.TYPE_CONSTR.LE);
				 else if(rel==ConsType.GE ) constraint_i.setType(InternalConstraint.TYPE_CONSTR.GE);
				 else if(rel==ConsType.EQ ) constraint_i.setType(InternalConstraint.TYPE_CONSTR.EQ);

				 lp_original.addConstraint(constraint_i);
			 }


			 else if(rel==ConsType.LOWER || rel==ConsType.UPPER) {
				 if(rel==ConsType.UPPER  ) { 
					 if(is_set_row_upper) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg3"));
					 else is_set_row_upper=true;
				 }
				 if(rel==ConsType.LOWER  ) { 
					 if(is_set_row_lower) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg4"));
					 else is_set_row_lower=true;
				 }

				 Aj=constraint.getAj();
				 double bound_val=0.0;
				 for(int _j=0;_j<N; _j++)  {
					 Var xj=lp_original.getVar(_j);
					 bound_val=Aj[_j];
					 //System.out.println("AIUTOOOOO::::"+bound_val);;
					 if(rel==ConsType.UPPER) xj.setUpper(bound_val);
					 else if(rel==ConsType.LOWER ) xj.setLower(bound_val);
				 }
			 }


			 //FACOLTATIVO, UNA SOLA VOLTA, esclusivo o binary o integer
			 else if(rel==ConsType.BIN || rel==ConsType.INT || rel==ConsType.SEMICONT ) {
				 exist_integer_var=true;
				 if(!isMilp) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg5"));

				 if(rel==ConsType.BIN ) { 
					 if(is_set_row_binary) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg6"));
					 else is_set_row_binary=true;
				 }
				 if(rel==ConsType.INT ) { 
					 if(is_set_row_integer) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg7"));
					 else is_set_row_integer=true;
				 }
				 
				 if(rel==ConsType.SEMICONT ) { 
					 if(is_set_row_semicont) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg8"));
					 else is_set_row_semicont=true;
				 }

				 Aj=constraint.getAj();

				 for(int _j=0;_j<N; _j++)  {

					 Var xj=lp_original.getVar(_j);
					 double type_var=Aj[_j];

					 if(!Double.isNaN(type_var) && type_var==1.0) {
						 //System.out.println("VAR  "+_j +"  "+type);
						 if(rel==ConsType.INT) { 
							 if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg9", (_j+1)));
							 xj.setType(Var.TYPE_VAR.INTEGER);
						 }
						 if(rel==ConsType.BIN)  {
							 if(xj.getType()==Var.TYPE_VAR.INTEGER) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg10", (_j+1)));
							 xj.setType(Var.TYPE_VAR.BINARY);
						 }
						 
						 if(rel==ConsType.SEMICONT)  {
							 if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg11", (_j+1)));
							 xj.setSemicon(true);;
						 }
						 
					 }
					 else if(Double.isNaN(type_var) || type_var!=0.0) {
						 if(rel==ConsType.INT) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg12"));
						 if(rel==ConsType.BIN) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg13"));
						 if(rel==ConsType.SEMICONT) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg14"));
					 }
				 }
			 }
		 }
		 
		 lp_original.configureFree();
		 
		 if(isMilp && !exist_integer_var) logger.log(SscLevel.WARNING,RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg15"));
		 return lp_original;
	 }


	
	//ATTENZIONE FARE TUTTI i CONTROLLI 
	
	protected static PLProblem create(DataSource lp_data_source,boolean isMilp) throws Exception  {
		
		boolean exist_integer_var=false;
		boolean is_set_row_obj_function=false;
		boolean is_set_row_upper=false;
		boolean is_set_row_lower=false;
		boolean is_set_row_binary=false;
		boolean is_set_row_integer=false;
		boolean is_set_row_semicont=false;
		
		int N=0;
		int num_col_datasource=lp_data_source.getNumColunm();
		if(lp_data_source.existVar("NAME_CONST"))  {
			N=num_col_datasource-3;
		}
		else N=num_col_datasource-2;
		
		//logger.log(Level.INFO, "Numero var dichiarate:"+N);
		
		/*Crea un problema con N variabili */
		PLProblem pl_original=new PLProblem(N);
		
		boolean exist_column_type=false; 
		boolean exist_column_rhs=false;  
		for(int _a=0;_a<num_col_datasource; _a++)  {
			String name=lp_data_source.getNameColunm(_a);
			if(name.equalsIgnoreCase("TYPE")) {
				exist_column_type=true;
			}
			else if(name.equalsIgnoreCase("RHS")) {  
				exist_column_rhs=true;
			}
		}
		if(!exist_column_type) throw  new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg16"));
		if(!exist_column_rhs)  throw  new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg17"));
		
		/*le colonne TYPE e RHS possono trovarsi anche prima della dichiarzione delle variabili*/
		for(int _a=0,_j=0;_a<num_col_datasource; _a++)  {
			String name=lp_data_source.getNameColunm(_a);
			if(!(name.equalsIgnoreCase("NAME_CONST") || name.equalsIgnoreCase("TYPE") || name.equalsIgnoreCase("RHS"))) { 
				pl_original.setNameVar(_j, name);
				_j++;
			}
		}
		
		//leggo i record del datasource
		while(lp_data_source.next()) { 
			/*La riga con TYPE = max/min non deve essere necessariamente ESSERE la prima, 
			 ma la elaboro appena la incontro*/
			String type=lp_data_source.getString("TYPE");
			//System.out.println("TYPE"+type);
			// il type deve essere sempre valorizzato 
			if(type==null) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg18"));
			//OBLIGATORIO, UNA SOLA VOLTA
			//se e' la riga della fo
			if(type.equalsIgnoreCase(MAX) || type.equalsIgnoreCase(MIN)) {
				if(is_set_row_obj_function) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg19")+type );
				}
				//per 
				is_set_row_obj_function=true;
				pl_original.setTargetObjFunction(type);
				//leggo gli N valori (size-2) dei cj
				for(int _a=0,_j=0;_a<num_col_datasource; _a++)  {
					String name=lp_data_source.getNameColunm(_a);
					if(!(name.equalsIgnoreCase("NAME_CONST") || name.equalsIgnoreCase("TYPE") || name.equalsIgnoreCase("RHS"))) {
						Double double_val=lp_data_source.getDouble(_a);
						if(double_val==null) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg20"));
						pl_original.setCjOF(_j, double_val);
						_j++;
					}
				}
			}
			else if(type.equalsIgnoreCase(LE) || 
					type.equalsIgnoreCase(GE) ||  
					type.equalsIgnoreCase(EQ)) {
				
				InternalConstraint constraint_i=new InternalConstraint(N) ;
				Double double_val;
				for(int _a=0,_j=0;_a<num_col_datasource; _a++)  {
					String name=lp_data_source.getNameColunm(_a);
					
					//se la variabile letta e' type ('LE','GE','EQ') 
					if(name.equalsIgnoreCase("TYPE") ) {
						if(type.equalsIgnoreCase(LE)) constraint_i.setType(TYPE_CONSTR.LE);
						else if(type.equalsIgnoreCase(GE)) constraint_i.setType(TYPE_CONSTR.GE);
						else if(type.equalsIgnoreCase(EQ)) constraint_i.setType(TYPE_CONSTR.EQ);
					}
					else if(name.equalsIgnoreCase("RHS")) {
						double_val=lp_data_source.getDouble(_a);
						if(double_val==null) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg1"));
						constraint_i.setBi(double_val);
					}
					else if(name.equalsIgnoreCase("NAME_CONST")) {
						String name_cont=lp_data_source.getString(_a);
						constraint_i.setName(name_cont);
					}
					else {
						double_val=lp_data_source.getDouble(_a);
						if(double_val==null) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg21"));
						constraint_i.setAij(_j, double_val);
						_j++;
					}
				}
				pl_original.addConstraint(constraint_i);
			}
			
			//riga upper / lower
			else if(type.equalsIgnoreCase(UPPER)|| type.equalsIgnoreCase(LOWER)) {
				//FACOLTATIVO, UNA SOLA VOLTA
				//UPPER
				if(type.equalsIgnoreCase(UPPER) ) { 
					if(is_set_row_upper) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg3"));
					else is_set_row_upper=true;
				}
				else  { //LOWER 
					if(is_set_row_lower) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg4"));
					else is_set_row_lower=true;
				}
				Var xj;
				Double bound_val;
				for(int _a=0,_j=0;_a<num_col_datasource; _a++)  {
					String name=lp_data_source.getNameColunm(_a);
					if(!(name.equalsIgnoreCase("NAME_CONST") || name.equalsIgnoreCase("TYPE") || name.equalsIgnoreCase("RHS"))) {
						xj=pl_original.getVar(_j);
						bound_val=lp_data_source.getDouble(_a);
						if(type.equalsIgnoreCase(UPPER)) xj.setUpper(bound_val);
						else xj.setLower(bound_val);
						_j++;
					}
				}
			}
			
			//FACOLTATIVO, UNA SOLA VOLTA, esclusivo o binary o integer
			else if(type.equalsIgnoreCase(BINARY)|| type.equalsIgnoreCase(INTEGER) || type.equalsIgnoreCase(SEMICONT)) {
				if(!isMilp) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg5"));
				exist_integer_var=true;
				if(type.equalsIgnoreCase(BINARY) ) { 
					if(is_set_row_binary) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg6"));
					else is_set_row_binary=true;
				}
				if(type.equalsIgnoreCase(INTEGER) ) { 
					if(is_set_row_integer) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg7"));
					else is_set_row_integer=true;
				}
				if(type.equalsIgnoreCase(SEMICONT) ) { 
					if(is_set_row_semicont) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg8"));
					else is_set_row_semicont=true;
				}
				
				for(int _a=0,_j=0;_a<num_col_datasource; _a++)  {
					String name=lp_data_source.getNameColunm(_a);
					if(!(name.equalsIgnoreCase("NAME_CONST") ||name.equalsIgnoreCase("TYPE") || name.equalsIgnoreCase("RHS"))) {
						Var xj=pl_original.getVar(_j);
						Double type_var=lp_data_source.getDouble(_a);
						//System.out.println("var type integer:"+type_var);
						if(type_var!=null && type_var==1.0) {
							//System.out.println("VAR  "+_j +"  "+type);
							if(type.equalsIgnoreCase(INTEGER)) { 
								if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg22", name));
								xj.setType(Var.TYPE_VAR.INTEGER);
							}
							if(type.equalsIgnoreCase(BINARY))  {
								if(xj.getType()==Var.TYPE_VAR.INTEGER) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg23", name));
								if(xj.isSemicon()) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg24", name));
								xj.setType(Var.TYPE_VAR.BINARY);
							}
							if(type.equalsIgnoreCase(SEMICONT))  {
								if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg25", name));
								//essere semicontinua e intera contemporaneamente e' possibile
								xj.setSemicon(true);
							}
						}
						else if(type_var==null || type_var!=0.0) {
							if(type.equalsIgnoreCase(INTEGER)) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg12"));
							if(type.equalsIgnoreCase(BINARY))  throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg13"));
							if(type.equalsIgnoreCase(SEMICONT))  throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg14"));
						}
						_j++;
					}
				}
			}
			else {
				throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg26")+type); 
			}
		}
		lp_data_source.close();
		
		//verifica se upper > lower e setta le variabili se free
		pl_original.configureFree();
		
		if(!is_set_row_obj_function) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg50"));
		if(isMilp && !exist_integer_var) logger.log(SscLevel.WARNING,RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg15"));
		return pl_original;
	}
	
	
	
	protected static PLProblem createFromSparse(DataSource source_sparse,boolean isMilp) throws DataSourceException, Exception {
		HashMap<String,String> hash_row_type=new HashMap<String,String>(); 
		HashMap<String,HashMap<String, Double>> hash_row_col=new HashMap<String,HashMap<String, Double>>(); 
		TreeSet<String> tree_col=new TreeSet<String>();
		
		int size=source_sparse.getNumColunm();

		boolean exist_column_type=false; 
		boolean exist_column_col=false;  
		boolean exist_column_row=false; 
		boolean exist_column_coe=false; 
		for(int _a=0;_a<size; _a++)  {
			String name=source_sparse.getNameColunm(_a);

			if(name.equalsIgnoreCase("TYPE")) {
				exist_column_type=true;
			}
			if(name.equalsIgnoreCase("COL_")) {  
				exist_column_col=true;
			}
			if(name.equalsIgnoreCase("ROW_")) {  
				exist_column_row=true;
			}
			if(name.equalsIgnoreCase("COEF")) {  
				exist_column_coe=true;
			}
		}
		if(!exist_column_type) throw  new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg27"));
		if(!exist_column_col)  throw  new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg28"));
		if(!exist_column_row)  throw  new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg29"));
		if(!exist_column_coe)  throw  new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg30"));


		int num_row=0;	
		while(source_sparse.next()) { 
			num_row++;
			String type=source_sparse.getString("TYPE");
			if(type!=null) {
				type=type.toUpperCase();
				if(!(type.equals(MAX)   || type.equals(MIN)    || type.equals(GE)      || 
					 type.equals(LE)    || type.equals(EQ)     || type.equals(UPPER)   || 
					 type.equals(LOWER) || type.equals(BINARY) || type.equals(INTEGER) || 
					 type.equals(SEMICONT) )) { 
					
					 throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg26")+type);	
				}
				
				if(type.equalsIgnoreCase(BINARY) || type.equalsIgnoreCase(INTEGER) || type.equalsIgnoreCase(SEMICONT)) {
					if(!isMilp) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg5"));
				}	
				
				if(type.equals(MAX) && ( hash_row_type.containsValue(MIN) || hash_row_type.containsValue(MAX)) ) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg31")+num_row);
				}
				else if(type.equals(MIN) && ( hash_row_type.containsValue(MIN) || hash_row_type.containsValue(MAX))) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg32")+num_row);
				}
				
				if(type.equals(UPPER) && hash_row_type.containsValue(UPPER)  ) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg33")+num_row);
				}
				if(type.equals(LOWER) && hash_row_type.containsValue(LOWER)  ) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg34")+num_row);
				}
				if(type.equals(BINARY) && hash_row_type.containsValue(BINARY)  ) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg35")+num_row);
				}
				if(type.equals(INTEGER) && hash_row_type.containsValue(INTEGER)  ) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg36")+num_row);
				}
				if(type.equals(SEMICONT) && hash_row_type.containsValue(SEMICONT)  ) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg37")+num_row);
				}
				
				
				String row=source_sparse.getString("ROW_");	
				if(row==null) {
					throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg38", type)+num_row); 
				}
				if(hash_row_type.containsKey(row)) {
					throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg39", row,hash_row_type.get(row))+num_row);    
				}
				hash_row_type.put(row, type);
			}
			else {
				String col=source_sparse.getString("COL_");
				String row=source_sparse.getString("ROW_");
				Double coef=source_sparse.getDouble("COEF");
				
				
				if(row==null) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg40")+num_row); 
				}
				if(col==null) {
					throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg41")+num_row); 
				}
				col=col.toUpperCase();
				
				tree_col.add(col);
				
				HashMap<String, Double> hash_col_val;
				if(hash_row_col.containsKey(row)) { 
					hash_col_val=hash_row_col.get(row);
					hash_col_val.put(col, coef);
				}
				else { 
					hash_col_val=new HashMap<String, Double>();
					hash_col_val.put(col, coef);
					hash_row_col.put(row, hash_col_val);
				}
			}
		}	
		
		//controllo che ogni row di tipo EQ,LE,GE abbia valorizzato coef
		for(String row2:hash_row_type.keySet()) {
			String type2=hash_row_type.get(row2);
			if( type2.equals(LE) || type2.equals(EQ) || type2.equals(GE) || type2.equals(MAX) || type2.equals(MIN)) {
				//logger.log(SscLevel.WARNING,"####:"+row2);
				HashMap<String, Double> hash_col_val2=hash_row_col.get(row2);
				if(hash_col_val2==null) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg49", row2));
				for(String col2:tree_col) {
					Double coef2=hash_col_val2.get(col2);
					if( !col2.equals("RHS") && coef2==null) hash_col_val2.put(col2, 0.0); //se manca vuol dire che e' a zero
				}
			}
		}
		
		//Controlli da fare : a) ogni vincolo di tipo EQ,LE,Ge deve avere un RHS 
		
		
		for(String row2:hash_row_type.keySet()) {
			String type2=hash_row_type.get(row2);
			if( type2.equals(LE)    || type2.equals(EQ)     || type2.equals(GE))  {
				HashMap<String, Double> hash_col_val2=hash_row_col.get(row2);
				boolean exist_rhs=false;
				for(String col2:hash_col_val2.keySet()) {
					if(col2.equals("RHS")) exist_rhs=true;
					//System.out.println("ROW:"+row2 +" COL:"+col2 );
				}
				if(!exist_rhs) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg42", row2,type2));  
			}
		}
		
		
		for(String row:hash_row_col.keySet()) {
			if(!hash_row_type.containsKey(row)) {
				throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg43", row));  
			}
		}	
		
		//CONTROLLARE CHE ESISTA ALMENO UN MAX O MIN
		if(!(hash_row_type.containsValue(MAX) || hash_row_type.containsValue(MIN)))  {
			throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg44")); 
		}
		
		source_sparse.close();
		return createMilpFromHashTables( hash_row_type,hash_row_col,tree_col) ;
	}
	
	
	
	protected static PLProblem createMilpFromHashTables(HashMap<String,String> hash_row_type,
											  HashMap<String,HashMap<String, Double>> hash_row_col,
											  TreeSet<String> tree_col) throws LPException  {
		
		int size=tree_col.size();
		int N=size;
		if(tree_col.contains("RHS")) N=size-1;
		//System.out.println("DIME::"+N);
		PLProblem lp_original=new PLProblem(N);
		int _j=0;
		HashMap<String,Integer> link_name_index=new HashMap<String,Integer>();
		for(String nome_var:tree_col)  {
			 if(!nome_var.equals("RHS")) {
				 lp_original.setNameVar(_j,nome_var);
				 link_name_index.put(nome_var, _j);
				 _j++;
			 }
		 }
		
		for(String row:hash_row_col.keySet()) { 
			String type_row=hash_row_type.get(row);
			HashMap<String,Double> hah_col_val=hash_row_col.get(row);
			
			if(type_row.equals(MAX) || type_row.equals(MIN)) {
				lp_original.setTargetObjFunction(type_row);

				for(String col:hah_col_val.keySet()) { 
					if(!col.equals("RHS")) { 
						lp_original.setCjOF(link_name_index.get(col), hah_col_val.get(col));
						//System.out.println("xxx CIJ::"+link_name_index.get(col)+"::"+hah_col_val.get(col));
					}
				}
			}
			
			else if(type_row.equals(LE) || type_row.equals(GE) || type_row.equals(EQ)) {
				InternalConstraint constraint_i=new InternalConstraint(N) ;
				constraint_i.setName(row);
				//System.out.println("xxx RHS::"+row);
				for(String col:hah_col_val.keySet()) { 
					if(col.equals("RHS")) { 
						 Double double_b=hah_col_val.get(col);
						 if(double_b==null) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg1"));
						 constraint_i.setBi(double_b);
						 //System.out.println("xxx RHS::"+double_b);
					}
					else {
						Double double_val=hah_col_val.get(col);
						if(double_val==null) throw new LPException(RB.getString("it.ssc.pl.milp.CreateMilpProblem.msg2"));
						constraint_i.setAij(link_name_index.get(col), double_val);
						//System.out.println("xxx AIJ::"+link_name_index.get(col)+"::"+double_val);

						if(type_row.equals(LE)) constraint_i.setType(InternalConstraint.TYPE_CONSTR.LE);
						else if(type_row.equals(GE)) constraint_i.setType(InternalConstraint.TYPE_CONSTR.GE);
						else if(type_row.equals(EQ)) constraint_i.setType(InternalConstraint.TYPE_CONSTR.EQ);
					}
				}
				lp_original.addConstraint(constraint_i);
			}
			
			else if(type_row.equals(UPPER) || type_row.equals(LOWER) ) {
				
				for(String col:hah_col_val.keySet()) { 
					if(!col.equals("RHS")) { 
						Var xj=lp_original.getVar(link_name_index.get(col));
						Double bound_val=hah_col_val.get(col);
						if(type_row.equals(UPPER)) xj.setUpper(bound_val);
						if(type_row.equals(LOWER)) xj.setLower(bound_val);
						//System.out.println("xxx "+type_row+"::"+link_name_index.get(col)+"::"+bound_val);
					}
				}
			}
			
			
			else if(type_row.equalsIgnoreCase(BINARY) || type_row.equalsIgnoreCase(INTEGER) || type_row.equalsIgnoreCase(SEMICONT)) {

				for(String col:hah_col_val.keySet()) { 
					if(!col.equals("RHS")) { 

						Var xj=lp_original.getVar(link_name_index.get(col));
						Double type_var=hah_col_val.get(col);
						
						if(type_var!=null && type_var==1.0) {
							//System.out.println("VAR  "+_j +"  "+type_row);
							if(type_row.equalsIgnoreCase(INTEGER)) { 
								if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg22", col));
								xj.setType(Var.TYPE_VAR.INTEGER);
							}
							if(type_row.equalsIgnoreCase(BINARY))  {
								if(xj.getType()==Var.TYPE_VAR.INTEGER) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg23", col));
								xj.setType(Var.TYPE_VAR.BINARY);
							}
							if(type_row.equalsIgnoreCase(SEMICONT)) { 
								if(xj.getType()==Var.TYPE_VAR.BINARY) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg25", col));
								xj.setSemicon(true);
							}
						}
						else if(type_var==null || type_var!=0.0) {
							if(type_row.equalsIgnoreCase(INTEGER)) throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg45", row));
							if(type_row.equalsIgnoreCase(BINARY))  throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg46", row));
							if(type_row.equalsIgnoreCase(SEMICONT))  throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg47", row));
						}
					}
				}
			}
		}	
		lp_original.configureFree();
		return lp_original;
	}
	
	
	protected static void checkDimensionProblem(LinearObjectiveFunction f,
											  ArrayList<Constraint> constraints) throws LPException {

		double[] c=f.getC();
		int n=c.length;

		for(Constraint constraint:constraints) {
			if(n!= constraint.getAj().length) {
				throw new LPException(RB.format("it.ssc.pl.milp.CreateMilpProblem.msg48", constraint.getRel(),n)); 		 
			}
		}
	}
}
