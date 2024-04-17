package org.ssclab.pl.milp;

public class ArrayProblem {
	public Double array_upper[];
	public Double array_lower[];
	public byte array_sec[];
	public byte array_bin[];
	public byte array_int[];
	public boolean isMilp;
	
	public ArrayProblem(int dim) {
		 array_upper=new Double[dim];
		 array_lower=new Double[dim];
		 array_sec=new byte[dim];
		 array_bin=new byte[dim];
		 array_int=new byte[dim];
		 isMilp=false;
	}
}
