package cz.agents.cycleplanner.MLC;

import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

/**
 * 
 * TODO javadoc
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 * @param <TNode>
 * @param <TEdge>
 */
public interface MLCCostFunction<TNode extends Node, TEdge extends Edge> {

	/**
	 * 
	 * Returns cost vector containing criteria values for directed arc between
	 * node <code>current</code> and <code>next</code>.
	 * 
	 * @param current
	 *            arc's from node
	 * @param next
	 *            arc's to node
	 * @param edge
	 *            directed arc
	 * @return cost vector represented as array of integers
	 */
	public int[] getCostVector(TNode current, TNode next, TEdge edge);
}
