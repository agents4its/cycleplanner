package cz.agents.cycleplanner.ebike;

import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.algorithms.heuristics.Heuristic;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;

public class ElectricHeuristicFunction implements Heuristic<TimedNode> {

	private Node goal;
	private double averageSpeedMetersPerSecond;
	
	// TODO
	public ElectricHeuristicFunction(Node goal, double averageSpeedKMpH) {

		this.goal = goal;
		this.averageSpeedMetersPerSecond = (averageSpeedKMpH / 3.6);
	}

	@Override
	public double getCostToGoalEstimate(TimedNode current) {

		return 0d;
	}

}
