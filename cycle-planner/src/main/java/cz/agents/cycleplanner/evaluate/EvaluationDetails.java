package cz.agents.cycleplanner.evaluate;

import java.io.Serializable;

public class EvaluationDetails implements Serializable {
	
	private double travelTimePrecomputation; // TODO rename
	private double travelTimeSlowdownConstant;	
	private double comfortMultiplier;
	private double quietnessMultiplier;
	private double flatnessMultiplier;
	
	public EvaluationDetails(double travelTimePrecomputation,
			double travelTimeSlowdonwConstant, double comfortMultiplier,
			double quietnessMultiplier, double flatnessMultiplier) {
		
		this.travelTimePrecomputation = travelTimePrecomputation;
		this.travelTimeSlowdownConstant = travelTimeSlowdonwConstant;
		this.comfortMultiplier = comfortMultiplier;
		this.quietnessMultiplier = quietnessMultiplier;
		this.flatnessMultiplier = flatnessMultiplier;
	}

	public double getTravelTimePrecomputation() {
		return travelTimePrecomputation;
	}

	public double getTravelTimeSlowdownConstant() {
		return travelTimeSlowdownConstant;
	}

	public double getComfortMultiplier() {
		return comfortMultiplier;
	}

	public double getQuietnessMultiplier() {
		return quietnessMultiplier;
	}

	public double getFlatnessMultiplier() {
		return flatnessMultiplier;
	}
}
