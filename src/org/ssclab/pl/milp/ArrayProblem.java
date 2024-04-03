package org.ssclab.pl.milp;

public class ArrayProblem {
	public Double array_upper[];
	public Double array_lower[];
	public double array_sec[];
	public double array_bin[];
	public double array_int[];
	public boolean isMilp;
	
	public ArrayProblem(int dim) {
		 array_upper=new Double[dim];
		 array_lower=new Double[dim];
		 array_sec=new double[dim];
		 array_bin=new double[dim];
		 array_int=new double[dim];
		 isMilp=false;
	}
}
