package cz.agents.cycleplanner.MLC.alg.mixRatioPruningOthers;

import java.util.Iterator;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.MLC.alg.MLCRatioPruning;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCRatioPruningEpsilonDominance<TNode extends Node, TEdge extends Edge> extends MLCRatioPruning<TNode, TEdge> {

	/**
	 * TODO documentation
	 */
	private double epsilon = .05d;

	public MLCRatioPruningEpsilonDominance(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
	}

	@Override
	public boolean checkDominance(Label<TNode> next) {

		boolean isDominant = true;
		long successorsNodeID = next.getNode().getId();
		int[] successorsCriteria = next.getCostVector();

		for (Iterator<Label<TNode>> it = bags.get(successorsNodeID).iterator(); it.hasNext();) {
			Label<TNode> lab = it.next();

			if (isEpsilonDominant(lab.getCostVector(), successorsCriteria, epsilon)) {
				isDominant = false;
				break;
			}

			if (isEpsilonDominant(successorsCriteria, lab.getCostVector(), epsilon)) {
				it.remove();
				queue.remove(lab);
			}
		}

		return isDominant;
	}
}