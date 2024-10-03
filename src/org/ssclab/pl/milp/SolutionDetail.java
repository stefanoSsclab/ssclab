package org.ssclab.pl.milp;

/**
 * Enumeration {@code SolutionDetail} defines optional parts of the solution that can be included 
 * in the JSON output when solving a Linear Programming (LP) or Mixed-Integer Linear Programming (MILP) problem.
 * 
 * Each value of the enumeration corresponds to a specific part of the solution that may be added to 
 * the JSON representation. These details allow for greater customization of the information returned.
 * 
 * <ul>
 *   <li>{@link #INCLUDE_BOUNDS}: Includes the upper and lower bounds of the variables in the solution.</li>
 *   <li>{@link #INCLUDE_RELAXED}: Adds the relaxed solution to the JSON output, in addition to the integer solution.</li>
 *   <li>{@link #INCLUDE_CONSTRAINT}: Includes the Left-Hand Side (LHS) and Right-Hand Side (RHS) values of the constraints in the solution.</li>
 *   <li>{@link #INCLUDE_META}: Provides metadata about the optimization process, such as execution time, number of threads, number of iterations, etc.</li>
 *   <li>{@link #INCLUDE_TYPEVAR}: Indicates the original type of each variable in the solution (e.g., integer, binary, continuous).</li>
 * </ul>
 * 
 * This enumeration is used as an argument in the {@code getSolutionAsJson} method, enabling flexibility in the information presented in the JSON format.
 */
public enum SolutionDetail {
    /**
     * Includes the upper and lower bounds for each variable in the solution.
     */
    INCLUDE_BOUNDS,

    /**
     * Includes the relaxed solution, in addition to the integer solution.
     * The relaxed solution refers to the linear relaxation of the MILP problem.
     */
    INCLUDE_RELAXED,

    /**
     * Adds the LHS (Left-Hand Side) and RHS (Right-Hand Side) values for the problem's constraints.
     */
    INCLUDE_CONSTRAINT,

    /**
     * Provides meta-information about the optimization process, such as execution time, number of iterations, 
     * number of threads, and other details.
     */
    INCLUDE_META,

    /**
     * Includes the original type of each variable (e.g., integer, binary, continuous).
     */
    INCLUDE_TYPEVAR
}

