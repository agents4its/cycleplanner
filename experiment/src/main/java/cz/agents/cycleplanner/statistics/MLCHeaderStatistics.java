package cz.agents.cycleplanner.statistics;

/**
 * Header of statistics.
 * 
 * Contains description of all statistics, especially for experiments with
 * multi-label correcting algorithm.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class MLCHeaderStatistics implements HeaderStatistics {

	private final String algorithmHeader = "Heuristic";

	private final String parametersHeader = "Heuristic param";

	private final String regionHeader = "Region";

	private final String indexHeader = "Index";

	private final String originIDHeader = "Origin ID";

	private final String destinationIDHeader = "Destination ID";

	private final String directDistanceHeader = "Direct distance";

	private final String optimalParetoSetSizeHeader = "Optimal Pareto set size";

	private final String heuristicParetoSetSizeHeader = "Heuristic Pareto set size";

	private final String numberOfIterationsHeader = "Number of itrerations";

	private final String runningTimeHeader = "Running time";

	private final String numberOfPlansDifferentFromOptimalSetHeader = "Number of heuristic plans from optimal Pareto set";

	private final String averageMinJacaardDistanceHeader = "Average minimal Jaccard Distance from optimal Pareto set to heuristic Pareto set";

	private final String averageMinCostSpaceDistanceHeader = "Average minimal distance in cost space from optimal Pareto set to heuristic Pareto set";

	private final String jaccardDistanceOfSetsOfEdgesHeader = "Jaccard distance of sets of edges";

	private final String maxDurationHeader = "Max duration";

	private final String minDurationHeader = "Min duration";

	private final String maxMaxComfortQuietnessHeader = "Max max(comfort quietness)";

	private final String minMaxComfortQuietnessHeader = "Min max(comfort quietness)";

	private final String maxFlatnessHeader = "Max flatness";

	private final String minFlatnessHeader = "Min flatness";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getHeader() {
		return new String[] { algorithmHeader, parametersHeader, regionHeader, indexHeader, originIDHeader,
				destinationIDHeader, directDistanceHeader, optimalParetoSetSizeHeader, heuristicParetoSetSizeHeader,
				numberOfIterationsHeader, runningTimeHeader, numberOfPlansDifferentFromOptimalSetHeader,
				averageMinJacaardDistanceHeader, averageMinCostSpaceDistanceHeader, jaccardDistanceOfSetsOfEdgesHeader,
				blankHeader, maxDurationHeader, minDurationHeader, maxMaxComfortQuietnessHeader,
				minMaxComfortQuietnessHeader, maxFlatnessHeader, minFlatnessHeader };
	}

}
