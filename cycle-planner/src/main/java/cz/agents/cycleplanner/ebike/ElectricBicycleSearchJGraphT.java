package cz.agents.cycleplanner.ebike;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import cz.agents.cycleplanner.aStar.AStarSearch;
import cz.agents.cycleplanner.aStar.JGraphT.BikeAdvancedTimedGraph;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.algorithms.DijkstraSimpleGraphSingleGoal;
import eu.superhub.wp5.plannercore.algorithms.goalcheckers.DestinationNodeChecker;
import eu.superhub.wp5.plannercore.algorithms.heuristics.Heuristic;
import eu.superhub.wp5.plannercore.structures.evaluators.vertexevaluators.HeuristicVE;
import eu.superhub.wp5.plannercore.structures.evaluators.vertexevaluators.KeyDistanceTimeVE;
import eu.superhub.wp5.plannercore.structures.evaluators.vertexevaluators.VertexEvaluator;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedEdge;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;

public class ElectricBicycleSearchJGraphT implements AStarSearch {
	private final static Logger log = Logger.getLogger(ElectricBicycleSearchJGraphT.class);
	
	private double averageSpeedKMpH;

	public ElectricBicycleSearchJGraphT(double averageSpeedKMpH) {
		
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

		BikeAdvancedTimedGraph timedGraph = new BikeAdvancedTimedGraph(graph, new ElectricBicycleCriterion(
				averageSpeedKMpH), averageSpeedKMpH);

		Heuristic<TimedNode> heuristic = new ElectricHeuristicFunction(endNode, averageSpeedKMpH);

		TimedNode startVertex = new TimedNode(startNode, new DateTime(), 0);
		startVertex = new TimedNode(startNode, new DateTime(), heuristic.getCostToGoalEstimate(startVertex));

		VertexEvaluator<TimedNode> vertexEvaluator = new KeyDistanceTimeVE<>();

		HeuristicVE<TimedNode> comparator = new HeuristicVE<TimedNode>(heuristic, vertexEvaluator);

		DestinationNodeChecker<TimedNode> goalChecker = new DestinationNodeChecker<>(endNode.getId());

		DijkstraSimpleGraphSingleGoal<TimedNode, TimedEdge> dijkstraSimpleGraph = new DijkstraSimpleGraphSingleGoal<TimedNode, TimedEdge>(
				timedGraph, startVertex, comparator, goalChecker);

		dijkstraSimpleGraph.call();
		
		Collection<CycleEdge> edges = new ArrayList<>();
		
		for (TimedEdge tEdge : dijkstraSimpleGraph.getPathEdgeList()) {			
			CycleEdge cycleEdge = (CycleEdge) graph.getEdgeByEdgeId(tEdge.getEdge().getEdgeId()); 
			
			edges.add(cycleEdge);
		}
		log.info("Path length: " + dijkstraSimpleGraph.getPathWeight());
		log.info("Closed set size: " + dijkstraSimpleGraph.getClosedSetSize());

		return edges;
	}

}
