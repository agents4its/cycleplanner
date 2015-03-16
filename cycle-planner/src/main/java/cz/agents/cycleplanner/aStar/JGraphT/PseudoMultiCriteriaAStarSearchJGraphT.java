package cz.agents.cycleplanner.aStar.JGraphT;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import cz.agents.cycleplanner.aStar.AStarSearch;
import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.algorithms.AStarSimpleGraph;
import eu.superhub.wp5.plannercore.algorithms.goalcheckers.DestinationNodeChecker;
import eu.superhub.wp5.plannercore.algorithms.heuristics.Heuristic;
import eu.superhub.wp5.plannercore.structures.evaluators.vertexevaluators.HeuristicVE;
import eu.superhub.wp5.plannercore.structures.evaluators.vertexevaluators.KeyDistanceTimeVE;
import eu.superhub.wp5.plannercore.structures.evaluators.vertexevaluators.VertexEvaluator;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedEdge;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;

public class PseudoMultiCriteriaAStarSearchJGraphT implements AStarSearch {
	private final static Logger log = Logger.getLogger(PseudoMultiCriteriaAStarSearchJGraphT.class);
	private Profile profile;
	private double averageSpeedKMpH;

	public PseudoMultiCriteriaAStarSearchJGraphT(Profile profile, double averageSpeedKMpH) {
		this.profile = profile;
		this.averageSpeedKMpH = averageSpeedKMpH;
	}

	@Override
	public Collection<CycleNode> findPath(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph)
			throws Exception {

		return null;
	}

	@Override
	public Collection<CycleEdge> findPathEdges(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph)
			throws Exception {

		BikeAdvancedTimedGraph timedGraph = new BikeAdvancedTimedGraph(graph, new CriterionAKE(profile,
				averageSpeedKMpH), averageSpeedKMpH);

		Heuristic<TimedNode> heuristic = new HeuristicFunction(endNode, profile, averageSpeedKMpH);

		TimedNode startVertex = new TimedNode(startNode, new DateTime(), 0);
		startVertex = new TimedNode(startNode, new DateTime(), heuristic.getCostToGoalEstimate(startVertex));

		VertexEvaluator<TimedNode> vertexEvaluator = new KeyDistanceTimeVE<>();

		HeuristicVE<TimedNode> comparator = new HeuristicVE<TimedNode>(heuristic, vertexEvaluator);

		DestinationNodeChecker<TimedNode> goalChecker = new DestinationNodeChecker<>(endNode.getId());

		AStarSimpleGraph<TimedNode, TimedEdge> aStarSimpleGraph = new AStarSimpleGraph<TimedNode, TimedEdge>(
				timedGraph, startVertex, comparator, goalChecker);

		aStarSimpleGraph.call();
		Collection<CycleEdge> edges = new ArrayList<>();
		for (TimedEdge tEdge : aStarSimpleGraph.getPathEdgeList()) {
			edges.add((CycleEdge) graph.getEdgeByEdgeId(tEdge.getEdge().getEdgeId()));
		}

		log.info("Closed set size: " + aStarSimpleGraph.getClosedSetSize());

		return edges;
	}
}
