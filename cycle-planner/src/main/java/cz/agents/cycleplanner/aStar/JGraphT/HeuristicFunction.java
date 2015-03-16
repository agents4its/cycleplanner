package cz.agents.cycleplanner.aStar.JGraphT;

import java.util.List;

import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.criteria.Criterion;
import cz.agents.cycleplanner.criteria.FlatnessCriterion;
import cz.agents.cycleplanner.evaluate.Evaluator;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;
import eu.superhub.wp5.plannercore.algorithms.heuristics.Heuristic;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;

public class HeuristicFunction implements Heuristic<TimedNode> {

	private Node goal;
	private double averageSpeedMetersPerSecond;
	private double oneOverTravelTimeDenominator;
	private double flatnessBase;
	private Profile profile;

	public HeuristicFunction(Node goal, Profile profile,
			double averageSpeedKMpH) {

		this.goal = goal;
		this.averageSpeedMetersPerSecond = (averageSpeedKMpH / 3.6);
		this.oneOverTravelTimeDenominator = 1 / (averageSpeedMetersPerSecond * Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER);
		this.profile = profile;
		this.flatnessBase = Evaluator.PERCEPTION_UPHILL_MULTIPLIER
				/ averageSpeedMetersPerSecond;
	}

	@Override
	public double getCostToGoalEstimate(TimedNode current) {
		
		double directDistance = EdgeUtil
				.computeDirectDistanceInM(current, goal);

		double traveTimeHeuristic = directDistance
				* oneOverTravelTimeDenominator;
		double result = 0;

		List<Criterion> criteria = profile.getCriteria();

		for (Criterion criterion : criteria) {
			if (criterion instanceof FlatnessCriterion) {
				result += flatnessBase * computeRisesToDestination(current)
						* criterion.getWeightedHeuristicValue();
			} else {
				result += traveTimeHeuristic
						* criterion.getWeightedHeuristicValue();
			}
		}

		return result;
	}

	private double computeRisesToDestination(TimedNode node) {
		double elevation = node.getElevation() - goal.getElevation();

		if (elevation > 0)
			return elevation;
		return 0;
	}

}
