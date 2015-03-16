package cz.agents.cycleplanner.originDestination;

import java.util.Random;

import org.apache.log4j.Logger;
import org.openstreetmap.osm.data.coordinates.LatLon;

import cz.agents.cycleplanner.api.datamodel.BoundingBox;
import eu.superhub.wp5.wp5common.location.GPSLocation;
import eu.superhub.wp5.wp5common.location.Location;

/**
 * A random generator of origin and destination.
 * 
 * Origin and destination are instances of <code>CycleNode</code>.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class OriginDestinationLocationGenerator implements OriginDestinationGenerator<Location> {
	private static final Logger log = Logger.getLogger(OriginDestinationLocationGenerator.class);

	private Random random;

	private double left;
	private double right;
	private double top;
	private double bottom;

	private int maxDirectDistance;
	private int minDirectDistance;

	public OriginDestinationLocationGenerator(long seed, BoundingBox boundingBox, int maxDirectDistance,
			int minDirectDistance) {
		random = new Random(seed);

		left = boundingBox.getLeftE6() / 1E6;
		right = boundingBox.getRightE6() / 1E6;
		top = boundingBox.getTopE6() / 1E6;
		bottom = boundingBox.getBottomE6() / 1E6;

		this.maxDirectDistance = maxDirectDistance;
		this.minDirectDistance = minDirectDistance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OriginDestinationPair<Location> getNextOriginDestination() {
		return generate(random);
	}

	/**
	 * Using random generator specified as argument randomly generate origin and
	 * destination which meets direct distance restrictions
	 * 
	 * @param generator
	 * @return OriginDestinationPair<Location>
	 * 
	 */
	private OriginDestinationPair<Location> generate(Random generator) {

		double originLat, originLon, destinationLat, destinationLon;
		int directDistance;

		do {
			// Randomly pick coordinates for origin and destination
			originLon = left + (right - left) * random.nextDouble();
			originLat = bottom + (top - bottom) * random.nextDouble();
			destinationLon = left + (right - left) * random.nextDouble();
			destinationLat = bottom + (top - bottom) * random.nextDouble();

			// Compute direct between origin and current destination
			directDistance = (int) Math.round(LatLon.distanceInMeters(originLat, originLon, destinationLat,
					destinationLon));

			// Check whether direct distance is less then maximum allowed or
			// whether direct distance is more then allowed minimum
		} while (directDistance > maxDirectDistance || directDistance < minDirectDistance);

		GPSLocation origin = new GPSLocation(originLat, originLon);
		GPSLocation destination = new GPSLocation(destinationLat, destinationLon);
		OriginDestinationPair<Location> originDestinationPair = new OriginDestinationPair<Location>(origin,
				destination, directDistance);
		log.info("generate " + originDestinationPair);

		return originDestinationPair;
	}

}
