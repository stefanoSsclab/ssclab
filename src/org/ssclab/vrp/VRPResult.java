package org.ssclab.vrp;

import java.util.Collection;

/**
 * VRPResult represents the result of the CVRP solving algorithm,
 * containing the calculated routes and the number of vehicles used.
 */
public class VRPResult {
    private Solution solution;
    private double totalCost;
    private int numberOfVehiclesSetByUser=0;

    /**
     * Constructor for the VRPResult class.
     *
     * @param routes the vehicle routes.
     * @param totalCost the total cost of the solution.
     */
    VRPResult(Solution percorsi, int numberOfVehiclesSetByUser) {
        this.solution = percorsi;
        this.totalCost = percorsi.getCostoTotale();
        this.numberOfVehiclesSetByUser=numberOfVehiclesSetByUser;
    }

    /**
     * Returns the vehicle routes.
     *
     * @return a Collection of Route, where each Route represents a vehicle route.
     */
    public Collection<Route> getRoutes() {
        return solution.values();
    }

    /**
     * Returns the total cost of the solution.
     *
     * @return the total cost of the solution.
     */
    public double getTotalCost() {
        return totalCost;
    }
    
    /**
     * Returns the number of vehicles used in the solution.
     *
     * @return The number of vehicles used.
     */
    public int getNumberOfVehiclesUsed() {
        return solution.size();
    }

    /**
     * Returns the number of vehicles set by the user.
     *
     * @return The number of vehicles set by the user.
     */
    public int getNumberOfVehiclesSetByUser() {
        return numberOfVehiclesSetByUser;
    }
    
}
