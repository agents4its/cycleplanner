package cz.agents.cycleplanner.api.datamodel.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TimedCoordinate {

	@JsonProperty(required = true)
	private final int latE6;
	@JsonProperty(required = true)
	private final int lonE6;
	@JsonProperty(required = true)
	private final String timestamp;

	@SuppressWarnings("unused")
	private TimedCoordinate() {
		this.latE6 = Integer.MAX_VALUE;
		this.lonE6 = Integer.MAX_VALUE;
		this.timestamp = null;
	}

	public TimedCoordinate(int latE6, int lonE6, String timestamp) {
		super();
		this.latE6 = latE6;
		this.lonE6 = lonE6;
		this.timestamp = timestamp;
	}

	public int getLatE6() {
		return latE6;
	}

	public int getLonE6() {
		return lonE6;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + latE6;
		result = prime * result + lonE6;
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimedCoordinate other = (TimedCoordinate) obj;
		if (latE6 != other.latE6)
			return false;
		if (lonE6 != other.lonE6)
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimedCoordinate [latE6=" + latE6 + ", lonE6=" + lonE6 + ", timestamp=" + timestamp + "]";
	}

}
