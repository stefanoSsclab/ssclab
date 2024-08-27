package org.ssclab.vrp;

import java.util.HashMap;
import java.util.List;

public class VRP {
	private double[][] distanceMatrix;
	private int numberOfVehicles;
	private int numberOfIterations;
	private int maxStopsForVehicle;
	private boolean balanceStops;
	private int depotIndex;
	final int MAX_STOP=1234567;

	/**
	 * Constructor for the VRPSolver class.
	 *
	 * @param distanceMatrix   the distance/cost matrix between nodes.
	 * @param numberOfVehicles the number of available vehicles.
	 */
	public VRP(int numberOfVehicles, double[][] distanceMatrix) {

		this.distanceMatrix = distanceMatrix;
		this.numberOfVehicles = numberOfVehicles;
		this.numberOfIterations = 1000; // Default value
		this.depotIndex = 0; // Default value
		this.maxStopsForVehicle = MAX_STOP;
		this.balanceStops=false;
	}

	/**
	 * Sets the number of iterations for the algorithm.
	 *
	 * @param numberOfIterations the number of iterations tra una soluzione e la
	 *                           determinazione di una successiva migliore.
	 */
	public void setNumberOfIterations(int numberOfIterations) {
		if (numberOfIterations < 0) {
			throw new InvalidCVRPInputException("Number of iterations have negative values.");
		}
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
		if (depotIndex < 0 || depotIndex >= distanceMatrix.length) {
			throw new InvalidCVRPInputException("The depot index if out of range.");
		}
		this.depotIndex = depotIndex;
	}
	
	/**
	 * Sets the maximum number of stops per vehicle.
	 *
	 * @param maxStopsPerVehicle the maximum number of stops each vehicle can make.
	 */
	
	public void setMaxStopsPerVehicle(int maxStopsPerVehicle) {
		this.maxStopsForVehicle = maxStopsPerVehicle;
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
		
		int maxStopsForVehicleLocal=maxStopsForVehicle;
		if (balanceStops==true && maxStopsForVehicle!=MAX_STOP)  {
			throw new InvalidCVRPInputException("Solo uno dei parametri tra balanceStopsAmongVehicles e maxStopsForVehicle puo essere impostato dall'utente.");
		}
		Engine engine = new Engine(distanceMatrix, depotIndex, numberOfVehicles);
		
		HashMap<Integer, Node> nodi = new HashMap<Integer, Node>();
		for (int i = 0; i < distanceMatrix.length; i++) {
			nodi.put(i, new Node(i, 0));
		}

		Route.setListAllNodi(nodi);
		Route.setDistanceMatrix(distanceMatrix);
		Route.setIndexDepot(depotIndex);

		// Calcola i guadagni
		List<Edge> archiConGuadagni = engine.calcolaGuadagni(), newArchiConGuadagni=null;
		int iterazioni = numberOfIterations;
			
		// calcolo il valore di equidistribuzione del numero delle fermate tra i veicoli
		// e lo imposto come valore predefinito di fermate
		int fermatePerVeicoliCalcolato = (int) Math.ceil((distanceMatrix.length - 1.0) / numberOfVehicles);
		if (balanceStops) maxStopsForVehicleLocal = fermatePerVeicoliCalcolato ;
				
		// Costruisce i percorsi iniziali
		Solution solution = engine.costruisciPercorsi(nodi, archiConGuadagni, maxStopsForVehicleLocal);
		Solution newSolution=null;
		double totalCost = solution.getCostoTotale();
	
		double newtotalCost=0;
		boolean ottimizzato=false;
		while (iterazioni >= 0) {
			newArchiConGuadagni = engine.generaNuovaListaOrdinata(archiConGuadagni);
			newSolution = engine.costruisciPercorsi(nodi, newArchiConGuadagni, maxStopsForVehicleLocal);
			newtotalCost = newSolution.getCostoTotale();
			//System.out.println("iterazione:"+iterazioni);
			//System.out.println("fermatePerVeicoliCalcolato:"+fermatePerVeicoliCalcolato);
			//System.out.println("maxStopsForVehicleLocal:"+maxStopsForVehicleLocal);
			if ( newtotalCost < totalCost) {
				if((ottimizzato && newSolution.size()==this.numberOfVehicles) || !ottimizzato) {
					archiConGuadagni = newArchiConGuadagni;
					solution = newSolution;
					totalCost = newtotalCost;
				} 
			} 
			iterazioni--;
			
			if(iterazioni==0 && fermatePerVeicoliCalcolato < maxStopsForVehicleLocal && maxStopsForVehicle!=MAX_STOP) {
				iterazioni=numberOfIterations;
				maxStopsForVehicleLocal--;
				ottimizzato=true;
			}
		} 
		return new VRPResult(solution,numberOfVehicles);
	}
}
