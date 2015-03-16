package cz.agents.cycleplanner.criteria;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.evaluate.EvaluationDetails;
import cz.agents.cycleplanner.evaluate.Evaluator;

public class TravelTimeCriterion implements Criterion {
	
	private final static double heuristicMultiplier = 1;
	private final double weight;
	
	public TravelTimeCriterion(double weight) {
		super();
		this.weight = weight;
	}
	
	@Override
	public double getHeuristicValue() {	
		return heuristicMultiplier;
	}
	
	@Override
	public double getWeightedHeuristicValue() {
		return getHeuristicValue()*weight;
	}

	@Override
	public double evaluate(CycleEdge edge, double base) {		
		return base;
	}
	
	public static double evaluateWithSpeed(CycleEdge edge, double oneOverAverageSpeedMetersPerSecond) {
		
		EvaluationDetails evaluationDetails = edge.getEvaluationDetails();
		if (evaluationDetails == null) {
			
			evaluationDetails = Evaluator.createEvaluationDetails(edge);
			edge.setEvaluationDetails(evaluationDetails);
		}
		
		return evaluationDetails.getTravelTimePrecomputation() * oneOverAverageSpeedMetersPerSecond + evaluationDetails.getTravelTimeSlowdownConstant();
	}
	
	@Override
	public double evaluateWithWeight(CycleEdge edge, double base) {
		return evaluate(edge, base)*weight;
	}

}
