package cz.agents.cycleplanner.junctions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.EdgeId;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;

/**
 * 
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 * @author Qing Song <qing.song@agents.fel.cvut.cz>
 */
public class JunctionExtension {
	private final static Logger log = Logger.getLogger(JunctionExtension.class);

	private Graph<CycleNode, CycleEdge> graph;
	private Map<Long, CycleNode> nodes;
	private Map<EdgeId, CycleEdge> edges;

	public JunctionExtension(Graph<CycleNode, CycleEdge> graph) {
		this.graph = graph;

		this.nodes = new HashMap<>();
		this.edges = new HashMap<>();

		for (CycleNode node : graph.getAllNodes()) {
			this.nodes.put(node.getId(), node);
		}

		for (CycleEdge edge : graph.getAllEdges()) {
			this.edges.put(edge.getEdgeId(), edge);
		}

	}

	public Graph<CycleNode, CycleEdge> getExtendedGraph() {

		JunctionExtraction junctionExtraction = new JunctionExtraction(graph);
		Collection<Junction> junctions = junctionExtraction.getJunctions();

		extendGraph(junctions);

		GraphBuilder<CycleNode, CycleEdge> builder = new GraphBuilder<>();
		builder.addNodes(nodes.values());
		builder.addEdges(edges.values());

		return builder.createGraph();
	}

	private void extendGraph(Collection<Junction> junctions) {

		for (Junction junction : junctions) {
			if (nodes.containsKey(junction.getJunctionId())) {
				nodes.remove(junction.getJunctionId());
			}

			for (EdgeId edgeId : junction.getNewIncomingEdgesByOldEdgeId().keySet()) {

				CycleEdge edge = junction.getNewIncomingEdgesByOldEdgeId().get(edgeId);
				CycleEdge combined = combineEdges(edge, edges.get(edgeId));
				edges.put(edgeId, combined);
				nodes.put(edge.getToNodeId(), edge.getToNode());
			}

			for (EdgeId edgeId : junction.getNewOutcomingEdgesByOldEdgeId().keySet()) {

				CycleEdge edge = junction.getNewOutcomingEdgesByOldEdgeId().get(edgeId);
				CycleEdge combined = combineEdges(junction.getNewOutcomingEdgesByOldEdgeId().get(edgeId),
						edges.get(edgeId));
				edges.put(edgeId, combined);
				nodes.put(edge.getFromNodeId(), edge.getFromNode());
			}

			for (JunctionInnerEdge innerEdge : junction.getInnerEdges()) {
				CycleEdge edge = innerEdge.getCycleEdge();
				edges.put(edge.getEdgeId(), edge);
			}
		}

	}

	private CycleEdge combineEdges(CycleEdge newEdge, CycleEdge existingEdge) {

		CycleNode from1 = newEdge.getFromNode();
		CycleNode to1 = newEdge.getToNode();

		CycleNode from2 = existingEdge.getFromNode();
		CycleNode to2 = existingEdge.getToNode();

		if (from2.getId() > 0) {
			from2 = from1;
		}
		if (to2.getId() > 0) {
			to2 = to1;
		}

		double lengthInMetres = EdgeUtil.computeDirectDistanceInM(from2.getGpsLocation(), to2.getGpsLocation());
		CycleEdge edge = new CycleEdge(from2, to2, lengthInMetres, existingEdge.getOSMtags(), new HashSet<>(),
				existingEdge.getWayId(), existingEdge.getJunctionAngle());

		return edge;
	}

}
