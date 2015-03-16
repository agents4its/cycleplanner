package cz.agents.cycleplanner.junctions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;

// TODO javadoc
public class JunctionExtraction {

	private static final Logger log = Logger.getLogger(JunctionExtraction.class);

	private static final Pattern PERMITED_EDGES = Pattern
			.compile("way::highway::living_street|way::highway::primary|way::highway::primary_link|way::highway::residential|way::highway::secondary|way::highway::secondary_link|way::highway::tertiary|way::highway::tertiary_link|way::highway::unclassified|way::highway::road");

	private Graph<CycleNode, CycleEdge> graph;

	public JunctionExtraction(Graph<CycleNode, CycleEdge> graph) {
		this.graph = graph;
	}

	public Collection<Junction> getJunctions() {

		log.info("Extracting junctions...");

		Collection<CycleEdge> extractedEdges = extractEdges(graph.getAllEdges());

		GraphBuilder<CycleNode, CycleEdge> builder = new GraphBuilder<>();
		builder.addNodes(graph.getAllNodes());
		builder.addEdges(extractedEdges);

		Collection<Junction> extractedNodes = extractNodes(graph.getAllNodes(), builder.createGraph());

		return extractedNodes;
	}

	private Collection<CycleEdge> extractEdges(Collection<CycleEdge> edges) {
		Collection<CycleEdge> simple = new HashSet<>();

		for (CycleEdge edge : edges) {
			if (!disableEdge((CycleEdge) edge)) {
				simple.add(edge);
			}
		}
		return simple;
	}

	private Collection<Junction> extractNodes(Collection<CycleNode> nodes, Graph<CycleNode, CycleEdge> graph) {

		Collection<Junction> simple = new HashSet<>();
		for (CycleNode node : nodes) {
			if (!disableNode((CycleNode) node, graph)) {
				
				log.debug((CycleNode) node + " " + this.graph.getNodeIncomingEdges(node.getId()) + " "
						+ this.graph.getNodeOutcomingEdges(node.getId()));
				
				Junction junction = new Junction((CycleNode) node, this.graph.getNodeIncomingEdges(node.getId()),
						this.graph.getNodeOutcomingEdges(node.getId()));

				simple.add(junction);
			}
		}
		return simple;
	}

	private boolean disableNode(CycleNode node, Graph<?, ?> graph) {
		Set<Long> neighbors = graph.getAllNeighbors(node.getId());
		return neighbors.size() <= 2;
	}

	private boolean disableEdge(CycleEdge edge) {
		if (edge.getOSMtags() != null) {
			for (String tag : edge.getOSMtags()) {
				if (PERMITED_EDGES.matcher(tag).matches())
					return false;
			}
		}
		return true;
	}
}
