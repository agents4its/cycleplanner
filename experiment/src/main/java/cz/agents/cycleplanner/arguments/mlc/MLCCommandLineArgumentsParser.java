package cz.agents.cycleplanner.arguments.mlc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cz.agents.cycleplanner.arguments.CommandLineArgumentsParser;
import cz.agents.cycleplanner.routingService.City;

/**
 * Command line parser for arguments, especially for experiments with multi-label
 * correcting algorithm
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class MLCCommandLineArgumentsParser extends CommandLineArgumentsParser {

	private static Logger log = Logger.getLogger(MLCCommandLineArgumentsParser.class);

	private Map<String, Class<?>> classes;
	private Map<String, MLCAlgorithmParameter> algorithmParameters;

	public MLCCommandLineArgumentsParser(String[] args) throws ParseException, IOException {
		super(args);

		classes = collectAvailableClasses("cz.agents.cycleplanner.MLC.alg");

		algorithmParameters = initializeMLCAlgorithmParameters();
	}

	/**
	 * Recursively search for all classes available from specified package
	 * 
	 * @param pkg
	 *            recursively start search from this package
	 * @return map of all available classes
	 */
	private Map<String, Class<?>> collectAvailableClasses(String pkg) {
		Map<String, Class<?>> availableClasses = new HashMap<>();

		try {
			ClassPath cp = ClassPath.from(this.getClass().getClassLoader());

			Set<ClassInfo> s = cp.getTopLevelClassesRecursive(pkg);

			for (ClassInfo classInfo : s) {
				availableClasses.put(classInfo.getSimpleName(), classInfo.load());
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return availableClasses;
	}

	/**
	 * Initialize relevant instance of MLCAlgorithmParameter for each parameter
	 * specified as command line argument
	 * 
	 * @return map of specified parameters with competent instance
	 */
	private Map<String, MLCAlgorithmParameter> initializeMLCAlgorithmParameters() {
		Map<String, MLCAlgorithmParameter> algorithmParameters = new HashMap<String, MLCAlgorithmParameter>();

		if (cmd.hasOption("cuttingTime")) {
			algorithmParameters.put("cuttingTime",
					new CuttingTimeParameter(Long.parseLong(cmd.getOptionValue("cuttingTime"))));
		}

		if (cmd.hasOption("gamma")) {
			algorithmParameters.put("gamma", new CostPruningParameter(Integer.parseInt(cmd.getOptionValue("gamma"))));
		}

		if (cmd.hasOption("aOverB")) {
			algorithmParameters.put("aOverB",
					new EllipseParameter(Double.parseDouble(cmd.getOptionValue("aOverB"))));
		}

		if (cmd.hasOption("alpha")) {
			algorithmParameters
					.put("alpha", new RatioPruningParameter(Double.parseDouble(cmd.getOptionValue("alpha"))));
		}

		if (cmd.hasOption("buckets")) {

			String[] bucketsAsString = cmd.getOptionValues("buckets");
			int[] buckets = new int[bucketsAsString.length];

			for (int i = 0; i < buckets.length; i++) {
				buckets[i] = Integer.parseInt(bucketsAsString[i]);
			}

			algorithmParameters.put("buckets", new BucketsParameter(buckets));
		}

		if (cmd.hasOption("epsilon")) {
			algorithmParameters.put("epsilon", new EpsilonDominanceParameter(Double.parseDouble(cmd.getOptionValue("epsilon"))));
		}

		return algorithmParameters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Options getOptions() {
		Options options = new Options();

		options.addOption(
				"algorithm",
				true,
				"MLC algorithm name. Choose one of the following: MLC, MLCEllipse, MLCBuckets, MLCCostPruning, MLCEpsilonDominance, MLCRatioPruning, MLCEllipseBuckets, MLCEllipseCostPruning, MLCEllipseEpsilonDominance, MLCEllipseRatioPruning, MLCEllipseRatioPruningBuckets, MLCEllipseRatioPruningCostPruning, MLCEllipseRatioPruningEpsilonDominance, MLCRatioPruningBuckets, MLCRatioPruningCostPruning, MLCRatioPruningEpsilonDominance");
		options.addOption(
				"region",
				true,
				"region where experiment will take place. Choose one of the following: PRAGUE_MEDIUM_A, PRAGUE_MEDIUM_B, PRAGUE_MEDIUM_C, PRAGUE_SMALL, PRAGUE, BRNO, PLZEN, CESKE_BUDEJOVICE, HRADEC_KRALOVE, PARDUBICE");
		options.addOption("location", true, "path to directory with results");
		options.addOption("cuttingTime", true, "maximal running time in milliseconds for one query");
		options.addOption("gamma", true, "parameter for cost pruning speedup technique");
		options.addOption("aOverB", true,
				"parameter for ellipse speedup technique, represents the ratio between ellipse's parameters a and b");
		options.addOption("alpha", true, "parameter for ratio pruning speedup technique");
		options.addOption("epsilon", true, "parameter for epsilon dominance speedup technique");

		@SuppressWarnings("static-access")
		Option buckets = OptionBuilder.withArgName("buckets").hasArgs()
				.withDescription("parameters for buckets speedup technique, specify one bucket value per criterion")
				.create("buckets");
		options.addOption(buckets);

		return options;
	}

	/**
	 * Returns class of MLC algorithm.
	 * 
	 * @return MLC algorithm class if was specified as argument,otherwise null
	 */
	public Class<?> getAlgorithm() {
		if (cmd.hasOption("algorithm")) {

			String className = cmd.getOptionValue("algorithm");

			if (classes.containsKey(className)) {
				Class<?> algClass = classes.get(className);

				return algClass;
			}
		}

		return null;
	}

	/**
	 * Returns city specified as argument.
	 * 
	 * @return city if region was specified as argument, otherwise null
	 */
	public City getCity() {
		if (cmd.hasOption("region")) {
			return City.valueOf(cmd.getOptionValue("region"));
		}

		return null;
	}

	/**
	 * Returns path to directory with experiment results.
	 * 
	 * @return path to directory if location was specified as argument,
	 *         otherwise empty string
	 */
	public String getResultsLocation() {
		if (cmd.hasOption("location")) {
			return cmd.getOptionValue("location");
		}

		return "";
	}

	/**
	 * Returns map with MLCAlgorithmParameters identified by argument's name
	 * 
	 * @return map with MLCAlgorithmParameters
	 */
	public Map<String, MLCAlgorithmParameter> getMLCAlgorithmParameters() {
		return algorithmParameters;
	}

}
