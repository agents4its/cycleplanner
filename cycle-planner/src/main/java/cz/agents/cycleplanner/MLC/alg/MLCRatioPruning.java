package cz.agents.cycleplanner.MLC.alg;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCRatioPruning<TNode extends Node, TEdge extends Edge> extends MLC<TNode, TEdge> {

	/**
	 * TODO documentation, add static final modifier, but than is not possible to change value not even with reflection
	 */
	private double alpha = 1.6;

	/**
	 * TODO documentation
	 */
	private double maxTravelTime;

	/**
	 * TODO documentation
	 */
	private double minTravelTime;

	/**
	 * TODO documentation
	 */
	private boolean reachedDestination = false;

	public MLCRatioPruning(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
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
