package org.ssclab.pl.milp;

import java.util.HashMap;

 class Meta {
	private HashMap<String, Object> map;

	 Meta() {
		// TODO Auto-generated constructor stub
		map=new HashMap<String, Object>();
	}
	
	 Object getProperty(String key) {
		 return map.get(key);
	 }
	 
	 void put(String key,Object obj) {
		  map.put(key, obj);
	 }
	

}
