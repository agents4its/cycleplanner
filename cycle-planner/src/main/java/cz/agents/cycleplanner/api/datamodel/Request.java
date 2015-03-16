package cz.agents.cycleplanner.api.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import cz.agents.cycleplanner.aStar.Profile;

public class Request {

	@JsonProperty(required = true)
	private final double startLatitude;
	@JsonProperty(required = true)
	private final double startLongitude;
	@JsonProperty(required = true)
	private final double endLatitude;
	@JsonProperty(required = true)
	private final double endLongitude;
	@JsonProperty(required = true)
	private final double averageSpeed;
	@JsonProperty(required = true)
	private final double travelTimeWeight;
	@JsonProperty(required = true)
	private final double comfortWeight;
	@JsonProperty(required = true)
	private final double quietnessWeight;
	@JsonProperty(required = true)
	private final double flatnessWeight;

	@SuppressWarnings("unused")
	private Request() {
		this.startLatitude = Double.MAX_VALUE;
		this.startLongitude = Double.MAX_VALUE;
		this.endLatitude = Double.MAX_VALUE;
		this.endLongitude = Double.MAX_VALUE;
		this.averageSpeed = Double.MAX_VALUE;
		this.travelTimeWeight = Double.MAX_VALUE;
		this.comfortWeight = Double.MAX_VALUE;
		this.quietnessWeight = Double.MAX_VALUE;
		this.flatnessWeight = Double.MAX_VALUE;
	}

	public Request(double startLatitude, double startLongitude, double endLatitude, double endLongitude,
			double averageSpeed, double travelTimeWeight, double comfortWeight, double quietnessWeight,
			double flatnessWeight) {
		this.startLatitude = startLatitude;
		this.startLongitude = startLongitude;
		this.endLatitude = endLatitude;
		this.endLongitude = endLongitude;
		this.averageSpeed = averageSpeed;
		this.travelTimeWeight = travelTimeWeight;
		this.comfortWeight = comfortWeight;
		this.quietnessWeight = quietnessWeight;
		this.flatnessWeight = flatnessWeight;
	}

	public double getStartLatitude() {
		return startLatitude;
	}

	public double getStartLongitude() {
		return startLongitude;
	}

	public double getEndLatitude() {
		return endLatitude;
	}

	public double getEndLongitude() {
		return endLongitude;
	}

	/* kilometers per hour */
	public double getAverageSpeed() {
		return averageSpeed;
	}

	public Profile getProfile() {
		return new Profile(travelTimeWeight, comfortWeight, quietnessWeight, flatnessWeight);
	}

	@Override
	public String toString() {
		return "Request [startLatitude=" + startLatitude + ", startLongitude=" + startLongitude + ", endLatitude="
				+ endLatitude + ", endLongitude=" + endLongitude + ", averageSpeed=" + averageSpeed
				+ ", travelTimeWeight=" + travelTimeWeight + ", comfortWeight=" + comfortWeight + ", quietnessWeight="
				+ quietnessWeight + ", flatnessWeight=" + flatnessWeight + "]";
	}
}
