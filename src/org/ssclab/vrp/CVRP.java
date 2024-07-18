package org.ssclab.vrp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CVRP {
	private double[][] distanceMatrix;
	private int numberOfVehicles;
	private int numberOfIterations;
	private int depotIndex;
	private double[] demands;
	private double[] vehicleCapacities;

	/**
	 * Constructor for the VRP class.
	 *
	 * @param distanceMatrix   the distance/cost matrix between nodes.
	 * @param numberOfVehicles the number of available vehicles.
	 */
	public CVRP(int numberOfVehicles, double[][] distanceMatrix, double[] demands, double[] vehicleCapacities) {

		// CONTROLLARE MASSI,O NUMERO DI VEICOLI
		// ricontrollare variazione indexdepot su tutti i metodi (capacyty e vari)

		if (numberOfVehicles <= 0) {
			throw new InvalidCVRPInputException("Il numero di veicoli deve essere maggiore di zero.");
		}
		if (distanceMatrix == null || distanceMatrix.length == 0) {
			throw new InvalidCVRPInputException("La matrice delle distanze non puo essere nulla o vuota.");
		}
		if (demands == null || demands.length == 0) {
			throw new InvalidCVRPInputException("L'array delle domande dei clienti non puo essere nullo o vuoto.");
		}
		for (double demand : demands) {
			if (demand < 0) {
				throw new InvalidCVRPInputException("Le domande dei clienti non possono avere valori negativi.");
			}
		}
		if (vehicleCapacities.length != 1)
			if (vehicleCapacities == null || vehicleCapacities.length != numberOfVehicles) {
				throw new InvalidCVRPInputException(
						"La dimensione dell'array delle capacita dei veicoli deve essere uguale al numero di veicoli.");
			}

		for (double capacity : vehicleCapacities) {
			if (capacity < 0) {
				throw new InvalidCVRPInputException("Le capacita dei veicoli non possono avere valori negativi.");
			}
		}
		int matrixSize = distanceMatrix.length;
		for (double[] row : distanceMatrix) {
			if (row.length != matrixSize) {
				throw new InvalidCVRPInputException("La matrice delle distanze deve essere quadrata NxN.");
			}
		}

		double maxVehicleCapacity = Double.NEGATIVE_INFINITY;
		for (double capacity : vehicleCapacities) {
			if (capacity > maxVehicleCapacity) {
				maxVehicleCapacity = capacity;
			}
		}

		for (double demand : demands) {
			if (demand > maxVehicleCapacity) {
				throw new InvalidCVRPInputException(
						"La domanda di un nodo non puo superare la capacita di qualsiasi veicolo.");
			}
		}

		this.distanceMatrix = distanceMatrix;
		this.numberOfVehicles = numberOfVehicles;
		this.numberOfIterations = 1000; // Default value
		this.depotIndex = 0; // Default value
		this.demands = demands;
		this.vehicleCapacities = vehicleCapacities;
		Arrays.sort(this.vehicleCapacities);
	}

	/**
	 * Constructor for the VRPSolver class.
	 *
	 * @param distanceMatrix   the distance/cost matrix between nodes.
	 * @param numberOfVehicles the number of available vehicles.
	 */
	public CVRP(int numberOfVehicles, double[][] distanceMatrix, double[] demands, double vehicleCapacities) {

		this(numberOfVehicles, distanceMatrix, demands, new double[] { vehicleCapacities });
		/*
		 * this.distanceMatrix = distanceMatrix; this.numberOfVehicles =
		 * numberOfVehicles; this.numberOfIterations = 1000; // Default value
		 * this.depotIndex = 0; // Default value this.demands=demands;
		 * this.vehicleCapacities=new double[] {vehicleCapacities};
		 */
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
	 * @throws Exception
	 */
	public VRPResult solve() throws Exception {
		// Implementation of the VRP algorithm
		// This can include initialization, iterations, and final solution

		Engine engine = new Engine(distanceMatrix, depotIndex, numberOfVehicles);

		HashMap<Integer, Node> nodi = new HashMap<Integer, Node>();
		for (int i = 0; i < distanceMatrix.length; i++) {
			nodi.put(i, new Node(i, demands[i]));
		}

		Route.setListAllNodi(nodi);
		Route.setDistanceMatrix(distanceMatrix);
		Route.setIndexDepot(depotIndex);

		// Calcola i guadagni
		List<Edge> archiConGuadagni = engine.calcolaGuadagni(), newArchiConGuadagni=null;
		int iterazioni = numberOfIterations;
		// Costruisce i percorsi iniziali
		Solution solution = engine.costruisciPercorsi(nodi, archiConGuadagni, vehicleCapacities);
		Solution newSolution=null;
		double totalCost = solution.getCostoTotale();
		double newtotalCost=0;
		do {
			newArchiConGuadagni = engine.generaNuovaListaOrdinata(archiConGuadagni);
			newSolution = engine.costruisciPercorsi(nodi, newArchiConGuadagni, vehicleCapacities);
			newtotalCost = newSolution.getCostoTotale();
			if (newtotalCost < totalCost) {
				archiConGuadagni = newArchiConGuadagni;
				solution = newSolution;
				totalCost = newtotalCost;
				//iterazioni = numberOfIterations;
			} 
			else iterazioni--;
		} 
		while (iterazioni != 0);

		/*
		for (int keyRoute : solution.keySet()) {
			Route rotta = solution.get(keyRoute);
			System.out.println(rotta.toString() + " :: costo:" + rotta.getCost() + "  domanda :" + rotta.getTotalDemand());
		}
		System.out.println("costo PRIMA:" + solution.getCostoTotale());
		System.out.println("\n\n");

		totalCost = solution.getCostoTotale();
		System.out.println("costo totale:" + totalCost);
		*/
		return new VRPResult(solution,numberOfVehicles);
	}

	public static void main(String[] args) throws Exception {
		/*
		 * double[][] distanze = { {0, 548, 776, 696, 582, 274, 502, 194, 308, 194, 536,
		 * 502, 388, 354, 468, 776, 662}, {548, 0, 684, 308, 194, 502, 730, 354, 696,
		 * 742, 1084, 594, 480, 674, 1016, 868, 1210}, {776, 684, 0, 992, 878, 502, 274,
		 * 810, 468, 742, 400, 1278, 1164, 1130, 788, 1552, 754}, {696, 308, 992, 0,
		 * 114, 650, 878, 502, 844, 890, 1232, 514, 628, 822, 1164, 560, 1358}, {582,
		 * 194, 878, 114, 0, 536, 764, 388, 730, 776, 1118, 400, 514, 708, 1050, 674,
		 * 1244}, {274, 502, 502, 650, 536, 0, 228, 308, 194, 240, 582, 776, 662, 628,
		 * 514, 1050, 708}, {502, 730, 274, 878, 764, 228, 0, 536, 194, 468, 354, 1004,
		 * 890, 856, 514, 1278, 480}, {194, 354, 810, 502, 388, 308, 536, 0, 342, 388,
		 * 730, 468, 354, 320, 662, 742, 856}, {308, 696, 468, 844, 730, 194, 194, 342,
		 * 0, 274, 388, 810, 696, 662, 320, 1084, 514}, {194, 742, 742, 890, 776, 240,
		 * 468, 388, 274, 0, 342, 536, 422, 388, 274, 810, 468}, {536, 1084, 400, 1232,
		 * 1118, 582, 354, 730, 388, 342, 0, 878, 764, 730, 388, 1152, 354}, {502, 594,
		 * 1278, 514, 400, 776, 1004, 468, 810, 536, 878, 0, 114, 308, 650, 274, 844},
		 * {388, 480, 1164, 628, 514, 662, 890, 354, 696, 422, 764, 114, 0, 194, 536,
		 * 388, 730}, {354, 674, 1130, 822, 708, 628, 856, 320, 662, 388, 730, 308, 194,
		 * 0, 342, 422, 536}, {468, 1016, 788, 1164, 1050, 514, 514, 662, 320, 274, 388,
		 * 650, 536, 342, 0, 764, 194}, {776, 868, 1552, 560, 674, 1050, 1278, 742,
		 * 1084, 810, 1152, 274, 388, 422, 764, 0, 798}, {662, 1210, 754, 1358, 1244,
		 * 708, 480, 856, 514, 468, 354, 844, 730, 536, 194, 798, 0}, };
		 * 
		 * 
		 * 
		 * double[] demands = {0, 1, 1, 2, 4, 2, 4, 8, 8, 1, 2, 1, 2, 4, 4, 8, 8};
		 * double[] vehicleCapacities = {15, 15, 15, 15};
		 * 
		 * 
		 * // double vehicleCapacities =100; /*
		 * 
		 * double[][] distanze = { {0, 5, 9, 6, 8, 10, 999}, {6, 0, 9, 999, 999, 999,
		 * 6}, {10, 7, 0, 13, 999, 999, 10}, {7, 999, 11, 0, 7, 999, 999}, {999, 999,
		 * 999, 7, 0, 14, 999}, {11, 6, 999, 999, 15, 0, 999}, {999, 6, 12, 999, 999, 5,
		 * 0} };
		 * 
		 * 
		 * double[] demands = { 0 ,19 ,21 ,6 ,19 ,7 ,12 ,16 ,6 , 16 , 8 , 14 , 21 , 16 ,
		 * 3 , 22 , 18 , 19 , 1 , 24 , 8 , 12 , 4 , 8 , 24 , 24 , 2 , 20 , 15 , 2 , 14 ,
		 * 9};
		 * 
		 * 
		 */
		double[] demands = { 0, 24, 22, 23, 5, 11, 23, 26, 9, 23, 9, 14, 16, 12, 2, 2, 6, 20, 26, 12, 15, 13, 26, 17, 7,
				12, 4, 4, 20, 10, 9, 2, 9, 1, 2, 2, 12, 14, 23, 21, 13, 13, 23, 3, 6, 23, 11, 2, 7, 13, 10, 3, 6, 13, 2,
				14, 7, 21, 7, 22, 13, 22, 18, 22, 6, 2, 11, 5, 9, 9, 5, 12, 2, 12, 19, 6, 14, 2, 2, 24 };

		// double[] demands =
		// {0,38,51,73,70,58,54,1,98,62,98,25,86,46,27,17,97,74,81,62,59,23,62,66,35,53,18,87,32,4,61,95,23
		// ,15 ,5 ,53 ,97 ,70 ,32 ,27 ,42 ,67 ,76 ,15 ,39 ,14 ,43 ,11 ,93 ,53 ,44 ,80
		// ,87 ,97 ,67 ,72 ,50 ,8 ,58 ,55 ,67 ,89 ,38 ,65 ,3 ,5 ,46 ,100 ,52 ,28 ,96 ,18
		// ,16 ,7 ,73 ,76 ,6 ,64 ,39 ,86 ,70 ,14 ,83 ,96 ,43 ,12 ,73 ,2,21,18,55 ,75 ,68
		// ,100,61 ,24 ,40 ,48 ,51 ,78 ,35 };
		String filename = "c:\\appo\\coordi2.txt"; // Sostituisci con il percorso del tuo file
		List<double[]> coordinates = vrp.VRDPCalc.readCoordinates(filename);
		double[][] distanze = vrp.VRDPCalc.calculateDistanceMatrix(coordinates);
		double vehicleCapacities = 100;

		CVRP vrp = new CVRP(10, distanze, demands, vehicleCapacities);
		// vrp.setNumberOfIterations(2);
		vrp.setNumberOfIterations(1000);
		vrp.solve();

	}

	// SE HO UN NODO CON DOMANDA MAGGIORE DELLA CAPACITA DI UN INTERO VEICOLO >
	// TROEW ERROR

}
