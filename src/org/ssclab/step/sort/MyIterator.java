package org.ssclab.step.sort;

import java.io.IOException;

import org.ssclab.dynamic_source.DynamicClassSortInterface;

public interface MyIterator  {
	
	public boolean hasNext()  ;
	public DynamicClassSortInterface next() throws IOException, ClassNotFoundException; 
	
}
