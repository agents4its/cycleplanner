package cz.agents.cycleplanner.originDestination;

/**
 * A generator of origin and destination.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 * @param <T>
 */
public interface OriginDestinationGenerator<T> {

	/**
	 * Returns origin and destination next in order.
	 * 
	 * @return origin and destination
	 */
	public OriginDestinationPair<T> getNextOriginDestination();
}
