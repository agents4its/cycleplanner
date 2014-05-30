package cycle.planner.evaluate.criteria;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cycle.planner.evaluate.evaluator.EvaluationDetails;
import cycle.planner.evaluate.evaluator.Evaluator;

public class FlatnessCriterion implements Criterion {
	
	private final static double heuristicMultiplier = 1;
	private final double weight;
	
	public FlatnessCriterion(double weight) {
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

	/**
	 * @param base - in this criterion base is expected to be one over average cruising speed of rider in meters per second
	 */
	@Override
	public double evaluate(CycleEdge edge, double base) {
		EvaluationDetails evaluationDetails = edge.getEvaluationDetails();
		if (evaluationDetails == null) {
			
			evaluationDetails = Evaluator.createEvaluationDetails(edge);
			edge.setEvaluationDetails(evaluationDetails);
		}
		return evaluationDetails.getFlatnessMultiplier()*base;
	}
	
	@Override
	public double evaluateWithWeight(CycleEdge edge, double base) {
		return evaluate(edge, base)*weight;
	}
}
