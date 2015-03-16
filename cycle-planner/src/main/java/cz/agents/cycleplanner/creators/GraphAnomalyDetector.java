package cz.agents.cycleplanner.creators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.EdgeId;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;

public class GraphAnomalyDetector {
	private static Logger log = Logger.getLogger(GraphAnomalyDetector.class);

	private Graph<CycleNode, CycleEdge> graph;
	private Map<Long, CycleNode> nodes;
	private Map<EdgeId, CycleEdge> edges;

	public GraphAnomalyDetector(Graph<CycleNode, CycleEdge> graph) {
		this.graph = graph;
		this.nodes = new HashMap<>();
		this.edges = new HashMap<>();
	}

	/**
	 * Method eliminates edges with same FROM and TO nodes(self loop) and edges
	 * with different ids but same coordinates for FROM and TO nodes
	 */
	public Graph<CycleNode, CycleEdge> detect() {

		Set<Long> deletedNodes = new HashSet<>();

		for (CycleEdge edge : graph.getAllEdges()) {

			if (edge.getFromNode().equals(edge.getToNode())) {
				log.info("Found edge with same id for from and to node.");

			} else if (deletedNodes.contains(edge.getFromNodeId()) || deletedNodes.contains(edge.getToNodeId())) {
				log.info("Found edge with node we already removed from graph.");

			} else if (edge.getFromNode().getLatitudeE6().equals(edge.getToNode().getLatitudeE6())
					&& edge.getFromNode().getLongitudeE6().equals(edge.getToNode().getLongitudeE6())) {

				log.info("Found edge with same coordinates for from and to node.");

				// put from node to the nodes map
				nodes.put(edge.getFromNodeId(), edge.getFromNode());

				// if TO node was add with other edge, remove it
				nodes.remove(edge.getToNodeId());
				// added to the set of deleted nodes
				deletedNodes.add(edge.getToNodeId());

				processIncomingEdges(graph.getNodeIncomingEdges(edge.getToNodeId()), edge.getFromNode());

				processOutcomingEdges(graph.getNodeOutcomingEdges(edge.getToNodeId()), edge.getFromNode());

			} else {
				nodes.put(edge.getFromNodeId(), edge.getFromNode());
				nodes.put(edge.getToNodeId(), edge.getToNode());
				edges.put(edge.getEdgeId(), edge);
			}
		}

		return buildNewGraph();
	}

	/**
	 * Method redirect edges which end in deleted node to the node @param FROM
	 */
	private void processIncomingEdges(List<CycleEdge> incoming, CycleNode from) {
		for (CycleEdge incomingEdge : incoming) {

			// if the edge is equal to the edge which trigger this method
			if (incomingEdge.getFromNode().equals(from)) {
				continue;
			}

			edges.remove(incomingEdge.getEdgeId());

			CycleEdge newEdge = new CycleEdge(incomingEdge.getFromNode(), from, incomingEdge.getLengthInMetres(),
					incomingEdge.getOSMtags(), new HashSet<>(), incomingEdge.getWayId(),
					incomingEdge.getJunctionAngle());

			edges.put(newEdge.getEdgeId(), newEdge);
			nodes.put(newEdge.getFromNodeId(), newEdge.getFromNode());
		}
	}

	/**
	 * Method redirect edges which starts in deleted node to the node @param
	 * FROM
	 */
	private void processOutcomingEdges(List<CycleEdge> outcoming, CycleNode from) {
		for (CycleEdge outcomingEdge : outcoming) {

			// if the edge is equal to the revert edge which trigger this method
			if (outcomingEdge.getToNode().equals(from)) {
				continue;
			}
			edges.remove(outcomingEdge.getEdgeId());

			CycleEdge newEdge = new CycleEdge(from, outcomingEdge.getToNode(), outcomingEdge.getLengthInMetres(),
					outcomingEdge.getOSMtags(), new HashSet<>(), outcomingEdge.getWayId(),
					outcomingEdge.getJunctionAngle());

			edges.put(newEdge.getEdgeId(), newEdge);
			nodes.put(newEdge.getToNodeId(), newEdge.getToNode());
		}
	}

	private Graph<CycleNode, CycleEdge> buildNewGraph() {
		GraphBuilder<CycleNode, CycleEdge> builder = new GraphBuilder<>();

		builder.addNodes(nodes.values());
		builder.addEdges(edges.values());

		return builder.createGraph();
	}
}
