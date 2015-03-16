package cz.agents.cycleplanner.originDestination;

/**
 * An origin and a destination.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 * @param <T>
 *            origin and destination type
 */
public class OriginDestinationPair<T> {

	/**
	 * An origin object.
	 */
	private T origin;

	/**
	 * An destination object.
	 */
	private T destination;

	/**
	 * A direct Euclidean distance between origin and destination. Measured in
	 * meters.
	 */
	private int directDistance;

	public OriginDestinationPair(T origin, T destination, int directDistance) {

		this.origin = origin;
		this.destination = destination;
		this.directDistance = directDistance;
	}

	/**
	 * Returns the origin.
	 * 
	 * @return origin object
	 */
	public T getOrigin() {
		return origin;
	}

	/**
	 * Returns the destination.
	 * 
	 * @return destination object
	 */
	public T getDestination() {
		return destination;
	}

	/**
	 * Returns direct distance between origin and destination.
	 * 
	 * Measured in meters.
	 * 
	 * @return direct Euclidean distance
	 */
	public int getDirectDistance() {
		return directDistance;
	}

	@Override
	public String toString() {
		return "OriginDestinationPair [origin=" + origin + ", destination=" + destination + ", directDistance="
				+ directDistance + "]";
	}
}
