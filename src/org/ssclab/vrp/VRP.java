package org.ssclab.vrp;

import java.util.List;
import java.util.HashMap;

/**
 * Class to solve the Vehicle Routing Problem (VRP).
 */
public class VRP {
	private double[][] distanceMatrix;
	private int numberOfVehicles;
	private int numberOfIterations;
	private int maxStopsForVehicle;
	private boolean balanceStops = false;
	private int depotIndex;

	/**
	 * Constructor for the VRPSolver class.
	 *
	 * @param distanceMatrix   the distance/cost matrix between nodes.
	 * @param numberOfVehicles the number of available vehicles.
	 */
	public VRP(int numberOfVehicles, double[][] distanceMatrix) {

		// CONTROLLARE MASSI,O NUMERO DI VEICOLI

		this.distanceMatrix = distanceMatrix;
		this.numberOfVehicles = numberOfVehicles;
		this.numberOfIterations = 1000; // Default value
		this.depotIndex = 0; // Default value
		this.maxStopsForVehicle = Integer.MAX_VALUE;

	}

	/**
	 * Sets the number of iterations for the algorithm.
	 *
	 * @param numberOfIterations the number of iterations tra una soluzione e la
	 *                           determinazione di una successiva migliore.
	 */
	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
	}

	/**
	 * Balances the number of stops among vehicles.
	 * <p>
	 * If the {@code balance} parameter is {@code true}, the stops will be evenly
	 * distributed among the available vehicles. If {@code balance} is
	 * {@code false}, the method will not perform any balancing.
	 *
	 * @param balance a boolean indicating whether to balance stops among vehicles
	 */
	public void balanceStopsAmongVehicles(boolean balance) {
		this.balanceStops = balance;
	}

	/**
	 * Sets the index of the depot node.
	 *
	 * @param depotIndex the index of the depot in the distance matrix.
	 */
	public void setDepotIndex(int depotIndex) {
		this.depotIndex = depotIndex;
	}

	/**
	 * Executes the VRP algorithm and returns the results.
	 *
	 * @return a VRPResult object containing the vehicle routes and the total cost.
	 */
	public VRPResult solve() {
		// Implementation of the VRP algorithm
		// This can include initialization, iterations, and final solution

		// VERIFICARE CHE LA MATRICE SIA QUADRATA
		System.out.println("Partenza:");
		Engine engine = new Engine(distanceMatrix, depotIndex, numberOfVehicles);

		HashMap<Integer, Node> nodi = new HashMap<Integer, Node>();
		for (int i = 0; i < distanceMatrix.length; i++) {
			nodi.put(i, new Node(i, 0));
		}

		Route.setListAllNodi(nodi);
		Route.setDistanceMatrix(distanceMatrix);

		// Calcola i guadagni
		List<Edge> archiConGuadagni = engine.calcolaGuadagni();

		for (Edge arco : archiConGuadagni) {
			System.out.println(arco + " sij:" + arco.guadagno);
		}

		double totalCost = 0;
		int iterazioni = numberOfIterations;

		// calcolo il valore di equidistribuzione del numero delle fermate tra i veicoli
		// e lo imposto come valore predefinito di fermate
		int stopPerVeicoliCorrente = (int) Math.ceil((distanceMatrix.length - 1.0) / numberOfVehicles);
		if (balanceStops)
			maxStopsForVehicle = stopPerVeicoliCorrente + 1;
		// se il numero di fermate impostate dall'utente e' minore di quello di
		// equidistribuzione
		// imposto come quello voluto dall'utente come valore. Altrimenti lascio quello
		// di
		// equidistribuzione per calcolare una soluzione con tale vincolo
		if (stopPerVeicoliCorrente >= maxStopsForVehicle)
			stopPerVeicoliCorrente = maxStopsForVehicle;
		// Costruisce i percorsi iniziali
		HashMap<Integer, Route> percorsi = engine.costruisciPercorsi(nodi, archiConGuadagni, stopPerVeicoliCorrente);

		totalCost = engine.getCostoTotale(percorsi);

		do {
			System.out.println("Dimensione STOP CORRENTE:" + stopPerVeicoliCorrente);
			List<Edge> newArchiConGuadagni = engine.generaNuovaListaOrdinata(archiConGuadagni);
			/*
			 * for(Edge arco:newArchiConGuadagni) { System.out.println( arco
			 * +"new sij:"+arco.guadagno); }
			 */
			HashMap<Integer, Route> new_percorsi = engine.costruisciPercorsi(nodi, newArchiConGuadagni,
					stopPerVeicoliCorrente);
			double new_costo_totale = engine.getCostoTotale(new_percorsi);
			if (new_costo_totale < totalCost) {
				archiConGuadagni = newArchiConGuadagni;
				percorsi = new_percorsi;
				totalCost = new_costo_totale;
				iterazioni = numberOfIterations;
			} else
				iterazioni--;

			// System.out.println( "iterazione" +iterazioni);
			if (iterazioni == 0) {
				if (stopPerVeicoliCorrente < maxStopsForVehicle) {
					stopPerVeicoliCorrente = maxStopsForVehicle;
					iterazioni = numberOfIterations;
				}
			}
		} while (iterazioni != 0);

		for (int keyRoute : percorsi.keySet()) {
			Route rotta = percorsi.get(keyRoute);
			System.out.println(rotta.getKey() + " :: " + rotta.getCost());
		}

		// swapSemplice(percorsi);
		totalCost = engine.getCostoTotale(percorsi);

		// Logic to populate routes and totalCost with actual results...
		return new VRPResult(null, numberOfVehicles);
	}

	/**
	 * Sets the maximum number of stops per vehicle.
	 *
	 * @param maxStopsPerVehicle the maximum number of stops each vehicle can make.
	 */
	public void setMaxStopsPerVehicle(int maxStopsPerVehicle) {
		this.maxStopsForVehicle = maxStopsPerVehicle;
	}

	public static void main(String[] args) {

		double[][] distanze = { { 0, 548, 776, 696, 582, 274, 502, 194, 308, 194, 536, 502, 388, 354, 468, 776, 662 },
				{ 548, 0, 684, 308, 194, 502, 730, 354, 696, 742, 1084, 594, 480, 674, 1016, 868, 1210 },
				{ 776, 684, 0, 992, 878, 502, 274, 810, 468, 742, 400, 1278, 1164, 1130, 788, 1552, 754 },
				{ 696, 308, 992, 0, 114, 650, 878, 502, 844, 890, 1232, 514, 628, 822, 1164, 560, 1358 },
				{ 582, 194, 878, 114, 0, 536, 764, 388, 730, 776, 1118, 400, 514, 708, 1050, 674, 1244 },
				{ 274, 502, 502, 650, 536, 0, 228, 308, 194, 240, 582, 776, 662, 628, 514, 1050, 708 },
				{ 502, 730, 274, 878, 764, 228, 0, 536, 194, 468, 354, 1004, 890, 856, 514, 1278, 480 },
				{ 194, 354, 810, 502, 388, 308, 536, 0, 342, 388, 730, 468, 354, 320, 662, 742, 856 },
				{ 308, 696, 468, 844, 730, 194, 194, 342, 0, 274, 388, 810, 696, 662, 320, 1084, 514 },
				{ 194, 742, 742, 890, 776, 240, 468, 388, 274, 0, 342, 536, 422, 388, 274, 810, 468 },
				{ 536, 1084, 400, 1232, 1118, 582, 354, 730, 388, 342, 0, 878, 764, 730, 388, 1152, 354 },
				{ 502, 594, 1278, 514, 400, 776, 1004, 468, 810, 536, 878, 0, 114, 308, 650, 274, 844 },
				{ 388, 480, 1164, 628, 514, 662, 890, 354, 696, 422, 764, 114, 0, 194, 536, 388, 730 },
				{ 354, 674, 1130, 822, 708, 628, 856, 320, 662, 388, 730, 308, 194, 0, 342, 422, 536 },
				{ 468, 1016, 788, 1164, 1050, 514, 514, 662, 320, 274, 388, 650, 536, 342, 0, 764, 194 },
				{ 776, 868, 1552, 560, 674, 1050, 1278, 742, 1084, 810, 1152, 274, 388, 422, 764, 0, 798 },
				{ 662, 1210, 754, 1358, 1244, 708, 480, 856, 514, 468, 354, 844, 730, 536, 194, 798, 0 }, };
		/*
		 * double[][] distanze = { {0, 5, 9, 6, 8, 10, 999}, {6, 0, 9, 999, 999, 999,
		 * 6}, {10, 7, 0, 13, 999, 999, 10}, {7, 999, 11, 0, 7, 999, 999}, {999, 999,
		 * 999, 7, 0, 14, 999}, {11, 6, 999, 999, 15, 0, 999}, {999, 6, 12, 999, 999, 5,
		 * 0} };
		 */

		VRP vrp = new VRP(4, distanze);
		// vrp.setNumberOfIterations(2);
		// vrp.setMaxStopsPerVehicle(4);
		vrp.balanceStopsAmongVehicles(true);
		vrp.solve();

	}

}
