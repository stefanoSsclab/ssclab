package org.ssclab.vrp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

final class Engine {
	private double[][] distanze;
	private int depotIndex;
	private int numberOfVehicles;
	private Random random;


	// (distanceMatrix, depotIndex, numberOfVehicles);
	Engine(double[][] distanceMatrix, int depotIndex, int numberOfVehicles) {
		this.distanze = distanceMatrix;
		this.depotIndex = depotIndex;
		this.numberOfVehicles = numberOfVehicles;
		this.random = new Random();
	}
	
	
	/**
	 * Calcola i guadagni relativi agli archi (i,j) come ci0 +c0j - cij
	 * 
	 * @return la lista degli archi con guadagni, ordinati in base ai guadagni stessi
	 */

	List<Edge> calcolaGuadagni() {
		double s_ij = 0;
		List<Edge> listaArchi = new ArrayList<Edge>();
		for (int i = 0; i < distanze.length; i++) {
			for (int j = 0; j < distanze.length; j++) {
				if (i != j && i != depotIndex && j != depotIndex) {
					s_ij = distanze[i][depotIndex] + distanze[depotIndex][j] - distanze[i][j];
					listaArchi.add(new Edge(i, j, s_ij));
				}
			}
		}
		Collections.sort(listaArchi, Comparator.comparingDouble((Edge a) -> -a.guadagno));
		return listaArchi;
	}

	HashMap<Integer, Route> costruisciPercorsi(HashMap<Integer, Node> nodi, List<Edge> archi_ordinati_per_sij,
			int maxStopsPerVehicle) {
		HashMap<Integer, Route> percorsi = new HashMap<Integer, Route>();
		// Assegna ogni cliente a un percorso individuale iniziale con il deposito
		Route percorso = null;
		for (int i = 0; i < nodi.size(); i++) {
			if (i != depotIndex) {
				percorso = new Route();
				percorso.add(depotIndex);
				percorso.add(nodi.get(i).numNode);
				percorso.add(depotIndex);
				percorsi.put(percorso.index, percorso);
			}
		}

		// Itera attraverso la lista ordinata dei guadagni
		Route percorso_i, percorso_j, nuovoPercorso;
		for (Edge arco : archi_ordinati_per_sij) {
			// System.out.println("testo arco : "+arco);
			percorso_i = nodi.get(arco.i).route;
			percorso_j = nodi.get(arco.j).route;
			// System.out.println("trovati percorsi: "+percorso_i.getKey() +" -
			// "+percorso_j.getKey());
			if (percorso_i.index == percorso_j.index) {
				// System.out.println("continue ");
				continue;
			}

			int numeroFermateNew = percorso_i.getNumStop() + percorso_j.getNumStop();
			// System.out.println("capacita totale' "+capacitaNuovoPercorso);
			if (numeroFermateNew <= maxStopsPerVehicle) {
				// System.out.println("capacita' OK ");
				nuovoPercorso = combinaPercorsi(percorso_i, percorso_j, arco.i, arco.j, nodi);
				if (nuovoPercorso != null) {
					percorsi.remove(percorso_i.index);
					percorsi.remove(percorso_j.index);
					percorsi.put(nuovoPercorso.index, nuovoPercorso);
				}
			}
			if (percorsi.size() == numberOfVehicles) {
				// System.out.println("raggiunto numero di veicoli");
				break;
			}
		}
		return percorsi;
	}
	
	/**
	 * Costruisce tutti i percorsi costituenti la soluzione , partendo daalla
	 * lista degli archi ordinati con guadagni
	 * 
	 * @param nodi
	 * @param archiOrdinatiSij
	 * @param vehicleCapacities
	 * @return La soluzione con l'insieme delle rotte 
	 */

	Solution costruisciPercorsi(HashMap<Integer, Node> nodi, List<Edge> 
	                           archiOrdinatiSij, double[] vehicleCapacities) {
		Solution percorsi = new Solution();
		// Assegna ogni cliente a un percorso individuale iniziale con il deposito
		Route rotta = null;
		for (int i = 0; i < nodi.size(); i++) {
			if (i != depotIndex) {
				rotta = new Route();
				rotta.add(depotIndex);
				rotta.add(nodi.get(i).numNode);
				rotta.add(depotIndex);
				percorsi.put(rotta.index, rotta);
			}
		}

		// Itera attraverso la lista ordinata dei guadagni
		Route rotta_i, rotta_j, nuovaRotta;
		double domandaNuovoPercorso=0;
		boolean valida = false;
		for (Edge arco : archiOrdinatiSij) {
			// System.out.println("testo arco : "+arco);
			rotta_i = nodi.get(arco.i).route;
			rotta_j = nodi.get(arco.j).route;
			// System.out.println("trovati percorsi: "+percorso_i.getKey() +" -
			// "+percorso_j.getKey());
			if (rotta_i.index == rotta_j.index) {
				// System.out.println("continue ");
				continue;
			}

			domandaNuovoPercorso = rotta_i.getTotalDemand() + rotta_j.getTotalDemand();
			// System.out.println("capacita totale' "+capacitaNuovoPercorso);
			

			//se la capacita' dei veicoli e' la stessa devo solo verificare, 
			//per accettare una soluzione, che la domanda del nuovo percorso
			//sia minore alla capacita del veicolo (tutti uguali) 
			if (vehicleCapacities.length == 1)
				valida = domandaNuovoPercorso <= vehicleCapacities[0];
			else
				valida = verificaCapacitaRotta(percorsi, rotta_i.index, rotta_j.index, 
						domandaNuovoPercorso, vehicleCapacities);

			if (valida) {
				// System.out.println("capacita' OK ");
				nuovaRotta = combinaPercorsi(rotta_i, rotta_j, arco.i, arco.j, nodi);
				if (nuovaRotta != null) {
					percorsi.remove(rotta_i.index);
					percorsi.remove(rotta_j.index);
					percorsi.put(nuovaRotta.index, nuovaRotta);
				}
			}
			if (percorsi.size() == numberOfVehicles) {
				// System.out.println("raggiunto numero di veicoli");
				break;
			}
		}
		return percorsi;
	}

	Route combinaPercorsi(Route percorso_i, Route percorso_j, int nodo_i, int nodo_j, HashMap<Integer, Node> nodi) {
		int nodop_j = percorso_j.getsJ();
		int nodop_i = percorso_i.getpI();
		if (nodop_j == nodo_j && nodop_i == nodo_i) {
			// System.out.println("nodi da unire : ("+nodop_i+","+nodop_j+")");
			// System.out.println("unisco: "+percorso_i.getKey() +" -
			// "+percorso_j.getKey());
			return new Route(percorso_i, percorso_j);
		}
		return null;
	}

	int generaNumero() {
		// int randomNumber = random.nextInt(5) + 2;
		//int randomNumber = random.nextInt(5) + 2;
		// System.out.println("Numero casuale tra " + min + " e " + max + ": " +
		// randomNumber);
		return random.nextInt(5) + 2;
	}

	List<Edge> generaNuovaListaOrdinata(List<Edge> archiOrdinati) {
		// ArrayList<Edge> lista_copia = new ArrayList<Edge>(archi_ordinati);
		LinkedList<Edge> copiaListaOrdinata = new LinkedList<Edge>(archiOrdinati);
		ArrayList<Edge> newLista = new ArrayList<Edge>();
		Edge arco = null;
		ListIterator<Edge> listIterator = null;
		int min = 0, numero_casuale = 0;
		double randomMontante = 0.0, somma_montante = 0.0, cumulataGuadagni = 0.0;
		do {
			numero_casuale = generaNumero();
			somma_montante = 0.0;
			// System.out.println("generato numero intero:"+numero_casuale);
			min = Math.min(numero_casuale, copiaListaOrdinata.size());
			// System.out.println("minimo numero intero:"+min);

			listIterator = copiaListaOrdinata.listIterator();
			for (int i = 0; i < min; i++) {
				somma_montante += listIterator.next().guadagno;
			}

			// System.out.println("somma montante:"+somma_montante);
			boolean isSommaZero = false;
			if (somma_montante <= 0) {
				somma_montante = min;
				isSommaZero = true;
			}
			randomMontante = Math.random() * somma_montante;
			// System.out.println("casuale intra montante:"+randomMontante);
			cumulataGuadagni = 0;
			listIterator = copiaListaOrdinata.listIterator();

			for (int i = 0; i < min; i++) {
				arco = listIterator.next();
				if (!isSommaZero) cumulataGuadagni += arco.guadagno;
				else cumulataGuadagni = i + 1;
				
				if (cumulataGuadagni > randomMontante) {
					// System.out.println("aggiunto elemento:"+i);
					newLista.add(arco);
					listIterator.remove();
					break;
				}
			}

			/*
			 * 
			 * System.out.println("dimensione old:"+lista_copia.size());
			 * System.out.println("dimensione new:"+new_lista.size()); try {
			 * Thread.sleep(100); } catch (InterruptedException e) {
			 * 
			 * e.printStackTrace(); }
			 */
		} 
		while (copiaListaOrdinata.size() != 0);
		return newLista;
	}

	 double getCostoTotale(HashMap<Integer, Route> percorsi) {
		// System.out.println("Output dei percorsi iniziali");
		double costoTotale = 0;
		for (Route percorso : percorsi.values()) {
			costoTotale += percorso.getCost();
		}
		return costoTotale;
	}

	boolean verificaCapacitaRotta(Solution percorsiEsistenti, int indice_i, int indice_j,
			double capacitaNuovaRotta, double capacitaVeicoli[]) {

		//System.out.println("inizio  i:" + indice_i + "  j:" + indice_j);
		List<Double> sortedList = new ArrayList<>();
		sortedList.add(capacitaNuovaRotta);

		// abbiamo che i percorsi sono sempre piu numerosi dei veicoli, quindi il ciclo
		// che comanda e quello dei percorsi.

		for (Route rotta : percorsiEsistenti.values()) {
			if (rotta.index != indice_i && rotta.index != indice_i) {
				sortedList.add(rotta.getTotalDemand());
			} 
			//else System.out.println("trovate una delle due :" + rotta.index + "  cap:" + rotta.getTotalDemand());
		}

		Collections.sort(sortedList, Collections.reverseOrder());

		int size = capacitaVeicoli.length;
		int k = size - 1;
		for (double capacityRoute : sortedList) {
			if (k < 0) return true;
			else if (capacityRoute > capacitaVeicoli[k]) {
				//System.out.println("dentro capRotta:" + capacityRoute + "   capVehicle:" + capacitaVeicoli[k]);
				return false;
			}
			//System.out.println("capRotta:" + capacityRoute + "   capVehicle:" + capacitaVeicoli[k]);
			k--;
		}
		return true;
	}
}
