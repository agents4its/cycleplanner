package cz.agents.cycleplanner.MLC.alg;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.util.Ellipse;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCEllipse<TNode extends Node, TEdge extends Edge> extends MLC<TNode, TEdge> {
	
	/**
	 * TODO documentation
	 */
	private double aOverB = 1.25d;
	
	/**
	 * TODO documentation
	 */
	private Ellipse ellipse;

	public MLCEllipse(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
		
//		ellipse = new Ellipse(origin.getGpsLocation(), destination.getGpsLocation(), this.aOverB);
	}

	@Override
	public boolean skipEdge(Label<TNode> next) {
		// Lazy initialization
		// TODO consider correctness of this implementation
		// TODO use initialization in constructor when we decide on stable value of aOverB
		if (ellipse == null) {
			ellipse = new Ellipse(origin.getGpsLocation(), destination.getGpsLocation(), this.aOverB);
		}

		return super.skipEdge(next) || !ellipse.isInside(next.getNode().getGpsLocation());
	}

}
