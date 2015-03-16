package cz.agents.cycleplanner.MLC.alg.mixEllipseRatioPruningOthers;

import java.util.Iterator;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseRatioPruning;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCEllipseRatioPruningBuckets<TNode extends Node, TEdge extends Edge> extends MLCEllipseRatioPruning<TNode, TEdge> {

	/**
	 * TODO javadoc
	 */
	private int[] buckets = new int[]{15, 2500, 4};

	public MLCEllipseRatioPruningBuckets(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
	}

	@Override
	public boolean checkDominance(Label<TNode> next) {
		boolean isDominant = true;
		long successorsNodeID = next.getNode().getId();
		int[] successorsCriteria = next.getCostVector();
		int[] successorBucketCriteria = new int[] { bucketValue(successorsCriteria[0], buckets[0]),
				bucketValue(successorsCriteria[1], buckets[1]), bucketValue(successorsCriteria[2], buckets[2]) };

		for (Iterator<Label<TNode>> it = bags.get(successorsNodeID).iterator(); it.hasNext();) {
			Label<TNode> lab = it.next();

			int[] bagBucketCriteria = new int[] { bucketValue(lab.getCostVector()[0], buckets[0]),
					bucketValue(lab.getCostVector()[1], buckets[1]), bucketValue(lab.getCostVector()[2], buckets[2]) };

			if (isDominant(bagBucketCriteria, successorBucketCriteria)) {
				isDominant = false;
				break;
			}

			if (isDominant(successorBucketCriteria, bagBucketCriteria)) {
				it.remove();
				queue.remove(lab);
			}
		}

		return isDominant;
	}

	/**
	 * TODO javadoc
	 */
	private int bucketValue(int value, int bucketSize) {

		return value - (value % bucketSize);
	}

}
