package cz.agents.cycleplanner.MLC.alg;

import java.util.Iterator;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCCostPruning<TNode extends Node, TEdge extends Edge> extends MLC<TNode, TEdge> {
	
	/**
	 * TODO documentation
	 */
	private int gamma = 5;

	public MLCCostPruning(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
	}

	@Override
	public boolean checkDominance(Label<TNode> next) {

		boolean isDominant = true;
		long successorsNodeID = next.getNode().getId();
		int[] successorsCriteria = next.getCostVector();

		for (Iterator<Label<TNode>> it = bags.get(successorsNodeID).iterator(); it.hasNext();) {
			Label<TNode> lab = it.next();

			if (isDominant(successorsCriteria, lab.getCostVector())) {
				it.remove();
				queue.remove(lab);
				// TODO WHY continue?
				continue;
			}

			if (!labelDistanceCheck(lab.getCostVector(), successorsCriteria)
					|| isDominant(lab.getCostVector(), successorsCriteria)) {
				isDominant = false;
				break;
			}

		}

		return isDominant;
	}

	// TODO rename
	private boolean labelDistanceCheck(int[] dist1, int[] dist2) {

		int costLimit = dist1[0] / gamma;
		int cost = 0;
		int length = (dist1.length <= dist2.length) ? dist1.length : dist2.length;

		for (int i = 0; i < length; i++) {
			cost += ((dist1[i] - dist2[i]) * (dist1[i] - dist2[i]));
		}

		// TODO MAKE PARAMETER for 200
		// podmienka dist2[0]<200 znamena ze chceme aby sa search dostal do
		// nejakeho priestoru
		// s vatsim poctom lablov ktore mozme expandovat a az potom zacne
		// prunning
		// v nasom pripade zacne pruning v momente ked mame trasu z origin do
		// ostatnych bodov vatsiu ako tri minuty
		if ((dist2[0] < 200) || (cost > (costLimit * costLimit))) {
			return true;
		}

		return false;
	}

}
