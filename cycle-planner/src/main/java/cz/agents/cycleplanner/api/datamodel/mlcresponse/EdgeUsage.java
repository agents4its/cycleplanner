package cz.agents.cycleplanner.api.datamodel.mlcresponse;

import cz.agents.cycleplanner.api.datamodel.Coordinate;

// TODO rename
public class EdgeUsage {
	private final Coordinate from;
	private final Coordinate to;
	private final double width;
	private final String colour;

	@SuppressWarnings("unused")
	private EdgeUsage() {
		this.from = null;
		this.to = null;
		this.width = Double.MAX_VALUE;
		this.colour = null;
	}

	public EdgeUsage(Coordinate from, Coordinate to, double width, String colour) {
		super();
		this.from = from;
		this.to = to;
		this.width = width;
		this.colour = colour;
	}

	public Coordinate getFrom() {
		return from;
	}

	public Coordinate getTo() {
		return to;
	}

	public double getWidth() {
		return width;
	}

	public String getColour() {
		return colour;
	}
}
