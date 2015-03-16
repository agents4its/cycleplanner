package cz.agents.cycleplanner.experiment;

import cz.agents.cycleplanner.aStar.Profile;

/**
 * An experiment.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
// TODO move static variables to Property file, then we can define properties
// for different experiments
public interface Experiment {

	/**
	 * A random number generator seed.
	 */
	public final static long SEED = 103l;

	/**
	 * A maximal allowed direct Euclidean distance between origin and
	 * destination nodes.
	 */
	public final static int MAX_DIRECT_DISTANCE = 10000;

	/**
	 * A minimal allowed direct Euclidean distance between origin and
	 * destination nodes.
	 */
	public final static int MIN_DIRECT_DISTANCE = 500;

	/**
	 * A number of experiment's iterations.
	 */
	public final static int NUMBER_OF_EXECUTIONS = 1;
	
	/**
	 * A starting query index.
	 */
	public final static int STARTING_INDEX = 0;

	/**
	 * An average cruising speed in kilometers per hour.
	 */
	public final static double AVERAGE_SPEED_KILOMETERS_PER_HOUR = 13.68;

	/**
	 * An average cruising speed in meters per second.
	 */
	public final static double AVERAGE_SPEED_METERS_PER_SECOND = AVERAGE_SPEED_KILOMETERS_PER_HOUR / 3.6;

	/**
	 * A weight of travel time criterion.
	 */
	public final static double TRAVEL_TIME_WEIGHT = 0d;

	/**
	 * A weight of comfort criterion.
	 */
	public final static double COMFORT_WEIGHT = 0d;

	/**
	 * A weight of quietness criterion.
	 */
	public final static double QUIETNESS_WEIGHT = 0d;

	/**
	 * A weight of flatness criterion.
	 */
	public final static double FLATNESS_WEIGHT = 0d;

	/**
	 * A cyclist's preference profile.
	 */
	public final static Profile PROFILE = new Profile(TRAVEL_TIME_WEIGHT, COMFORT_WEIGHT, QUIETNESS_WEIGHT,
			FLATNESS_WEIGHT);

	/**
	 * Runs experiment.
	 */
	public void run();
}
