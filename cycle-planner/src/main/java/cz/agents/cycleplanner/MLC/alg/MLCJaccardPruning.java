package cz.agents.cycleplanner.MLC.alg;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

//TODO Implement???
@SuppressWarnings("unused")
public class MLCJaccardPruning<TNode extends Node, TEdge extends Edge> extends MLC<TNode, TEdge> {
	
	
	public MLCJaccardPruning(Graph<TNode, TEdge> graph, TNode origin, TNode destination, MLCCostFunction<TNode, TEdge> costFunction) {
		super(graph, origin, destination, costFunction);
	}

	@Override
	public boolean checkDominance(Label<TNode> next) {
		// compute jaccardLimit
//		if (label.getCriteria()[0] > DISTANCE_LOWER_BOUND) {
//			if (label.getCriteria()[0] < DISTANCE_UPPER_BOUND) {
//				jaccardLimit = 0.7 * (label.getCriteria()[0] - 300) / 1500;
//			} else {
//				jaccardLimit = 0.55;
//			}
//		}
		
//		for (Iterator<Label> it = bags.get(successor.getId()).iterator(); it.hasNext();) {
//			Label lab = it.next();
//
//			if (dominanceCheck(successorsCriteria, lab.getCriteria())) {
//				it.remove();
//				deletedLabels.add(lab.getID());
//				continue;
//			}
//
//			double jaccardDistance = 0;
//			Label prevBagLabel = lab.getPredecessorLabel();
//			
//			if (prevBagLabel != null && successorsCriteria[0] >= DISTANCE_LOWER_BOUND) {
//				List<Long> pathBag = reconstructPathAsNodeIDSequence(prevBagLabel);
//				List<Long> pathSuccessor = reconstructPathAsNodeIDSequence(label);
//				jaccardDistance = JaccardDistanceComputation(pathBag, pathSuccessor);
//			}
//
//			if ((successorsCriteria[0] < DISTANCE_LOWER_BOUND) || (jaccardDistance > jaccardLimit)) { // ////////
//
//				if (dominanceCheck(lab.getCriteria(), successorsCriteria)) {
//					insert = false;
//					break; // exit current loop
//				}
//
//			} else {
//				insert = false;
//				break;
//			}
//		}

		
		
		return super.checkDominance(next);
	}
	
}
