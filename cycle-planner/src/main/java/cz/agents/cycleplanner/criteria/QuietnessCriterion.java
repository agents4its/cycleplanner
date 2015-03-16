package cz.agents.cycleplanner.criteria;

import cz.agents.cycleplanner.dataStructures.CycleEdge;

public class QuietnessCriterion implements Criterion {
	
	private final static double heuristicMultiplier = 0.2;
	private final double weight;
	
	public QuietnessCriterion(double weight) {
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
		return base*edge.getEvaluationDetails().getQuietnessMultiplier();
	}

	@Override
	public double evaluateWithWeight(CycleEdge edge, double base) {
		return evaluate(edge, base)*weight;
	}
}
