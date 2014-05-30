package cycle.planner.aStar;

import java.util.List;

import cvut.fel.nemetma1.dataStructures.CycleNode;
import cycle.planner.evaluate.criteria.Criterion;
import cycle.planner.evaluate.criteria.FlatnessCriterion;
import cycle.planner.evaluate.evaluator.Evaluator;
import aima.core.search.framework.HeuristicFunction;

public class Heuristic implements HeuristicFunction {
	
	private CycleNode goal;
	private double averageSpeedMetersPerSecond;
	private double flatnessBase;
	private Profile profile;
	
	public Heuristic(CycleNode goal, Profile profile, double averageSpeed) {
		
		this.goal = goal;
		this.averageSpeedMetersPerSecond = averageSpeed;
		this.profile = profile;
		this.flatnessBase = Evaluator.PERCEPTION_UPHILL_MULTIPLIER/averageSpeed;
	}
	
	@Override
	public double h(Object state) {
		CycleNode node = (CycleNode) state;
		
		double x = node.getProjectedLatitude() - goal.getProjectedLatitude();
		double y = node.getProjectedLongitude() - goal.getProjectedLongitude();
		double directDistance = Math.sqrt(x*x + y*y);
		
		double traveTimeHeuristic = (directDistance / (averageSpeedMetersPerSecond*Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER));
		double result = 0;
		
		List<Criterion> criteria = profile.getCriteria();
		
		for (Criterion criterion : criteria) {
			if (criterion instanceof FlatnessCriterion) {
				result += flatnessBase*computeRisesToDestination(node)*criterion.getWeightedHeuristicValue();
			} else {
				result += traveTimeHeuristic*criterion.getWeightedHeuristicValue();
			}
		}
				
		return result;
	}
	
	private double computeRisesToDestination(CycleNode node) {
		double elevation = node.getElevation() - goal.getElevation();
		
		if (elevation > 0) return elevation;
		return 0;
	}

}
