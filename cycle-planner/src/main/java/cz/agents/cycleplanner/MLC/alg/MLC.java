package cz.agents.cycleplanner.MLC.alg;

import java.util.Iterator;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLC<TNode extends Node, TEdge extends Edge> extends AbstractMultiLabelCorrectingAlgorithm<TNode, TEdge> {

	public MLC(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
	}

	@Override
	public boolean terminationConditon(Label<TNode> current) {

		return false;
	}

	@Override
	public boolean skipLabel(Label<TNode> current) {

		return false;
	}

	@Override
	public boolean skipEdge(Label<TNode> next) {
		// Check if previous node is equal to next node
		Label<TNode> prevLabel = next.getPredecessorLabel().getPredecessorLabel();
		TNode successor = next.getNode();

		return prevLabel != null && prevLabel.getNode().equals(successor);
	}

	@Override
	public boolean checkDominance(Label<TNode> next) {
		boolean isDominant = true;
		long successorsNodeID = next.getNode().getId();
		int[] successorsCriteria = next.getCostVector();

		for (Iterator<Label<TNode>> it = bags.get(successorsNodeID).iterator(); it.hasNext();) {
			Label<TNode> lab = it.next();

			if (isDominant(lab.getCostVector(), successorsCriteria)) {
				isDominant = false;
				break;
			}

			if (isDominant(successorsCriteria, lab.getCostVector())) {
				it.remove();
				queue.remove(lab);
			}
		}

		return isDominant;
	}

}
