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
			throw new InvalidCVRPInputException("The number of vehicles must be greater than zero.");
		}
		if (distanceMatrix == null || distanceMatrix.length == 0) {
			throw new InvalidCVRPInputException("The distance matrix cannot be null or empty.");
		}
		if (demands == null || demands.length == 0) {
			throw new InvalidCVRPInputException("The array related to customer queries cannot be null or empty.");
		}
		for (double demand : demands) {
			if (demand < 0) {
				throw new InvalidCVRPInputException("Customer demand cannot have negative values.");
			}
		}
		if (vehicleCapacities.length != 1)
			if (vehicleCapacities == null || vehicleCapacities.length != numberOfVehicles) {
				throw new InvalidCVRPInputException("The size of the array related to vehicle capacities must be equal to the number of vehicles.");
			}

		for (double capacity : vehicleCapacities) {
			if (capacity < 0) {
				throw new InvalidCVRPInputException("The capacities of the vehicles cannot have negative values.");
			}
		}
		int matrixSize = distanceMatrix.length;
		for (double[] row : distanceMatrix) {
			if (row.length != matrixSize) {
				throw new InvalidCVRPInputException("The distance matrix must be square (NxN).");
			}
		}
		//variabile per calcolare se un nodo non puo superare la capacita di un veicolo 
		double maxVehicleCapacity = Double.NEGATIVE_INFINITY;
		
		for (double capacity : vehicleCapacities) {
			if (capacity > maxVehicleCapacity) {
				maxVehicleCapacity = capacity;
			}
		}

		for (double demand : demands) {
			if (demand > maxVehicleCapacity) {
				throw new InvalidCVRPInputException(
						"The demand of a node cannot exceed the capacity of any vehicle.");
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
	}

	/**
	 * Sets the number of iterations for the algorithm.
	 *
	 * @param numberOfIterations the number of iterations 
	 */
	public void setNumberOfIterations(int numberOfIterations) {
		this.numberOfIterations = numberOfIterations;
		if (numberOfIterations < 0) {
			throw new InvalidCVRPInputException("Number of iterations have negative values.");
		}
	}

	/**
	 * Sets the index of the depot node.
	 *
	 * @param depotIndex the index of the depot in the distance matrix.
	 */
	public void setDepotIndex(int depotIndex) {
		this.depotIndex = depotIndex;
		if (depotIndex < 0 || depotIndex >= distanceMatrix.length) {
			throw new InvalidCVRPInputException("The depot index if out of range.");
		}
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
		//System.out.println("Costo:"+totalCost);
		double newtotalCost=0;
		do {
			newArchiConGuadagni = engine.generaNuovaListaOrdinata(archiConGuadagni);
			newSolution = engine.costruisciPercorsi(nodi, newArchiConGuadagni, vehicleCapacities);
			newtotalCost = newSolution.getCostoTotale();
			//System.out.println("Costo new:"+newtotalCost   +"  iter:"+iterazioni);
			if (newtotalCost < totalCost) {
				archiConGuadagni = newArchiConGuadagni;
				solution = newSolution;
				totalCost = newtotalCost;
				//iterazioni = numberOfIterations;
			} 
			iterazioni--;	
		} 
		while (iterazioni != 0);
		return new VRPResult(solution,numberOfVehicles);
	}
}
