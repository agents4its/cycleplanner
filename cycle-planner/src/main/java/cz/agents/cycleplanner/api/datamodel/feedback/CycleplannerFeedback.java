package cz.agents.cycleplanner.api.datamodel.feedback;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CycleplannerFeedback {

	@JsonProperty(required = true)
	private final long responseId;
	@JsonProperty(required = true)
	private final int rating;
	private final Boolean forbiddenManoeuvers;
	private final Boolean badProfileCorrespondence;
	private final Boolean dangerousPlaces;
	private final Boolean pavementWhenNotNeeded;
	private final Boolean badMapData;
	private final List<TimedCoordinate> trackedJourney;

	@SuppressWarnings("unused")
	private CycleplannerFeedback() {
		this.responseId = Long.MAX_VALUE;
		this.rating = Integer.MAX_VALUE;
		this.forbiddenManoeuvers = null;
		this.badProfileCorrespondence = null;
		this.dangerousPlaces = null;
		this.pavementWhenNotNeeded = null;
		this.badMapData = null;
		this.trackedJourney = null;
	}

	public CycleplannerFeedback(long responseId, int rating, boolean forbiddenManoeuvers,
			boolean badProfileCorrespondence, boolean dangerousPlaces, boolean pavementWhenNotNeeded,
			boolean badMapData, List<TimedCoordinate> trackedJourney) {
		super();
		this.responseId = responseId;
		this.rating = rating;
		this.forbiddenManoeuvers = forbiddenManoeuvers;
		this.badProfileCorrespondence = badProfileCorrespondence;
		this.dangerousPlaces = dangerousPlaces;
		this.pavementWhenNotNeeded = pavementWhenNotNeeded;
		this.badMapData = badMapData;
		this.trackedJourney = trackedJourney;
	}

	@Override
	public String toString() {
		return "CycleplannerFeedback [responseId=" + responseId + ", rating=" + rating + ", forbiddenManoeuvers="
				+ forbiddenManoeuvers + ", badProfileCorrespondence=" + badProfileCorrespondence + ", dangerousPlaces="
				+ dangerousPlaces + ", pavementWhenNotNeeded=" + pavementWhenNotNeeded + ", badMapData=" + badMapData
				+ ", trackedJourney=" + trackedJourney + "]";
	}

	public long getResponseId() {
		return responseId;
	}

	public int getRating() {
		return rating;
	}

	public boolean isForbiddenManoeuvers() {
		return forbiddenManoeuvers;
	}

	public boolean isBadProfileCorrespondence() {
		return badProfileCorrespondence;
	}

	public boolean isDangerousPlaces() {
		return dangerousPlaces;
	}

	public boolean isPavementWhenNotNeeded() {
		return pavementWhenNotNeeded;
	}

	public boolean isBadMapData() {
		return badMapData;
	}

	public List<TimedCoordinate> getTrackedJourney() {
		return trackedJourney;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (badMapData ? 1231 : 1237);
		result = prime * result + (badProfileCorrespondence ? 1231 : 1237);
		result = prime * result + (dangerousPlaces ? 1231 : 1237);
		result = prime * result + (forbiddenManoeuvers ? 1231 : 1237);
		result = prime * result + (pavementWhenNotNeeded ? 1231 : 1237);
		result = prime * result + rating;
		result = prime * result + (int) (responseId ^ (responseId >>> 32));
		result = prime * result + ((trackedJourney == null) ? 0 : trackedJourney.hashCode());
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
		CycleplannerFeedback other = (CycleplannerFeedback) obj;
		if (badMapData != other.badMapData)
			return false;
		if (badProfileCorrespondence != other.badProfileCorrespondence)
			return false;
		if (dangerousPlaces != other.dangerousPlaces)
			return false;
		if (forbiddenManoeuvers != other.forbiddenManoeuvers)
			return false;
		if (pavementWhenNotNeeded != other.pavementWhenNotNeeded)
			return false;
		if (rating != other.rating)
			return false;
		if (responseId != other.responseId)
			return false;
		if (trackedJourney == null) {
			if (other.trackedJourney != null)
				return false;
		} else if (!trackedJourney.equals(other.trackedJourney))
			return false;
		return true;
	}

}
