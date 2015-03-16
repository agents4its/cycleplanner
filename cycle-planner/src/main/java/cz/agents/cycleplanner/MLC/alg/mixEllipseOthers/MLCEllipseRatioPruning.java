package cz.agents.cycleplanner.MLC.alg.mixEllipseOthers;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.MLC.alg.MLCEllipse;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCEllipseRatioPruning<TNode extends Node, TEdge extends Edge> extends MLCEllipse<TNode, TEdge> {

	/**
	 * TODO javadoc
	 */
	private double alpha = 1.6;

	/**
	 * TODO javadoc
	 */
	private double maxTravelTime;

	/**
	 * TODO javadoc
	 */
	private double minTravelTime;

	/**
	 * TODO javadoc
	 */
	private boolean reachedDestination = false;

	public MLCEllipseRatioPruning(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
	}

	@Override
	public boolean terminationConditon(Label<TNode> current) {

		// compute pruning parameter, setting ratio, only when we first time
		// arrived to destination
		if (!reachedDestination && current.getNode().equals(destination)) {
			reachedDestination = true;
			minTravelTime = current.getCostVector()[0];
			maxTravelTime = Math.ceil(minTravelTime * alpha);
		}

		return reachedDestination && current.getCostVector()[0] > maxTravelTime;
	}

}
