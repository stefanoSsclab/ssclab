package org.ssclab.vrp;

import java.util.HashMap;
import java.util.Map;

public final class Solution extends HashMap<Integer, Route>{
	private static final long serialVersionUID = 1L;
	
	public Solution() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Solution(Map<? extends Integer, ? extends Route> m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	double  getCostoTotale() {
		double costoTotale = 0;
		for (Route percorso : this.values()) {
			costoTotale += percorso.getCost();
		}
		return costoTotale;
	}
}
