package xample;

import java.util.ArrayList;
import java.util.Random;

import it.ssc.log.SscLogger;
import it.ssc.pl.milp.LP;
import it.ssc.pl.milp.Solution;
import it.ssc.pl.milp.SolutionConstraint;
import it.ssc.pl.milp.SolutionType;
import it.ssc.pl.milp.Variable;

public class TestTrasposto {
	
	public static void main(String arg[]) throws Exception  {
		
		
		double[] capacita_orig={20,13,23,13,34,23,13,18,29,16,26,25,15,19, 
				                23,18,21,16,24,21,17,19,21,26,16,15,17,29,
				                24,21,34,21,17,21,18,13,21,15,19,14,18,26};
		
		double[] capacita_dest={10,6,8,8,8,8,13,8,12,16,13,12,7,6, 
                23,18,9,16,24,21,17,19,21,15,16,15,17,8,
                24,21,7,8,7,21,18,13,11,15,8,14,9,11, 9,13,13,13,15,12,13,18,13,13,16,12,15,9, 
                    10,7,7,6,11,8,5,9,6,12,6,9,11};
		
		
		
		/*
		double[] capacita_orig={20,20,23,13,34};

        double[] capacita_dest={10,6,8,8,8,8,13,8,12,16,13};
		
		*/
		double totale_orig=0;
		for(double capacita_singola:capacita_orig) totale_orig+=capacita_singola;
		System.out.println(totale_orig);
		
		double totale_dest=0;
		for(double capacita_singola:capacita_dest) totale_dest+=capacita_singola;
		System.out.println(totale_dest);
		
		
		/*
		
		double[][] Aij=new double[capacita_orig.length*capacita_dest.length][capacita_orig.length+capacita_dest.length];
		
		for(int i=0;i<capacita_orig.length;i++) 
			for(int j=0;j<capacita_dest.length;j++) Aij[i][j]=1.0;
		
		
		ArrayList< Constraint > constraints = new ArrayList< Constraint >();
		for(int i=0; i < capacita_orig.length; i++) {
			constraints.add(new Constraint(Aij[i], ConsType.LE, capacita_orig[i])); 
		}    
		
		for(int i=0; i < capacita_dest.length; i++) {
			constraints.add(new Constraint(Aij[i], ConsType.LE, capacita_dest[i])); 
		} 
		
		
		*/
		Random random = new Random();
		String fo="min:";
		for(int i=0; i < capacita_orig.length; i++) 
			for(int j=0; j < capacita_dest.length; j++) fo+="+X"+(i+1)+"o"+(j+1)+" ";
		
		//Xij quantita merce da i a j 
		System.out.println("Funzione costo:"+fo);
		
		ArrayList<String> lista_vincoli=new ArrayList<String>();
		lista_vincoli.add(fo);
		
		for(int i=0; i < capacita_orig.length; i++) {
			String vincolo="origine"+(i+1)+":";
			for(int j=0; j < capacita_dest.length; j++) {
				vincolo+="+X"+(i+1)+"o"+(j+1)+" ";
			}
			vincolo+="="+capacita_orig[i];
			lista_vincoli.add(vincolo);
			System.out.println(vincolo);
		}
		
		for(int j=0; j < capacita_dest.length; j++) {
			String vincolo="desti"+(j+1)+":";
			for(int i=0; i < capacita_orig.length; i++) {
				vincolo+="+X"+(i+1)+"o"+(j+1)+" ";
			}
			vincolo+="="+capacita_dest[j];
			lista_vincoli.add(vincolo);
			System.out.println(vincolo);
		}
		
		String vincolo="int ";
		for(int j=0; j < capacita_dest.length; j++) {
			for(int i=0; i < capacita_orig.length; i++) {
				vincolo+="X"+(i+1)+"o"+(j+1)+" ";
				if(!(j==(capacita_dest.length -1) && i==(capacita_orig.length-1))) vincolo+=",";
			}
			
		}
		System.out.println(vincolo);
		lista_vincoli.add(vincolo);
		
	     LP lp = new LP(lista_vincoli); 
         SolutionType solution_type=lp.resolve();
       
      if(solution_type==SolutionType.OPTIMUM) {
          Solution soluzione=lp.getSolution();
          for(Variable var:soluzione.getVariables()) {
              SscLogger.log("Nome variabile :"+var.getName() + " valore :"+var.getValue());
          }
          for (SolutionConstraint sol_constraint : soluzione.getSolutionConstraint()) {
				SscLogger.log("Vincolo " + sol_constraint.getName() + " : valore=" + sol_constraint.getValue() + "[ "
						+ sol_constraint.getRel() + "  " + sol_constraint.getRhs() + " ]");
			}
          SscLogger.log("Valore ottimo:"+soluzione.getOptimumValue());
      }
		
		
	}

}
