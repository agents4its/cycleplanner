package cz.agents.cycleplanner.aStar.aima;

import java.util.List;

import aima.core.search.framework.HeuristicFunction;
import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.criteria.Criterion;
import cz.agents.cycleplanner.criteria.FlatnessCriterion;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.evaluate.Evaluator;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;

public class Heuristic implements HeuristicFunction {
	
	private CycleNode goal;
	private double averageSpeedMetersPerSecond;
	private double flatnessBase;
	private Profile profile;
	
	public Heuristic(CycleNode goal, Profile profile, double averageSpeedKMpH) {
		
		this.goal = goal;
		this.averageSpeedMetersPerSecond = averageSpeedKMpH/3.6;
		this.profile = profile;
		this.flatnessBase = Evaluator.PERCEPTION_UPHILL_MULTIPLIER/this.averageSpeedMetersPerSecond;
	}
	
	@Override
	public double h(Object state) {
		CycleNode node = (CycleNode) state;
		
		double directDistance = EdgeUtil.computeDirectDistanceInM(node, goal);
		
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
