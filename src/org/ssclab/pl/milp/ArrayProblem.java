package org.ssclab.pl.milp;

import java.util.ArrayList;
import org.ssclab.pl.milp.Variable.TYPE_VAR;

public class ArrayProblem {
	public Double array_upper[];
	public Double array_lower[];
	//se qualche variabile fa gia parte di un SOS non puo far parte di un altro gruppo
	//con questa variabile faccio questo controllo. 0=nessun SOS sulla var. 1= primo gruppo
	//2=secondo gruppo,3=terzo gruppo.
	public TYPE_VAR array_sos1[];
	public byte array_sec[];
	public byte array_bin[];
	public byte array_int[];
	public boolean isMilp;
	public ArrayList<SosGroup> listSosGroup;
	
	public ArrayProblem(int dim) {
		 array_upper=new Double[dim];
		 array_lower=new Double[dim];
		 array_sos1=new TYPE_VAR[dim];
		 listSosGroup=new ArrayList<SosGroup>();
		 array_sec=new byte[dim];
		 array_bin=new byte[dim];
		 array_int=new byte[dim];
		 isMilp=false;
	}
}
