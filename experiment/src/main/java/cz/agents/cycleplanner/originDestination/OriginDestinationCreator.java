package cz.agents.cycleplanner.originDestination;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Sets;

import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.experiment.Experiment;
import cz.agents.cycleplanner.routingService.City;

/**
 * 
 * Creator of serialized origin and destination pairs.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class OriginDestinationCreator {
	private static Logger log = Logger.getLogger(OriginDestinationCreator.class);

	private long seed;
	private City city;
	private OriginDestinationNodeGenerator originDestinationGenerator;
	private Set<Integer> badRequests;

	public OriginDestinationCreator(long seed, int maxDirectDistance, int minDirectDistance, City city,
			Set<Integer> badRequests) {

		this.seed = seed;
		this.city = city;
		this.originDestinationGenerator = new OriginDestinationNodeGenerator(seed, city, maxDirectDistance,
				minDirectDistance);
		this.badRequests = badRequests;
	}

	public static void main(String[] args) {

		// Prohibited requests for individual regions
		Set<Integer> regionABadRequests = Sets.newHashSet(115, 107, 63, 125, 52, 114, 45, 36, 58, 104, 62, 6, 127, 71,
				96, 33, 95, 101, 9, 27, 117, 20, 1, 14, 56, 41, 100, 10, 8, 92);
		Set<Integer> regionBBadRequests = Sets.newHashSet(2, 120, 9, 64, 6, 106, 25, 47, 66, 83, 16, 56, 104, 43, 89,
				26, 18, 62, 4, 92, 50, 55, 68, 78, 14, 33, 86, 58, 0, 119);
		Set<Integer> regionCBadRequests = Sets.newHashSet(100, 37, 20, 51, 83, 6, 5, 12, 28, 127, 47, 7, 55, 109, 108,
				2, 94, 4, 112, 19, 78, 17, 69, 66, 104, 59, 64, 54, 85, 27);

		OriginDestinationCreator originDestinationCreator = new OriginDestinationCreator(Experiment.SEED,
				Experiment.MAX_DIRECT_DISTANCE, Experiment.MIN_DIRECT_DISTANCE, City.PRAGUE_MEDIUM_A,
				regionABadRequests);

		originDestinationCreator.createAndStore(130);
	}

	/**
	 * Creates origin and destination pairs and stores it to file.
	 * 
	 * @param size
	 *            number of origin and destination pairs to create
	 */
	public void createAndStore(int size) {
		List<OriginDestinationPair<CycleNode>> listOfOriginDestionPairs = originDestinationGenerator
				.getListOfOriginDestinationPairs(seed, size);

		List<String[]> convertedListOfOriginDestionPairs = convertListOfOriginDestinationPair(listOfOriginDestionPairs,
				badRequests);

		storeListOfOriginDestionPairs(convertedListOfOriginDestionPairs, city.toString() + "_requests.csv");
	}

	/**
	 * Converts list of origin and destination pairs to list of
	 * <code>String</code> arrays.
	 * 
	 * @param originalPairs
	 *            list of origin and destination pairs
	 * @param badRequests
	 *            set of prohibited requests indexes
	 * @return
	 */
	private List<String[]> convertListOfOriginDestinationPair(List<OriginDestinationPair<CycleNode>> originalPairs,
			Set<Integer> badRequests) {
		List<String[]> convertedPairs = new ArrayList<>();

		int i = 0;
		int j = 0;

		for (OriginDestinationPair<CycleNode> originalPair : originalPairs) {

			if (!badRequests.contains(i++)) {
				String[] convertedPair = new String[3];

				convertedPair[0] = Integer.toString(j++);
				convertedPair[1] = Long.toString(originalPair.getOrigin().getId());
				convertedPair[2] = Long.toString(originalPair.getDestination().getId());

				convertedPairs.add(convertedPair);
			}
		}

		return convertedPairs;
	}

	/**
	 * Save origin and destination pairs to comma-separated file.
	 * 
	 * @param listOfOriginDestionPairs
	 *            list of origin and destination
	 * @param storageName
	 *            name of the comma-separated file
	 */
	private void storeListOfOriginDestionPairs(List<String[]> listOfOriginDestionPairs, String storageName) {

		try {
			CSVWriter csvWriter = new CSVWriter(new FileWriter(storageName));
			csvWriter.writeAll(listOfOriginDestionPairs);
			csvWriter.close();
		} catch (IOException e) {
			log.error("Storing list of origins and destinations pairs was not sucessful", e);
		}
	}

}
