package cz.agents.cycleplanner.util;

import cz.agents.cycleplanner.dataStructures.CycleEdge;

public class EnergyConsumption {
	
	/* rolling coefficient */
	private final static double ROLLING_COEFFICIENT = 0.005;
	/* gravitational acceleration */
	private final static double GRAVITATIONAL_ACCELERATION = 9.81;
	/* weight rider+bike+load (kg) */
	private final static double OVERALL_WEIGHT = 100;
	/* frontal area (m^2) */
	private final static double FRONTAL_AREA = .4;
	/* air density (kg/m^3) */
	private final static double AIR_DENSITY = 1.247;
	/* drag coefficient */
	private final static double DRAG_COEFFICIENT = 1d;
	
	/**
	 * Compute energy consumption for given edge in Joules
	 * 
	 * @param edge
	 * @param averageSpeedMetersPerSecond
	 * @return energy in Joules
	 */
	public static double compute(CycleEdge edge, double averageSpeedMetersPerSecond) {
		
		double powerRollingResistance = ROLLING_COEFFICIENT * GRAVITATIONAL_ACCELERATION * OVERALL_WEIGHT;
		// Pdrag = 0.5 * Cd * D * A * (vg + vw)^2
		double powerDrag = 0.5 * DRAG_COEFFICIENT * AIR_DENSITY * FRONTAL_AREA * averageSpeedMetersPerSecond
				* averageSpeedMetersPerSecond;

		double slope = edge.getRises() / edge.getLengthInMetres();
		double powerClimb = GRAVITATIONAL_ACCELERATION * slope * OVERALL_WEIGHT;
				
		return edge.getLengthInMetres() * (powerRollingResistance + powerDrag + powerClimb);
	}
}
