package cz.agents.cycleplanner.aStar.aima;

import java.util.List;

import aima.core.agent.Action;
import aima.core.search.framework.HeuristicFunction;
import aima.core.search.framework.StepCostFunction;
import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class PseudoMultiCriteriaAStarSearchAIMA extends AbstractAStarSearch {

	private Profile profile;
	private double averageSpeedKMpH;

	public PseudoMultiCriteriaAStarSearchAIMA(Profile profile, double averageSpeedKMpH) {
		this.profile = profile;
		this.averageSpeedKMpH = averageSpeedKMpH;
	}

	@Override
	protected List<Action> findActions(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph)
			throws Exception {
		StepCostFunction costWithElevation = new Cost(profile, averageSpeedKMpH);
		HeuristicFunction heuristicWithElevation = new Heuristic(endNode, profile, averageSpeedKMpH);
		List<Action> actions = findPathAStar(startNode, graph, endNode, costWithElevation, heuristicWithElevation);
		return actions;
	}
}
