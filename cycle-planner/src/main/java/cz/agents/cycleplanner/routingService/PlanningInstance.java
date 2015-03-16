package cz.agents.cycleplanner.routingService;

import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;



public final class PlanningInstance {

	private final Request request;
	private final long responseID;
	private final CycleNode originInGraph;
	private final CycleNode destinationInGraph;
	private final Graph<CycleNode, CycleEdge> graph;
	private final CityCycleData data;

	public PlanningInstance(Request request, long responseID, CycleNode originInGraph, CycleNode destinationInGraph,
			Graph<CycleNode, CycleEdge> graph, CityCycleData data) {

		this.request = request;
		this.responseID = responseID;
		this.originInGraph = originInGraph;
		this.destinationInGraph = destinationInGraph;
		this.graph = graph;
		this.data = data;
	}

	public Request getRequest() {
		return request;
	}

	public long getResponseID() {
		return responseID;
	}

	public CycleNode getOriginInGraph() {
		return originInGraph;
	}

	public CycleNode getDestinationInGraph() {
		return destinationInGraph;
	}

	public Graph<CycleNode, CycleEdge> getGraph() {
		return graph;
	}

	public CityCycleData getData() {
		return data;
	}
}
