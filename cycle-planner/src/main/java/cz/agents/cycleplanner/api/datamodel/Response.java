package cz.agents.cycleplanner.api.datamodel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

	@JsonProperty(required = true)
	private final long responseId;
	@JsonProperty(required = true)
	private final ResponseStatus status;
	private final BoundingBox boundingBox;
	private final int length;
	private final int duration;
	private final int elevationGain;
	private final int elevationDrop;
	private final int consumedEnergy;
	private final List<Coordinate> coordinates;
	private final List<Integer> elevationProfile;
	private final List<Integer> comulativeDistance;
	private final List<Instruction> instructions;

	@SuppressWarnings("unused")
	private Response() {
		this.responseId = Long.MAX_VALUE;
		this.status = null;
		this.boundingBox = null;
		this.length = Integer.MAX_VALUE;
		this.duration = Integer.MAX_VALUE;
		this.elevationGain = Integer.MAX_VALUE;
		this.elevationDrop = Integer.MAX_VALUE;
		this.consumedEnergy = Integer.MAX_VALUE;
		this.coordinates = null;
		this.elevationProfile = null;
		this.comulativeDistance = null;
		this.instructions = null;
	}

	public Response(long responseId, ResponseStatus status, BoundingBox boundingBox, int length, int duration,
			int elevationGain, int elevationDrop, int consumedEnergy, List<Coordinate> journeyPlan,
			List<Integer> elevationProfile, List<Integer> comulativeDistance, List<Instruction> instructions) {

		this.responseId = responseId;
		this.status = status;
		this.boundingBox = boundingBox;
		this.length = length;
		this.duration = duration;
		this.elevationGain = elevationGain;
		this.elevationDrop = elevationDrop;
		this.consumedEnergy = consumedEnergy;
		this.coordinates = journeyPlan;
		this.elevationProfile = elevationProfile;
		this.comulativeDistance = comulativeDistance;
		this.instructions = instructions;
	}

	public Response(long responseId, ResponseStatus status) {

		this.responseId = responseId;
		this.status = status;
		this.boundingBox = null;
		this.coordinates = null;
		this.length = Integer.MAX_VALUE;
		this.duration = Integer.MAX_VALUE;
		this.elevationGain = Integer.MAX_VALUE;
		this.elevationDrop = Integer.MAX_VALUE;
		this.consumedEnergy = Integer.MAX_VALUE;
		this.elevationProfile = null;
		this.comulativeDistance = null;
		this.instructions = null;
	}

	public long getResponseId() {
		return responseId;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public int getLength() {
		return length;
	}

	public int getDuration() {
		return duration;
	}

	public int getElevationGain() {
		return elevationGain;
	}

	public int getElevationDrop() {
		return elevationDrop;
	}

	public int getConsumedEnergy() {
		return consumedEnergy;
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public List<Integer> getElevationProfile() {
		return elevationProfile;
	}

	public List<Integer> getComulativeDistance() {
		return comulativeDistance;
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}

	@Override
	public String toString() {
		return "Response [responseId=" + responseId + ", status=" + status + ", boundingBox=" + boundingBox
				+ ", length=" + length + ", duration=" + duration + ", elevationGain=" + elevationGain
				+ ", elevationDrop=" + elevationDrop + ", consumedEnergy=" + consumedEnergy + ", coordinates="
				+ coordinates + ", elevationProfile=" + elevationProfile + ", comulativeDistance=" + comulativeDistance
				+ ", instructions=" + instructions + "]";
	}

}
