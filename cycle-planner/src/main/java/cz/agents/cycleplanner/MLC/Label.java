package cz.agents.cycleplanner.MLC;

import java.util.Arrays;

import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class Label<TNode extends Node> implements Comparable<Label<TNode>> {

	private final TNode node;

	private final int[] costVector;

	private final Label<TNode> predecessorLabel;

	public Label(TNode node, int[] costVector, Label<TNode> predecessorLabel) {

		this.node = node;
		this.costVector = costVector;
		this.predecessorLabel = predecessorLabel;
	}

	/**
	 * 
	 * Returns node to which is assigned this label.
	 * 
	 * @return node
	 */
	public TNode getNode() {
		return node;
	}

	/**
	 * 
	 * Returns cost vector, that represents cumulated value for each criterion
	 * from origin to current node.
	 * 
	 * @return cost vector represented as array of integers
	 */
	public int[] getCostVector() {
		return costVector;
	}

	/**
	 * 
	 * Returns label, that precedes this label.
	 * 
	 * In order to reconstruct path after search process we need to able get
	 * previous label to this label.
	 * 
	 * @return previous label
	 */
	public Label<TNode> getPredecessorLabel() {
		return predecessorLabel;
	}

	@Override
	public int compareTo(Label<TNode> l) {

		for (int i = 0; i < costVector.length; i++) {

			if (costVector[i] < l.getCostVector()[i]) {
				return -1;

			} else if (costVector[i] > l.getCostVector()[i]) {
				return 1;
			}

		}

		return 0;
	}

	@Override
	public String toString() {
		return "Label [node=" + node + ", costVector=" + Arrays.toString(costVector) + ", predecessorLabel="
				+ predecessorLabel + "]";
	}
}
