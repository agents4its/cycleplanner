package cz.agents.cycleplanner.routingService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;

public class EdgeSimplifier {
	private final Set<Long> nodesThatWillNotBeRemoved;

	public EdgeSimplifier() {
		this.nodesThatWillNotBeRemoved = new HashSet<Long>();
	}

	public EdgeSimplifier(Set<Long> nodesThatWillNotBeRemoved) {
		this.nodesThatWillNotBeRemoved = nodesThatWillNotBeRemoved;
	}

	public Graph<CycleNode, CycleEdge> simplifyGraph(
			Graph<CycleNode, CycleEdge> graph) {

		GraphBuilderSimplifier graphRepairBuilder = new GraphBuilderSimplifier();

		for (CycleNode cycleNode : graph.getAllNodes()) {
			graphRepairBuilder.addNode(cycleNode);
		}

		for (CycleEdge CycleEdge : graph.getAllEdges()) {
			graphRepairBuilder.addEdge(CycleEdge);
		}

		for (CycleNode CycleNode : graph.getAllNodes()) {

			long nodeId = CycleNode.getId();

			if (nodesThatWillNotBeRemoved.contains(nodeId)) {
				continue;
			}

			if (graphRepairBuilder.getNodeIncomingEdges(nodeId).size() == 1
					&& graphRepairBuilder.getNodeOutcomingEdges(nodeId).size() == 1) {

				CycleEdge fromCycleEdge = graphRepairBuilder
						.getNodeIncomingEdges(nodeId).get(0);
				CycleEdge toCycleEdge = graphRepairBuilder
						.getNodeOutcomingEdges(nodeId).get(0);

				boolean notCircle = !((fromCycleEdge.getFromNodeId() == nodeId && toCycleEdge
						.getToNodeId() == nodeId) || (fromCycleEdge
						.getFromNodeId() == toCycleEdge.getToNodeId()));

				if (notCircle
						&& (fromCycleEdge.getWayId().equals(toCycleEdge
								.getWayId()))) {
					graphRepairBuilder.removeNodeAndConnectEdges(nodeId);
				}

				continue;
			}

			if (graphRepairBuilder.getNodeIncomingEdges(nodeId).size() == 2
					&& graphRepairBuilder.getNodeOutcomingEdges(nodeId).size() == 2) {

				CycleEdge fromCycleEdgeFirstDirection = graphRepairBuilder
						.getNodeIncomingEdges(nodeId).get(0);
				CycleEdge fromCycleEdgeSecondDirection = graphRepairBuilder
						.getNodeIncomingEdges(nodeId).get(1);
				CycleEdge toCycleEdgeFirstDirection = graphRepairBuilder
						.getNodeOutcomingEdges(nodeId).get(0);
				CycleEdge toCycleEdgeSecondDirection = graphRepairBuilder
						.getNodeOutcomingEdges(nodeId).get(1);

				Set<Long> fromIds = new HashSet<>();
				fromIds.add(fromCycleEdgeFirstDirection.getFromNodeId());
				fromIds.add(fromCycleEdgeSecondDirection.getFromNodeId());

				Set<Long> toIds = new HashSet<>();
				toIds.add(toCycleEdgeFirstDirection.getToNodeId());
				toIds.add(toCycleEdgeSecondDirection.getToNodeId());

				if (fromIds.containsAll(toIds)) {
					Iterator<Long> iterator = fromIds.iterator();

					long fromByNodeId = iterator.next();
					long toByNodeId = iterator.next();

					boolean notCircle = !(fromByNodeId == nodeId && toByNodeId == nodeId);

					CycleEdge fromCycleEdgeFirstDirectionFN = graphRepairBuilder
							.getEdges(fromByNodeId, nodeId);
					CycleEdge fromCycleEdgeSecondDirectionNF = graphRepairBuilder
							.getEdges(nodeId, fromByNodeId);
					CycleEdge toCycleEdgeFirstDirectionNT = graphRepairBuilder
							.getEdges(nodeId, toByNodeId);
					CycleEdge toCycleEdgeSecondDirectionTN = graphRepairBuilder
							.getEdges(toByNodeId, nodeId);

					if (notCircle
							// first direction
							&& (fromCycleEdgeFirstDirectionFN.getWayId()
									.equals(toCycleEdgeFirstDirectionNT
											.getWayId()))
							// second direction
							&& (fromCycleEdgeSecondDirectionNF.getWayId()
									.equals(toCycleEdgeSecondDirectionTN
											.getWayId()))) {

						graphRepairBuilder
								.removeNodeBothDirectionAndConnectEdges(nodeId,
										fromByNodeId, toByNodeId);
					}

				}

			}

		}

		return graphRepairBuilder.createGraph();
	}
}
