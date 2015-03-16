package cz.agents.cycleplanner.MLC.alg;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

abstract public class AbstractMultiLabelCorrectingAlgorithm<TNode extends Node, TEdge extends Edge> {

	private static Logger log = Logger.getLogger(AbstractMultiLabelCorrectingAlgorithm.class);

	private long cuttingTime = Long.MAX_VALUE;

	private Graph<TNode, TEdge> graph;

	protected TNode origin, destination;

	private MLCCostFunction<TNode, TEdge> costFunction;

	protected PriorityQueue<Label<TNode>> queue;

	protected Map<Long, Collection<Label<TNode>>> bags;

	private long labelIDGenerator = 0;

	private Collection<Collection<TNode>> pathsAsSequenceOfNodes;

	private Collection<Collection<TEdge>> pathsAsSequenceOfEdges;

	private Collection<Label<TNode>> destinationBag;

	// Statistics
	private long iterations = 0;

	private long planningTime = 0;

	public AbstractMultiLabelCorrectingAlgorithm(Graph<TNode, TEdge> graph, TNode origin, TNode destination,
			MLCCostFunction<TNode, TEdge> costFunction) {

		this.graph = graph;
		this.origin = origin;
		log.info("Origin: " + origin);
		this.destination = destination;
		log.info("Destination: " + destination);
		this.costFunction = costFunction;
		this.queue = new PriorityQueue<Label<TNode>>();
		this.bags = new HashMap<Long, Collection<Label<TNode>>>(this.graph.getAllNodes().size());

		for (TNode node : this.graph.getAllNodes()) {
			bags.put(node.getId(), new ArrayList<Label<TNode>>());
		}

		this.pathsAsSequenceOfNodes = new ArrayList<Collection<TNode>>();
		this.pathsAsSequenceOfEdges = new ArrayList<Collection<TEdge>>();
		this.destinationBag = this.bags.get(destination.getId());

	}

	public void call() {
		log.info("Cutting time set to " + cuttingTime + " ms.");
		planningTime = System.currentTimeMillis();

		insertOrigin(origin);

		while (!queue.isEmpty() && (System.currentTimeMillis() - planningTime) < cuttingTime) {

			Label<TNode> currentLabel = queue.poll();

			if (terminationConditon(currentLabel)) {
				break;
			}

			if (skipLabel(currentLabel)) {
				continue;
			}

			for (TEdge outcomingEdge : graph.getNodeOutcomingEdges(currentLabel.getNode().getId())) {
				long nextNodeId = outcomingEdge.getToNodeId();
				TNode nextNode = graph.getNodeByNodeId(nextNodeId);

				int[] nextLabelCostVector = new int[3];
				int[] outcomingEdgeCostVector = costFunction.getCostVector(currentLabel.getNode(), nextNode,
						outcomingEdge);

				for (int i = 0; i < nextLabelCostVector.length; i++) {
					nextLabelCostVector[i] = currentLabel.getCostVector()[i] + outcomingEdgeCostVector[i];
				}

				Label<TNode> nextLabel = new Label<TNode>(nextNode, nextLabelCostVector, currentLabel);

				if (skipEdge(nextLabel)) {
					continue;
				}

				if (checkDominance(nextLabel)) {

					bags.get(nextNode.getId()).add(nextLabel);
					queue.offer(nextLabel);
				}
			}

			// checkMemoryConsumption();
			iterations++;
		}

		destinationBag = bags.get(destination.getId());

		reconstructPaths();

		planningTime = System.currentTimeMillis() - planningTime;

		log.info("Number of pareto routes: " + destinationBag.size());
		log.info("Running time: " + planningTime + " ms");
		log.info("Number of iterations: " + iterations);
	}

	/**
	 * 
	 * @return
	 */
	abstract public boolean terminationConditon(Label<TNode> current);

	/**
	 * 
	 * @return
	 */
	abstract public boolean skipLabel(Label<TNode> current);

	/**
	 * 
	 * @return
	 */
	abstract public boolean skipEdge(Label<TNode> next);

	/**
	 * 
	 * @return
	 */
	abstract public boolean checkDominance(Label<TNode> next);

	/**
	 * Check if first parameter is dominant over second. In other words, check
	 * if second argument is dominated by first.
	 * 
	 * @param dist1
	 * @param dist2
	 * @return
	 */
	// TODO rename example: first dominate second
	protected boolean isDominant(int[] dist1, int[] dist2) {
		assert dist1.length == dist2.length : "Size of paraters do not match!";

		for (int i = 0; i < dist1.length; i++) {
			if (dist1[i] > dist2[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check if first parameter is Epsilon Dominant over second. In other words,
	 * check if second argument is dominated by first.
	 * 
	 * @param dist1
	 * @param dist2
	 * @param epsilon
	 * @return
	 */
	// TODO rename example: first dominate second
	protected boolean isEpsilonDominant(int[] dist1, int[] dist2, double epsilon) {
		assert dist1.length == dist2.length : "Size of paraters do not match!";
		
		for (int i = 0; i < dist1.length; i++) {
			int epsilonDistance = (int) Math.round((1d + epsilon) * ((double) dist2[i]));
			
			if (dist1[i] > epsilonDistance) {
				return false;
			}
		}
		
		return true;
	}

	public int getParetoSetSize() {
		return pathsAsSequenceOfNodes.size();
	}

	public long getIterations() {
		return iterations;
	}

	public long getPlanningTime() {
		return planningTime;
	}

	private void insertOrigin(TNode origin) {
		Label<TNode> originLabel = new Label<TNode>(origin, new int[] { 0, 0, 0 }, null);

		queue.add(originLabel);

		bags.put(origin.getId(), new ArrayList<Label<TNode>>());
		bags.get(origin.getId()).add(originLabel);
	}

	private void reconstructPaths() {
		log.info("Reconstructing paths");

		for (Iterator<Label<TNode>> it = destinationBag.iterator(); it.hasNext();) {
			// TODO MAYBE check dominance of the paths again!
			Label<TNode> destinationLabel = it.next();

			pathsAsSequenceOfNodes.add(reconstructPathNodes(destinationLabel));
			pathsAsSequenceOfEdges.add(reconstructPathEdges(destinationLabel));
		}
	}

	private Collection<TNode> reconstructPathNodes(Label<TNode> label) {

		ArrayDeque<TNode> path = new ArrayDeque<TNode>();
		Label<TNode> prevLabel;

		path.addFirst(label.getNode());

		while ((prevLabel = label.getPredecessorLabel()) != null) {

			path.addFirst(prevLabel.getNode());
			label = prevLabel;
		}

		return path;
	}

	private Collection<TEdge> reconstructPathEdges(Label<TNode> label) {

		ArrayDeque<TEdge> path = new ArrayDeque<>();
		Label<TNode> prevLabel = label.getPredecessorLabel();

		while ((prevLabel = label.getPredecessorLabel()) != null) {

			path.addFirst(graph.getEdge(prevLabel.getNode().getId(), label.getNode().getId()));
			label = prevLabel;
		}

		return path;
	}

	public Collection<Collection<TNode>> getPathsNodes() {
		return pathsAsSequenceOfNodes;
	}

	public Collection<Collection<TEdge>> getPathsEdges() {
		return pathsAsSequenceOfEdges;
	}

	public Collection<Label<TNode>> getDestinationBag() {
		return destinationBag;
	}

	protected long getLabelID() {
		return labelIDGenerator++;
	}

}
