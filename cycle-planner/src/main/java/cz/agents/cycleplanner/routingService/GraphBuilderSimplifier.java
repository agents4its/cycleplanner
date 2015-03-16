package cz.agents.cycleplanner.routingService;

import java.util.ArrayList;
import java.util.Collection;
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

public class GraphBuilderSimplifier {

	private final static Logger log = Logger.getLogger(GraphBuilderSimplifier.class);

	private Map<Long, CycleNode> nodesByNodeId = new HashMap<Long, CycleNode>();
	private Map<EdgeId, CycleEdge> edgeByFromToNodeIds = new HashMap<EdgeId, CycleEdge>();
	private Map<Long, List<CycleEdge>> nodeOutcomingEdges = new HashMap<Long, List<CycleEdge>>();
	private Map<Long, List<CycleEdge>> nodeIncomingEdges = new HashMap<Long, List<CycleEdge>>();

	public GraphBuilderSimplifier() {
		super();
	}

	public void addNode(CycleNode node) {
		nodesByNodeId.put(node.getId(), node);
		nodeOutcomingEdges.put(node.getId(), new ArrayList<CycleEdge>());
		nodeIncomingEdges.put(node.getId(), new ArrayList<CycleEdge>());
	}

	public void addEdge(CycleEdge edge) {

		assert nodesByNodeId.get(edge.getFromNodeId()) != null && nodesByNodeId.get(edge.getToNodeId()) != null : "Node has to be in graph builder before inserting edge";

		EdgeId edgeId = new EdgeId(edge.getFromNodeId(), edge.getToNodeId());

		assert edgeByFromToNodeIds.containsKey(edgeId) == false : "Edge has not to exist yet";

		List<CycleEdge> outcomingEdgesFromNode = nodeOutcomingEdges.get(edge.getFromNodeId());
		List<CycleEdge> incomingEdgesToNode = nodeIncomingEdges.get(edge.getToNodeId());

		outcomingEdgesFromNode.add(edge);
		incomingEdgesToNode.add(edge);

		edgeByFromToNodeIds.put(edgeId, edge);
		nodeOutcomingEdges.put(edge.getFromNodeId(), outcomingEdgesFromNode);
		nodeIncomingEdges.put(edge.getToNodeId(), incomingEdgesToNode);

	}

	public void removeNodeAndConnectEdges(long nodeId) {

		CycleEdge incomingEdge = nodeIncomingEdges.get(nodeId).get(0);
		CycleEdge outcomingEdge = nodeOutcomingEdges.get(nodeId).get(0);

		long fromNodeId = incomingEdge.getFromNodeId();
		long toNodeId = outcomingEdge.getToNodeId();

		if (edgeByFromToNodeIds.containsKey(new EdgeId(fromNodeId, toNodeId))) {
			return;
		}

		nodeIncomingEdges.remove(nodeId);
		nodeOutcomingEdges.remove(nodeId);

		nodesByNodeId.remove(nodeId);

		removeEdge(incomingEdge.getFromNodeId(), nodeId);
		removeEdge(nodeId, outcomingEdge.getToNodeId());

		modifyEdge(nodeId, fromNodeId, toNodeId, incomingEdge, outcomingEdge);
	}

	private void updateEdge(long nodeId, long fromNodeId, long toNodeId, CycleEdge fromToCycleEdge) {

		edgeByFromToNodeIds.put(new EdgeId(fromNodeId, toNodeId), fromToCycleEdge);

		List<CycleEdge> outcomingEdges = nodeOutcomingEdges.get(fromNodeId);
		List<CycleEdge> incomingEdges = nodeIncomingEdges.get(toNodeId);

		outcomingEdges = removeEdgeFromList(outcomingEdges, fromNodeId, nodeId);
		incomingEdges = removeEdgeFromList(incomingEdges, nodeId, toNodeId);

		outcomingEdges.add(fromToCycleEdge);
		incomingEdges.add(fromToCycleEdge);

		nodeOutcomingEdges.put(fromNodeId, outcomingEdges);
		nodeIncomingEdges.put(toNodeId, incomingEdges);
	}

	public void removeNodeBothDirectionAndConnectEdges(long nodeId, long fromNodeId, long toNodeId) {

		if (edgeByFromToNodeIds.containsKey(new EdgeId(fromNodeId, toNodeId))
				|| edgeByFromToNodeIds.containsKey(new EdgeId(toNodeId, fromNodeId))) {
			return;
		}

		nodesByNodeId.remove(nodeId);

		CycleEdge incomingEdgeFN = getEdges(fromNodeId, nodeId);
		CycleEdge outcomingEdgeNT = getEdges(nodeId, toNodeId);

		CycleEdge incomingEdgeTN = getEdges(toNodeId, nodeId);
		CycleEdge outcomingEdgeNF = getEdges(nodeId, fromNodeId);

		nodeIncomingEdges.remove(nodeId);
		nodeOutcomingEdges.remove(nodeId);

		removeEdge(fromNodeId, nodeId);
		removeEdge(nodeId, fromNodeId);
		removeEdge(nodeId, toNodeId);
		removeEdge(toNodeId, nodeId);

		modifyEdge(nodeId, fromNodeId, toNodeId, incomingEdgeFN, outcomingEdgeNT);
		modifyEdge(nodeId, toNodeId, fromNodeId, incomingEdgeTN, outcomingEdgeNF);
	}

	private void modifyEdge(long nodeId, long fromNodeId, long toNodeId, CycleEdge incomingEdge,
			CycleEdge outcomingEdgeNT) {

		if (!incomingEdge.getWayId().equals(outcomingEdgeNT.getWayId())) {
			log.warn(String.format("Simplifying edges with different way IDs %d and %d.", incomingEdge.getWayId(),
					outcomingEdgeNT.getWayId()));
		}

		double length = incomingEdge.getLengthInMetres() + outcomingEdgeNT.getLengthInMetres();

		Set<String> ways = new HashSet<>();
		ways.addAll(incomingEdge.getOSMtags());
		ways.addAll(outcomingEdgeNT.getOSMtags());

		CycleEdge cycleEdgeFromTo = new CycleEdge(incomingEdge.getFromNode(), outcomingEdgeNT.getToNode(), length,
				ways, new HashSet<>(), incomingEdge.getWayId(), Double.POSITIVE_INFINITY);

		updateEdge(nodeId, fromNodeId, toNodeId, cycleEdgeFromTo);
	}

	private void removeEdge(long fromNodeId, long toNodeId) {
		edgeByFromToNodeIds.remove(new EdgeId(fromNodeId, toNodeId));
	}

	private List<CycleEdge> removeEdgeFromList(List<CycleEdge> edges, long fromNodeByNodeId, long toNodeByNodeId) {

		int removeInd = 0;
		for (CycleEdge tmp : edges) {
			if (tmp.getFromNodeId() == fromNodeByNodeId && tmp.getToNodeId() == toNodeByNodeId) {
				break;
			}
			removeInd++;
		}

		edges.remove(removeInd);
		return edges;
	}

	public CycleNode getNodeByNodeId(long nodeId) {
		return nodesByNodeId.get(nodeId);
	}

	public CycleEdge getEdges(long fromNodeId, long toNodeId) {
		return edgeByFromToNodeIds.get(new EdgeId(fromNodeId, toNodeId));
	}

	public Collection<CycleNode> getAllNodes() {
		return nodesByNodeId.values();
	}

	public List<CycleEdge> getNodeIncomingEdges(long nodeId) {
		return nodeIncomingEdges.get(nodeId);
	}

	public List<CycleEdge> getNodeOutcomingEdges(long nodeId) {
		return nodeOutcomingEdges.get(nodeId);
	}

	public Collection<CycleEdge> getAllEdges() {
		return edgeByFromToNodeIds.values();
	}

	/** @return Builded graph. */
	public Graph<CycleNode, CycleEdge> createGraph() {
		Graph<CycleNode, CycleEdge> graph = new Graph<CycleNode, CycleEdge>(nodesByNodeId, edgeByFromToNodeIds,
				nodeOutcomingEdges, nodeIncomingEdges);

		this.nodesByNodeId = new HashMap<Long, CycleNode>();
		this.edgeByFromToNodeIds = new HashMap<EdgeId, CycleEdge>();
		this.nodeOutcomingEdges = new HashMap<Long, List<CycleEdge>>();
		this.nodeIncomingEdges = new HashMap<Long, List<CycleEdge>>();

		return graph;

	}

}
