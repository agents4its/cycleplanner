package cz.agents.cycleplanner.api.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Coordinate {
	@JsonProperty(required = true)
	private final int latE6;
	@JsonProperty(required = true)
	private final int lonE6;

	@SuppressWarnings("unused")
	private Coordinate() {
		this.latE6 = Integer.MAX_VALUE;
		this.lonE6 = Integer.MAX_VALUE;
	}

	public Coordinate(int latE6, int lonE6) {
		super();
		this.latE6 = latE6;
		this.lonE6 = lonE6;
	}

	public int getLatE6() {
		return latE6;
	}

	public int getLonE6() {
		return lonE6;
	}

	@Override
	public String toString() {
		return "Coordinate [latE6=" + latE6 + ", lonE6=" + lonE6 + "]";
	}
}
