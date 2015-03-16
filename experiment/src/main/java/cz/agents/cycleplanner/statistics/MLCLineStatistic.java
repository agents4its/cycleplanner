package cz.agents.cycleplanner.statistics;

/**
 * Line of statistics.
 * 
 * Contains value for each statistic. Its design specially for experiments with
 * multi-label correcting algorithm
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class MLCLineStatistic implements LineStatistic {

	private final String algorithm;

	private final String parameters;

	private final String region;

	private final String index;

	private final String originID;

	private final String destinationID;

	private final String directDistance;

	private final String optimalParetoSetSize;

	private final String heuristicParetoSetSize;

	private final String numberOfIterations;

	private final String runningTime;

	private final String numberOfPlansDifferentFromOptimalSet;

	private final String averageMinJacaardDistance;

	private final String averageMinCostSpaceDistance;

	private final String jaccardDistanceOfSetsOfEdges;

	private final String maxDuration;

	private final String minDuration;

	private final String maxMaxComfortQuietness;

	private final String minMaxComfortQuietness;

	private final String maxFlatness;

	private final String minFlatness;

	public MLCLineStatistic(String algorithm, String parameters, String region, int index, long originID,
			long destinationID, int directDistance, int optimalParetoSetSize, int heuristicParetoSetSize,
			long numberOfIterations, long runningTime, int numberOfPlansDifferentFromOptimalSet,
			double averageMinJacaardDistance, double averageMinCostSpaceDistance, double jaccardDistanceOfSetsOfEdges,
			int maxDuration, int minDuration, int maxMaxComfortQuietness, int minMaxComfortQuietness, int maxFlatness,
			int minFlatness) {

		this.algorithm = algorithm;
		this.parameters = parameters;
		this.region = region;
		this.index = Integer.toString(index);
		this.originID = Long.toString(originID);
		this.destinationID = Long.toString(destinationID);
		this.directDistance = Integer.toString(directDistance);
		this.optimalParetoSetSize = Integer.toString(optimalParetoSetSize);
		this.heuristicParetoSetSize = Integer.toString(heuristicParetoSetSize);
		this.numberOfIterations = Long.toString(numberOfIterations);
		this.runningTime = Long.toString(runningTime);
		this.numberOfPlansDifferentFromOptimalSet = Integer.toString(numberOfPlansDifferentFromOptimalSet);
		this.averageMinJacaardDistance = Double.toString(averageMinJacaardDistance);
		this.averageMinCostSpaceDistance = Double.toString(averageMinCostSpaceDistance);
		this.jaccardDistanceOfSetsOfEdges = Double.toString(jaccardDistanceOfSetsOfEdges);
		this.maxDuration = Integer.toString(maxDuration);
		this.minDuration = Integer.toString(minDuration);
		this.maxMaxComfortQuietness = Integer.toString(maxMaxComfortQuietness);
		this.minMaxComfortQuietness = Integer.toString(minMaxComfortQuietness);
		this.maxFlatness = Integer.toString(maxFlatness);
		this.minFlatness = Integer.toString(minFlatness);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getLine() {

		return new String[] { algorithm, parameters, region, index, originID, destinationID, directDistance,
				optimalParetoSetSize, heuristicParetoSetSize, numberOfIterations, runningTime,
				numberOfPlansDifferentFromOptimalSet, averageMinJacaardDistance, averageMinCostSpaceDistance,
				jaccardDistanceOfSetsOfEdges, blank, maxDuration, minDuration, maxMaxComfortQuietness,
				minMaxComfortQuietness, maxFlatness, minFlatness };
	}

}
