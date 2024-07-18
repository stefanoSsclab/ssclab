package org.ssclab.vrp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

final public class Route {
	ArrayList<Integer> listRouteNodes;
	private static HashMap<Integer, Node> listAllNodes;
	private static double[][] distanceMatrix;
	int index;
	private static int depotIndex = 0;
	private static int indexCounter = 1; // contatore statico
	// private static double maxCapacity;

	// DA CAMBIARE RENDERE COME METODO FABBRICATORE
	Route(Route percorso_i, Route percorso_j) {
		this.listRouteNodes = new ArrayList<Integer>();
		int nodo_corrente;
		for (int i = 0; i < percorso_i.listRouteNodes.size() - 1; i++) {
			nodo_corrente = percorso_i.listRouteNodes.get(i);
			listRouteNodes.add(nodo_corrente);
			if (nodo_corrente != Route.depotIndex) listAllNodes.get(nodo_corrente).route = this;
		}
		for (int j = 1; j < percorso_j.listRouteNodes.size(); j++) {
			nodo_corrente = percorso_j.listRouteNodes.get(j);
			listRouteNodes.add(nodo_corrente);
			if (nodo_corrente != Route.depotIndex) listAllNodes.get(nodo_corrente).route = this;
		}
		this.index = indexCounter++;
	}

	Route() {
		listRouteNodes = new ArrayList<Integer>();
		this.index = indexCounter++;
	}

	/*
	Route(Integer[] nodiRotta) {
		listRouteNodes = new ArrayList<Integer>();
		this.index = indexCounter++;
		for (int num_nodo : nodiRotta) {
			listRouteNodes.add(num_nodo);
			if (num_nodo != Route.depotIndex) listAllNodes.get(num_nodo).route = this;
		}
	}
	

	Route(Route rotta) {
		listRouteNodes = new ArrayList<Integer>();
		this.index = indexCounter++;
		for (int num_nodo : rotta.listRouteNodes) {
			listRouteNodes.add(num_nodo);
			if (num_nodo != Route.depotIndex) listAllNodes.get(num_nodo).route = this;
		}
	}

	Route(List<Integer> rotta) {
		listRouteNodes = new ArrayList<Integer>();
		this.index = indexCounter++;
		for (int num_nodo : rotta) {
			listRouteNodes.add(num_nodo);
			if (num_nodo != Route.depotIndex) listAllNodes.get(num_nodo).route = this;
		}
	}
    */
	void add(Integer num_nodo) {
		// assegno il nodo alla rotta ...
		listRouteNodes.add(num_nodo);
		// .. ma anche la rotta al nodo, per sapere in ogni momento su quel nodo, quale
		// rotta insiste.
		if (num_nodo != Route.depotIndex) listAllNodes.get(num_nodo).route = this;
	}

	void add(List<Integer> rotta) {
		for (int num_nodo : rotta) {
			listRouteNodes.add(num_nodo);
			if (num_nodo != Route.depotIndex) listAllNodes.get(num_nodo).route = this;
		}
	}

	String getKey() {
		String key = "[";
		for (int num_nodo : listRouteNodes) {
			if (!key.equals("["))
				key += ",";
			key += num_nodo;
		}
		return key + "]";
	}

	@Override
	public String toString() {
		return getKey();
	}

	int getpI() {
		return listRouteNodes.get(listRouteNodes.size() - 2);
	}

	int getsJ() {
		return listRouteNodes.get(1);
	}

	public int getNumStop() {
		return listRouteNodes.size() - 2;
	}

	static void setListAllNodi(HashMap<Integer, Node> listAllNode) {
		Route.listAllNodes = listAllNode;
	}

	static void setDistanceMatrix(double[][] distanceMatrix) {
		Route.distanceMatrix = distanceMatrix;
	}

	public double getCost() {
		int i = -1, j = -1;
		double costo = 0;
		for (int nodo : this.listRouteNodes) {
			j = nodo;
			if (i != -1) {
				costo += distanceMatrix[i][j];
			}
			i = j;
		}
		return costo;
	}


	static void setIndexDepot(int depotIndex) {
		Route.depotIndex = depotIndex;
	}

	/*
	static double getCapacity(Object[] nodiRotta1) {
		double capacita = 0;
		for (Object nodo : nodiRotta1) {
			if ((Integer) nodo != depotIndex) capacita += listAllNodes.get(nodo).domanda;

		}
		return capacita;
	}
	*/

	public double getTotalDemand() {
		double demand = 0;
		for (int id_nodo : listRouteNodes) {
			if (id_nodo != depotIndex)
				demand += listAllNodes.get(id_nodo).domanda;
		}
		return demand;
	}

	public int size() {
		return listRouteNodes.size();
	}
	
	public Collection<Node> getRouteNodes() {
		ArrayList<Node> lista=new ArrayList<Node>();
		for(int nodo:listRouteNodes) lista.add(listAllNodes.get(nodo));
		return lista;
	}
}
