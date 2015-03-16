package cz.agents.cycleplanner.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cz.agents.cycleplanner.MLC.Label;
import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.MLC.MLCCycleCostFunction;
import cz.agents.cycleplanner.MLC.alg.AbstractMultiLabelCorrectingAlgorithm;
import cz.agents.cycleplanner.arguments.mlc.MLCAlgorithmParameter;
import cz.agents.cycleplanner.arguments.mlc.MLCCommandLineArgumentsParser;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.originDestination.OriginDestinationGenerator;
import cz.agents.cycleplanner.originDestination.OriginDestinationNodeLoader;
import cz.agents.cycleplanner.originDestination.OriginDestinationPair;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.RoutingService;
import cz.agents.cycleplanner.statistics.ExperimentStatistics;
import cz.agents.cycleplanner.statistics.MLCHeaderStatistics;
import cz.agents.cycleplanner.statistics.MLCLineStatistic;
import cz.agents.cycleplanner.util.EuclideanDistanceCalculator;
import cz.agents.cycleplanner.util.JaccardDistanceCalculator;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

/**
 * An implementation of an experiment on multi-label correcting algorithm.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class MLCExperiment implements Experiment {

	private final static Logger log = Logger.getLogger(MLCExperiment.class);

	private OriginDestinationGenerator<CycleNode> originDestinationGenerator;
	private MLCCommandLineArgumentsParser argumentParser;
	private City city;
	private Graph<CycleNode, CycleEdge> graph;
	private MLCCostFunction<CycleNode, CycleEdge> costFunction;
	private String resultsDirectoryName;

	private Set<String> heuristicParametersNames = Sets.newHashSet("buckets", "gamma", "aOverB",
			"epsilon", "alpha");

	public MLCExperiment(MLCCommandLineArgumentsParser argumentParser) {
		this.argumentParser = argumentParser;
		this.city = argumentParser.getCity();
		this.originDestinationGenerator = new OriginDestinationNodeLoader(this.city);

		RoutingService routingService = RoutingService.INSTANCE;
		this.graph = routingService.getCycleGraph(this.city);

		costFunction = new MLCCycleCostFunction(AVERAGE_SPEED_KILOMETERS_PER_HOUR);

		resultsDirectoryName = argumentParser.getResultsLocation() + "mlc-final-experiment/" + city.toString() + "/"
				+ argumentParser.getAlgorithm().getSimpleName() + "/";

		// Directory for results does not have to exist
		File resultsDirectory = new File(resultsDirectoryName);
		resultsDirectory.mkdirs();
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {

		AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlcAlgorithm = null;

		ExperimentStatistics statistics = new ExperimentStatistics(new MLCHeaderStatistics());

		for (int i = 0; i < NUMBER_OF_EXECUTIONS; i++) {

			// Generate origin and destination
			OriginDestinationPair<CycleNode> originDestinationPair = originDestinationGenerator
					.getNextOriginDestination();
			log.info(originDestinationPair);

			if (i < STARTING_INDEX) {
				continue;
			}

			log.info("Loading optimal Pareto set...");
			Collection<Collection<CycleEdge>> optimalParetoSetOfPaths = loadParetoSet(i);
			Set<CycleEdge> optimalParetoSetOfEdges = flattenToSetOfEdges(optimalParetoSetOfPaths);

			log.info("Initializing MLC algorithm...");
			mlcAlgorithm = getMultiLabelCorrectingAlgorithm(originDestinationPair.getOrigin(),
					originDestinationPair.getDestination());

			log.info("Initialize MLC implementation: " + mlcAlgorithm.getClass().getSimpleName() + " "
					+ getValueOfHeuristicParameters(mlcAlgorithm));

			// Run MLC algorithm
			mlcAlgorithm.call();

			Collection<Collection<CycleEdge>> heuristicParetoSetOfPaths = mlcAlgorithm.getPathsEdges();
			Set<CycleEdge> heuristicParetoSetOfEdges = flattenToSetOfEdges(heuristicParetoSetOfPaths);
			
			EdgeOverlapping edgeOverlapping = new EdgeOverlapping();
			edgeOverlapping.neviemAkoPomenovat(originDestinationPair.getOrigin(),
					originDestinationPair.getDestination(), heuristicParetoSetOfPaths, 
					new File(resultsDirectoryName
							+ mlcAlgorithm.getClass().getSimpleName() 
							+ "-"
							+ getValueOfHeuristicParameters(mlcAlgorithm) 
							+ city.toString() 
							+ "-" 
							+ i 
							+ ".json"));

			log.info("Serializing Pareto set of cycle plans...");
			serializeParetoSet(mlcAlgorithm.getPathsEdges(), new File(resultsDirectoryName + "pareto_set_" + i
					+ ".javaobject"));

			int numOfJointPlans = getNumberOfJointPlans(optimalParetoSetOfPaths, heuristicParetoSetOfPaths);
			log.info("Number of joint plans: " + numOfJointPlans);

			double averageMinJaccardDistance = computeAverageMinJaccardDistance(optimalParetoSetOfPaths,
					heuristicParetoSetOfPaths);
			log.info("Average minimal Jaccard Distance from optimal Pareto set to heuristic Pareto set is "
					+ averageMinJaccardDistance);

			double averageMinCostSpaceDistance = computeAverageMinCostSpaceDistance(optimalParetoSetOfPaths,
					heuristicParetoSetOfPaths);
			log.info("Average minimal distance in cost space from optimal Pareto set to heuristic Pareto set is "
					+ averageMinCostSpaceDistance);

			double jdEdges = JaccardDistanceCalculator.calculate(optimalParetoSetOfEdges, heuristicParetoSetOfEdges);
			log.info("Jaccard distance of sets of edges of all plans: " + jdEdges);

			Collection<Label<CycleNode>> destinationBag = mlcAlgorithm.getDestinationBag();
			int[][] maxMinOfLabels = getMaximumAndMinimumOfLabels(destinationBag);

			statistics.add(new MLCLineStatistic(mlcAlgorithm.getClass().getSimpleName(),
					getValueOfHeuristicParameters(mlcAlgorithm), city.toString(), i, originDestinationPair.getOrigin()
							.getId(), originDestinationPair.getDestination().getId(), originDestinationPair
							.getDirectDistance(), optimalParetoSetOfPaths.size(), mlcAlgorithm.getParetoSetSize(),
					mlcAlgorithm.getIterations(), mlcAlgorithm.getPlanningTime(), numOfJointPlans,
					averageMinJaccardDistance, averageMinCostSpaceDistance, jdEdges, maxMinOfLabels[0][0],
					maxMinOfLabels[0][1], maxMinOfLabels[1][0], maxMinOfLabels[1][1], maxMinOfLabels[2][0],
					maxMinOfLabels[2][1]));

		}

		statistics.write(new File(resultsDirectoryName + mlcAlgorithm.getClass().getSimpleName() + "-"
				+ getValueOfHeuristicParameters(mlcAlgorithm) + city.toString() + ".csv"));

	}

	/**
	 * Loads serialized Pareto set of cycle plans.
	 * 
	 * @param paretoSetIndex
	 *            Pareto set identifier
	 * @return collection of cycle plans
	 */
	@SuppressWarnings("unchecked")
	private Collection<Collection<CycleEdge>> loadParetoSet(int paretoSetIndex) {
		String pathToParetoSet = "/" + city + "/pareto_set_" + paretoSetIndex + ".javaobject";
		Collection<Collection<CycleEdge>> loadedParetoSet = null;

		try {
			ObjectInputStream ois = new ObjectInputStream(this.getClass().getResourceAsStream(pathToParetoSet));

			loadedParetoSet = (Collection<Collection<CycleEdge>>) ois.readObject();

			ois.close();

		} catch (FileNotFoundException e) {
			log.error("Could not find a file! " + e.getMessage(), e);
		} catch (IOException | ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return loadedParetoSet;
	}

	/**
	 * Flatten collection of cycle plans to set of <code>CycleEdge</code>.
	 * 
	 * @param paretoSetOfPaths
	 *            collection of cycle plans
	 * @return set of <code>CycleEdge</code>
	 */
	private Set<CycleEdge> flattenToSetOfEdges(Collection<Collection<CycleEdge>> paretoSetOfPaths) {
		Set<CycleEdge> paretoSetOfEdges = new HashSet<>();

		for (Collection<CycleEdge> paths : paretoSetOfPaths) {
			for (CycleEdge cycleEdge : paths) {
				paretoSetOfEdges.add(cycleEdge);
			}
		}

		return paretoSetOfEdges;
	}

	/**
	 * Instantiate multi-label correcting algorithm.
	 * 
	 * @param graph
	 *            cycleway planning graph created for multi-label correcting
	 *            algorithm
	 * @param origin
	 *            search starts at this node
	 * @param destination
	 *            search ends at this node
	 * @return instance of <AbstractMultiLabelCorrectingAlgorithm> with set
	 *         parameters specified as arguments
	 */
	@SuppressWarnings("unchecked")
	private AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> getMultiLabelCorrectingAlgorithm(
			CycleNode origin, CycleNode destination) {
		AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> alg = null;

		try {
			Class<?> algClass = argumentParser.getAlgorithm();
			Constructor<?> algConstructor = algClass.getConstructor(Graph.class, Node.class, Node.class,
					MLCCostFunction.class);

			// get all parameters
			Map<String, Field> algParameters = new HashMap<String, Field>();
			Class<?> clazz = algClass;

			while (clazz != null && clazz != Object.class) {
				for (Field field : clazz.getDeclaredFields()) {
					if (!field.isSynthetic()) {
						algParameters.put(field.getName(), field);
					}
				}
				clazz = clazz.getSuperclass();
			}

			alg = (AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge>) algConstructor.newInstance(graph,
					origin, destination, costFunction);

			for (Entry<String, MLCAlgorithmParameter> entry : argumentParser.getMLCAlgorithmParameters().entrySet()) {
				if (algParameters.containsKey(entry.getKey())) {
					Field f = algParameters.get(entry.getKey());
					entry.getValue().set(f, alg);
				}
			}
		} catch (InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException
				| NoSuchMethodException | SecurityException e) {
			log.error("Creating instance of MLC algorithm failed! " + e.getMessage(), e);
		}
		return alg;
	}

	/**
	 * Serialize Pareto set of cycle plans.
	 * 
	 * @param paretoSet
	 *            collection of cycle plans
	 * @param f
	 *            file where Pareto set is serialized in
	 */
	private void serializeParetoSet(Collection<Collection<CycleEdge>> paretoSet, File f) {
		try {

			FileOutputStream fos = new FileOutputStream(f);

			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(paretoSet);
			oos.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Calculate number of cycle plans that are present in sub-optimal Pareto
	 * set as well as in optimal Pareto set
	 * 
	 * @param optimal
	 *            optimal pareto set of cycle plans
	 * @param subOptimal
	 *            sub-optimal pareto set of cycle plans
	 * @return number of joint cycle plans among two sets
	 */
	private int getNumberOfJointPlans(Collection<Collection<CycleEdge>> optimal,
			Collection<Collection<CycleEdge>> subOptimal) {
		int count = 0;

		for (Collection<CycleEdge> subOptimalPlan : subOptimal) {

			for (Collection<CycleEdge> optimalPlan : optimal) {
				boolean areEqual = isFirstPlanEqualsSecond(subOptimalPlan, optimalPlan);

				if (areEqual) {
					count++;
					break;
				}
			}
		}

		return count;
	}

	/**
	 * Compares first specified cycle plan to second.
	 * 
	 * @param first
	 *            cycle plan
	 * @param second
	 *            cycle plan
	 * @return <code>true</code> if first plan is equal to second, otherwise
	 *         <code>false</code>
	 */
	private boolean isFirstPlanEqualsSecond(Collection<CycleEdge> first, Collection<CycleEdge> second) {

		if (first.size() != second.size()) {
			return false;
		}

		Iterator<CycleEdge> firstIterator = first.iterator();
		Iterator<CycleEdge> secondIterator = second.iterator();

		while (firstIterator.hasNext() && secondIterator.hasNext()) {

			if (!firstIterator.next().equals(secondIterator.next())) {
				return false;
			}

		}

		return true;
	}

	/**
	 * 
	 * Calculates average minimal Jaccard distance between plans in optimal
	 * Pareto set and sub-optimal.
	 * 
	 * @param optimalParetoSetOfPaths
	 *            optimal pareto set of cycle plans
	 * @param subOptimalParetoSetOfPaths
	 *            sub-optimal pareto set of cycle plans
	 * @return average minimal distance Jaccard distance
	 */
	private double computeAverageMinJaccardDistance(Collection<Collection<CycleEdge>> optimalParetoSetOfPaths,
			Collection<Collection<CycleEdge>> subOptimalParetoSetOfPaths) {

		Set<Set<CycleEdge>> optimalParetoSetOfSetsOfEdges = new HashSet<>();

		for (Collection<CycleEdge> collection : optimalParetoSetOfPaths) {
			Set<CycleEdge> optimal = new HashSet<>(collection);

			optimalParetoSetOfSetsOfEdges.add(optimal);
		}

		Set<Set<CycleEdge>> heuristicParetoSetOfSetsOfEdges = new HashSet<>();

		for (Collection<CycleEdge> collection : subOptimalParetoSetOfPaths) {
			Set<CycleEdge> heuristic = new HashSet<>(collection);

			heuristicParetoSetOfSetsOfEdges.add(heuristic);
		}

		double averageMin = 0;

		for (Set<CycleEdge> optimal : optimalParetoSetOfSetsOfEdges) {

			double min = Double.MAX_VALUE;

			for (Set<CycleEdge> heuristic : heuristicParetoSetOfSetsOfEdges) {
				double jaccardDistance = JaccardDistanceCalculator.calculate(optimal, heuristic);

				min = (min > jaccardDistance) ? jaccardDistance : min;
				// log.debug("min JD: "+min +" JD: "+jaccardDistance);
			}

			averageMin += min;
		}

		return averageMin / optimalParetoSetOfSetsOfEdges.size();
	}

	/**
	 * Calculates average minimal distance in cost space between plans in
	 * optimal Pareto set and sub-optimal.
	 * 
	 * @param optimalParetoSetOfPaths
	 *            optimal pareto set of cycle plans
	 * @param subOptimalParetoSetOfPaths
	 *            sub-optimal pareto set of cycle plans
	 * @return average minimal distance in cost space
	 */
	private double computeAverageMinCostSpaceDistance(Collection<Collection<CycleEdge>> optimalParetoSetOfPaths,
			Collection<Collection<CycleEdge>> subOptimalParetoSetOfPaths) {

		double[] minCostVector = new double[3];
		double[] maxCostVector = new double[3];

		Arrays.fill(minCostVector, Double.MAX_VALUE);
		Arrays.fill(maxCostVector, Double.MIN_VALUE);

		Set<double[]> optimalCostVectors = new HashSet<>();

		for (Collection<CycleEdge> collection : optimalParetoSetOfPaths) {

			double[] costVector = new double[3];

			for (CycleEdge cycleEdge : collection) {
				int[] cycleEdgeCostVector = costFunction.getCostVector(cycleEdge.getFromNode(), cycleEdge.getToNode(),
						cycleEdge);

				for (int i = 0; i < costVector.length; i++) {
					costVector[i] += cycleEdgeCostVector[i];
				}
			}

			for (int i = 0; i < costVector.length; i++) {
				minCostVector[i] = (minCostVector[i] > costVector[i]) ? costVector[i] : minCostVector[i];
				maxCostVector[i] = (maxCostVector[i] < costVector[i]) ? costVector[i] : maxCostVector[i];
			}

			optimalCostVectors.add(costVector);
		}

		// log.info("mins: " + Arrays.toString(minCostVector));
		// log.info("max: " + Arrays.toString(maxCostVector));

		Set<double[]> heuristicCostVectors = new HashSet<>();

		for (Collection<CycleEdge> collection : subOptimalParetoSetOfPaths) {

			double[] costVector = new double[3];

			for (CycleEdge cycleEdge : collection) {
				int[] cycleEdgeCostVector = costFunction.getCostVector(cycleEdge.getFromNode(), cycleEdge.getToNode(),
						cycleEdge);

				for (int i = 0; i < costVector.length; i++) {
					costVector[i] += cycleEdgeCostVector[i];
				}
			}

			for (int i = 0; i < costVector.length; i++) {
				minCostVector[i] = (minCostVector[i] > costVector[i]) ? costVector[i] : minCostVector[i];
				maxCostVector[i] = (maxCostVector[i] < costVector[i]) ? costVector[i] : maxCostVector[i];
			}

			heuristicCostVectors.add(costVector);
		}

		for (double[] optimalCostVector : optimalCostVectors) {

			// log.debug("optimal before: "+Arrays.toString(optimalCostVector));
			for (int i = 0; i < optimalCostVector.length; i++) {
				optimalCostVector[i] = (optimalCostVector[i] - minCostVector[i])
						/ (maxCostVector[i] - minCostVector[i]);
			}

			// log.debug("optimal after: "+Arrays.toString(optimalCostVector));
		}

		for (double[] heuristicCostVector : heuristicCostVectors) {

			// log.debug("heuristic before: " +
			// Arrays.toString(heuristicCostVector));
			for (int i = 0; i < heuristicCostVector.length; i++) {
				heuristicCostVector[i] = (heuristicCostVector[i] - minCostVector[i])
						/ (maxCostVector[i] - minCostVector[i]);
			}

			// log.debug("heuristic after: " +
			// Arrays.toString(heuristicCostVector));
		}

		double averageMin = 0;

		for (double[] optimalCostVector : optimalCostVectors) {

			double min = Double.MAX_VALUE;

			for (double[] heuristicCostVector : heuristicCostVectors) {

				double euclideanDistance = EuclideanDistanceCalculator
						.calculate(optimalCostVector, heuristicCostVector);

				min = (min > euclideanDistance) ? euclideanDistance : min;
			}

			averageMin += min;
		}

		return averageMin / optimalCostVectors.size();
	}

	/**
	 * Collect maximal and minimal value of criteria for final plans.
	 * 
	 * @param destinationBag
	 *            collection of labels from destination node
	 * @return maximal and minimal value for each criteria
	 */
	private int[][] getMaximumAndMinimumOfLabels(Collection<Label<CycleNode>> destinationBag) {

		int[][] maxMinOfLabels = new int[3][2];

		for (int i = 0; i < maxMinOfLabels.length; i++) {
			maxMinOfLabels[i][0] = Integer.MIN_VALUE;
			maxMinOfLabels[i][1] = Integer.MAX_VALUE;
		}

		for (Label<CycleNode> label : destinationBag) {
			for (int i = 0; i < maxMinOfLabels.length; i++) {

				if (maxMinOfLabels[i][0] <= label.getCostVector()[i]) {
					maxMinOfLabels[i][0] = label.getCostVector()[i];
				}

				if (maxMinOfLabels[i][1] > label.getCostVector()[i]) {
					maxMinOfLabels[i][1] = label.getCostVector()[i];
				}
			}
		}

		return maxMinOfLabels;
	}

	/**
	 * Returns values of multi-label correcting parameters especially those
	 * related to speedup techniques.
	 * 
	 * @return values of multi-label correcting parameters as
	 *         <code>String</code>
	 */
	private String getValueOfHeuristicParameters(AbstractMultiLabelCorrectingAlgorithm<?, ?> mlcAlgorithm) {
		StringBuilder builder = new StringBuilder();

		try {
			List<Field> heuristicParameters = new ArrayList<Field>();

			Class<?> i = mlcAlgorithm.getClass();
			while (i != null && i != Object.class) {
				for (Field field : i.getDeclaredFields()) {
					if (!field.isSynthetic()) {
						heuristicParameters.add(field);
					}
				}
				i = i.getSuperclass();
			}

			for (Field field : heuristicParameters) {

				if (heuristicParametersNames.contains(field.getName())) {
					field.setAccessible(true);

					builder.append(field.getName());
					builder.append("=");

					if (field.getType().equals(Class.forName("[I"))) {
						builder.append(Arrays.toString((int[]) field.get(mlcAlgorithm)).replace(" ", ""));
					} else if (field.getType().equals(int.class)) {
						builder.append(Integer.toString(field.getInt(mlcAlgorithm)));
					} else if (field.getType().equals(double.class)) {
						builder.append(Double.toString(field.getDouble(mlcAlgorithm)));
					}
					builder.append("-");
				}
			}
		} catch (SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		return builder.toString();
	}

}
