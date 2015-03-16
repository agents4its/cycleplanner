package cz.agents.cycleplanner.api.datamodel.mlcresponse;

import java.util.List;

import cz.agents.cycleplanner.api.datamodel.Coordinate;

// TODO rename
public class MLCResponse {

	private final Coordinate origin;
	private final Coordinate destination;
	private final List<EdgeUsage> edges;

	@SuppressWarnings("unused")
	private MLCResponse() {
		this.origin = null;
		this.destination = null;
		this.edges = null;
	}

	public MLCResponse(Coordinate origin, Coordinate destination, List<EdgeUsage> edges) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.edges = edges;
	}

	public Coordinate getOrigin() {
		return origin;
	}

	public Coordinate getDestination() {
		return destination;
	}

	public List<EdgeUsage> getEdges() {
		return edges;
	}

}
