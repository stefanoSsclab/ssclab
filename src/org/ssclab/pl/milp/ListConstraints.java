package org.ssclab.pl.milp;

import java.util.ArrayList;

/**
 * The ListConstraints class represents a collection of constraints for a Mixed Integer Linear Programming (MILP) problem.
 * This class provides functionality to store and manage the constraints in a list.
 * 
 * <p>Each constraint is represented by an instance of the {@link Constraint} class.
 * 
 * <p>This class is immutable and final to ensure that the structure of the constraint list is controlled.
 */

public final class ListConstraints {
	  /** 
     * A list that holds the constraints of the MILP problem.
     */
	private ArrayList<Constraint> listConstraint;
	
	 /**
     * Constructs an empty ListConstraints object.
     * Initializes the list of constraints as an empty {@link ArrayList}.
     */
	public ListConstraints() {
		listConstraint=new ArrayList<Constraint>();
	}
	
	/**
     * Adds a new constraint to the list of constraints.
     *
     * @param constraint the {@link Constraint} object to be added
     */
	public void add(Constraint constraint) {
		listConstraint.add(constraint);
	}
	
	 /**
     * Returns the list of constraints.
     *
     * @return an {@link ArrayList} containing the constraints
     */

	public ArrayList<Constraint> getListConstraint() {
		return listConstraint;
	}
	
}
