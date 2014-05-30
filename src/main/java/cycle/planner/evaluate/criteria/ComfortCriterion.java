package cycle.planner.evaluate.criteria;

import cvut.fel.nemetma1.dataStructures.CycleEdge;

public class ComfortCriterion implements Criterion {
	
	private final static double heuristicMultiplier = 0.5;
	private final double weight;
	
	public ComfortCriterion(double weight) {
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
		return base*edge.getEvaluationDetails().getComfortMultiplier();
	}
	
	@Override
	public double evaluateWithWeight(CycleEdge edge, double base) {
		return evaluate(edge, base)*weight;
	}
}
