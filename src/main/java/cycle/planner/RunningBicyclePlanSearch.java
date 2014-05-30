package cycle.planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.openstreetmap.osm.data.coordinates.LatLon;

import aima.core.agent.Action;
import aima.core.search.framework.DefaultGoalTest;
import aima.core.search.framework.GraphSearch;
import aima.core.search.framework.HeuristicFunction;
import aima.core.search.framework.Problem;
import aima.core.search.framework.Search;
import aima.core.search.framework.SearchAgent;
import aima.core.search.framework.StepCostFunction;
import cvut.fel.nemetma1.aStar.search.CycleAction;
import cvut.fel.nemetma1.aStar.search.GraphFunctionFactory;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.graphCreator.HighwayGraphProvider;
import cvut.fel.nemetma1.graphWrapper.AdditionalGraphElements;
import cvut.fel.nemetma1.graphWrapper.GraphWrapper;
import cvut.fel.nemetma1.indexing.EdgeIndex;
import cvut.fel.nemetma1.nearestNode.NearestEdgePointServiceWithEdgeIndex;
import cvut.fel.nemetma1.nearestNode.NearestNodeJavaApprox;
import cvut.fel.nemetma1.nearestNode.NearestNodeService;
import cvut.fel.nemetma1.routingService.Config;
import cvut.fel.nemetma1.routingService.RoutingService;
import cycle.planner.aStar.Cost;
import cycle.planner.aStar.Heuristic;
import cycle.planner.aStar.Profile;
import cycle.planner.evaluate.evaluator.EvaluationDetails;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.wp5common.GPSLocation;
import eu.superhub.wp5.wp5common.Location;

public class RunningBicyclePlanSearch {

	final static int ITERATION = 100;

	final static double LEFT = 14.323425;
	final static double RIGHT = 14.567871;
	final static double TOP = 50.146546;
	final static double BOTTOM = 50.020094;
	final static double AVERAGE_SPEED_METERS_PER_SECOND = 3.8; // 14km/h = 3.8
																// m/s

	private static final Pattern FOR_BICYCLES_PATTERN = Pattern
			.compile("relation::route::bicycle|way::bicycle::designated|way::bicycle::permissive|"
					+ "way::bicycle::yes|way::cycleway::lane|way::cycleway::share_busway|way::cycleway::shared_lane|way::cycleway::track|way::cycleway:left::lane|"
					+ "way::cycleway:left::share_busway|way::cycleway:left::shared_lane|way::cycleway:right::lane|way::cycleway:right::share_busway|"
					+ "way::cycleway:right::shared_lane|way::highway::cycleway");

	private static final Pattern FOR_BICYCLES_PATTERN_ADDITIONAL = Pattern
			.compile("way::highway::living_street|way::highway::residential|way::highway::service");

	private HighwayGraphProvider graphProvider;
	private Graph<CycleNode, CycleEdge> cycleGraph;
	private NearestNodeService<CycleNode, CycleEdge> nearestNodeJavaApprox;
	private NearestEdgePointServiceWithEdgeIndex nearestEdgePointService;
	private StepCostFunction costFunction;
	private HeuristicFunction heuristic;

	private List<Output> outputs;
	private Output average;
	private Map<String, List<Output>> results;
	private Map<String, Output> resultsAverages;

	public static void main(String[] args) throws Exception {
		RunningBicyclePlanSearch search = new RunningBicyclePlanSearch();
		search.setUp();
		search.testProfiles();
		search.testCriteria();
	}

	public void setUp() throws Exception {
		try {
			System.out.println("************* Initializing *************");
			Config config = new Config();

			File osm = new File(config.getOSMFilePath());
			File g = new File(config.getGraphObjectFilePath());

			graphProvider = new HighwayGraphProvider(osm, g);
			if (config.isdestroyTags()) {
				graphProvider.setDestroyTags(true);
			}
			if (config.isRecreateGraph()) {
				graphProvider.recreateGraphFromOSM();
			}

			cycleGraph = graphProvider.getGraph();
			int countNAN = 0;
			int countZeroLength = 0;
			double countTags = 0;
			HashMap<String, Integer> tagsStatistic = new HashMap<String, Integer>();
			for (CycleEdge edge : cycleGraph.getAllEdges()) {
				countNAN += (Double.isNaN(edge.getEvaluationDetails()
						.getTravelTimePrecomputation())) ? 1 : 0;
				countZeroLength += (edge.getLengthInMetres() == 0) ? 1 : 0;
				countTags += edge.getOSMtags().size();
				for (Iterator<String> it = edge.getOSMtags().iterator(); it
						.hasNext();) {
					String tag = it.next();
					if (tagsStatistic.containsKey(tag)) {
						tagsStatistic.put(tag, tagsStatistic.get(tag) + 1);
					} else {
						tagsStatistic.put(tag, 1);
					}
				}
			}
			System.out.println("Tags statistic");
			for (Iterator<String> it = tagsStatistic.keySet().iterator(); it
					.hasNext();) {
				String key = it.next();
				System.out.println(key + " " + tagsStatistic.get(key));
			}
			System.out.println("Pocet hran s predpocitanymi hodnotami ako NAN "
					+ countNAN);
			System.out
					.println("Pocet hran s nulovou dlzkou " + countZeroLength);
			System.out.println("Priemerny pocet tagov na hranu "
					+ (countTags / (double) cycleGraph.getAllEdges().size()));

			nearestNodeJavaApprox = new NearestNodeJavaApprox<CycleNode, CycleEdge>(
					cycleGraph);
			EdgeIndex<CycleNode, CycleEdge> edgeIndex = new EdgeIndex<CycleNode, CycleEdge>(
					cycleGraph, 0.004, 0.003);
			nearestEdgePointService = new NearestEdgePointServiceWithEdgeIndex(
					cycleGraph, 500, edgeIndex);

			System.gc();
		} catch (Exception ex) {
			Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE,
					null, ex);
			System.out.println("Initializing failed, exception: " + ex);
		}
	}

	public void testProfiles() throws Exception {

		results = new HashMap<String, List<Output>>();
		resultsAverages = new HashMap<String, Output>();

		long time = System.currentTimeMillis();

		// Commuting (2; 1; 1; 1)
		// Bike friendly (1; 3; 5; 2)
		// Flat (1; 1; 1; 5)
		// Travel time (1; 0; 0; 0)

		System.out.println("Starting commuting profile");
		runTest(new Profile(2, 1, 1, 1));
		results.put("commuting", outputs);
		resultsAverages.put("commuting", average);
		System.out.println(average);

		System.out.println("Starting bike friendly profile");
		runTest(new Profile(1, 3, 5, 2));
		results.put("bike_friendly", outputs);
		resultsAverages.put("bike_friendly", average);
		System.out.println(average);

		System.out.println("Starting flat profile");
		runTest(new Profile(1, 1, 1, 5));
		results.put("flat", outputs);
		resultsAverages.put("flat", average);
		System.out.println(average);

		System.out.println("Starting travel time profile");
		runTest(new Profile(1, 0, 0, 0));
		results.put("travel_time", outputs);
		resultsAverages.put("travel_time", average);
		System.out.println(average);

		System.out.println("Test time: "
				+ ((System.currentTimeMillis() - time) / 1000) + "s");

		writeResultsIntoOneFile("search_results_profiles.csv");
	}

	public void testCriteria() throws Exception {

		results = new HashMap<String, List<Output>>();
		resultsAverages = new HashMap<String, Output>();

		long time = System.currentTimeMillis();

		System.out.println("Starting travel time");
		runTest(new Profile(1, 0, 0, 0));
		results.put("travel_time", outputs);
		resultsAverages.put("travel_time", average);
		System.out.println(average);

		System.out.println("Starting comfort");
		runTest(new Profile(0, 1, 0, 0));
		results.put("comfort", outputs);
		resultsAverages.put("comfort", average);
		System.out.println(average);

		System.out.println("Starting quietness");
		runTest(new Profile(0, 0, 1, 0));
		results.put("quietness", outputs);
		resultsAverages.put("quietness", average);
		System.out.println(average);

		System.out.println("Starting flatness");
		runTest(new Profile(0, 0, 0, 1));
		results.put("flatness", outputs);
		resultsAverages.put("flatness", average);
		System.out.println(average);

		System.out.println("Test time: "
				+ ((System.currentTimeMillis() - time) / 1000) + "s");

		writeResultsIntoOneFile("search_results_criteria.csv");
	}

	private void runTest(Profile profile) throws Exception {
		Random random = new Random(103L);

		double averageBicycleRouteLength = 0;
		double averageLength = 0;
		double averageDirectDistance = 0;
		double averageTravelTime = 0;
		double averageComfort = 0;
		double averageQuietness = 0;
		double averageRises = 0;
		double averageDrops = 0;
		double averageComputationTime = 0;
		double averageNodesExpanded = 0;

		outputs = new ArrayList<Output>();

		int i = 0;
		while (i <= ITERATION) {

			double startLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
			double startLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
			double endLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
			double endLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();

			// Checking whether direct distance is less then 10 km.
			double directDistance = LatLon.distanceInMeters(startLat, startLon,
					endLat, endLon);
			if (directDistance > 10000) {
				continue;
			} else {
				i++;
			}

			AdditionalGraphElements additionalGraphElements = new AdditionalGraphElements();
			CycleNode startNode = findClosestNode(startLat, startLon,
					additionalGraphElements);
			CycleNode endNode = findClosestNode(endLat, endLon,
					additionalGraphElements);

			GraphWrapper graph = new GraphWrapper(cycleGraph,
					additionalGraphElements);

			costFunction = new Cost(profile, AVERAGE_SPEED_METERS_PER_SECOND);
			heuristic = new Heuristic(endNode, profile,
					AVERAGE_SPEED_METERS_PER_SECOND);

			Problem problem = new Problem(startNode,
					GraphFunctionFactory.getActionsFunction(graph),
					GraphFunctionFactory.getResultFunction(),
					new DefaultGoalTest(endNode), costFunction);
			Search search = new aima.core.search.informed.AStarSearch(
					new GraphSearch(), heuristic);

			long computationTime = System.currentTimeMillis();
			SearchAgent agent = new SearchAgent(problem, search);
			computationTime = System.currentTimeMillis() - computationTime;

			List<CycleEdge> path = buildPathCycleEdge(startNode,
					agent.getActions());
			double bicycleRouteLength = 0;
			double length = 0;
			double travelTime = 0;
			double comfort = 0;
			double quietness = 0;
			double rises = 0;
			double drops = 0;

			for (Iterator<CycleEdge> it = path.iterator(); it.hasNext();) {
				CycleEdge cycleEdge = it.next();

				rises += cycleEdge.getRises();
				drops += cycleEdge.getDrops();
				length += cycleEdge.getLengthInMetres();
				EvaluationDetails evaluationDetails = cycleEdge
						.getEvaluationDetails();

				double edgeTravelTime = evaluationDetails
						.getTravelTimePrecomputation()
						/ AVERAGE_SPEED_METERS_PER_SECOND
						+ evaluationDetails.getTravelTimeSlowdownConstant();

				comfort += edgeTravelTime
						* evaluationDetails.getComfortMultiplier();
				quietness += edgeTravelTime
						* evaluationDetails.getQuietnessMultiplier();
				travelTime += edgeTravelTime;

				if (cycleEdge.getOSMtags() != null) {
					for (String tag : cycleEdge.getOSMtags()) {
						if (FOR_BICYCLES_PATTERN.matcher(tag).matches()
								|| FOR_BICYCLES_PATTERN_ADDITIONAL.matcher(tag)
										.matches()) {
							bicycleRouteLength += cycleEdge.getLengthInMetres();
							break;
						}
					}

				}
			}

			if (i != 0) {
				averageTravelTime += travelTime;
				averageLength += length;
				averageDirectDistance += directDistance;
				averageBicycleRouteLength += bicycleRouteLength;
				averageComfort += comfort / travelTime;
				averageQuietness += quietness / travelTime;
				averageRises += rises;
				averageDrops += drops;
				averageComputationTime += computationTime;
				averageNodesExpanded += Integer.parseInt(agent
						.getInstrumentation().getProperty("nodesExpanded"));

			}

			// System.out.println("***** Search properties *****");
			// System.out.println("Travel time: "+travelTime);
			// System.out.println("Rises: "+rises);
			// System.out.println("Drops: "+drops);
			// System.out.println("Length: "+ length);
			// System.out.println("Bicycle path length: "+bicycleRouteLength);
			// System.out.println("Comfortness: "+(comfortness/travelTime));
			// System.out.println("Quietness: "+(quietness/travelTime));
			// System.out.println("Searching time: "+computationTime+"ms");
			// System.out.println("Nodes expanded: "+
			// agent.getInstrumentation().getProperty("nodesExpanded"));
			// System.out.println("*****************************");

			outputs.add(new Output(path, startLat, startLon, endLat, endLon,
					travelTime, directDistance, length, bicycleRouteLength,
					comfort / travelTime, quietness / travelTime, rises, drops,
					computationTime, Integer.parseInt(agent
							.getInstrumentation().getProperty("nodesExpanded"))));
		}

		average = new Output(null, 0, 0, 0, 0, averageTravelTime /= ITERATION,
				averageDirectDistance /= ITERATION, averageLength /= ITERATION,
				averageBicycleRouteLength /= ITERATION,
				averageComfort /= ITERATION, averageQuietness /= ITERATION,
				averageRises /= ITERATION, averageDrops /= ITERATION,
				averageComputationTime /= ITERATION,
				averageNodesExpanded /= ITERATION);

	}

	private CycleNode findClosestNode(double lat, double lon,
			AdditionalGraphElements additionalGraphElements) {

		Location startLocation = new GPSLocation(lat, lon);
		CycleNode startEdgePoint = nearestEdgePointService.getNearestPoint(
				startLocation, additionalGraphElements);
		if (startEdgePoint == null) {
			startEdgePoint = nearestNodeJavaApprox
					.getNearestNode(startLocation);
		}

		return startEdgePoint;
	}

	private List<CycleEdge> buildPathCycleEdge(CycleNode startNode,
			List<Action> actions) {
		ArrayList<CycleEdge> path = new ArrayList<CycleEdge>();
		for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
			Action a = it.next();
			if (a.isNoOp()) {
				path.add(new CycleEdge(startNode, startNode, 0, 0, 0));
				return path;
			}
			CycleAction acycle = (CycleAction) a;
			path.add(acycle.getEdgeToTake());
		}
		return path;
	}

	private void writeResultsIntoOneFile(String fileName)
			throws FileNotFoundException {
		PrintStream c;
		File csv = new File(fileName);

		c = new PrintStream(csv);
		String[] keys = (String[]) results.keySet().toArray(new String[0]);

		c.println("Profile,Iteration,From latitude,From longitude,To latitude,To longitude,Travel time,Travel time minus mean,Direct distance[m],Length [m],Length minus mean,Length on bicycle ways [m],Length on bicycle ways minus mean,Length on bicycle ways [%],Comfort,Comfort minus mean,Quietness,Quietness minus mean,Route rises[m],Route rises minus mean,Route drops[m],Route drops minus mean,Search time[ms],Search time minus mean,Number of expanded nodes,Number of expanded nodes minus mean,");

		for (int i = 0; i < keys.length; i++) {
			Output avg = resultsAverages.get(keys[i]);
			for (int j = 1; j <= ITERATION; j++) {
				c.print(keys[i] + ",");
				c.print(j + ",");

				Output out = results.get(keys[i]).get(j);
				c.print(out.startLat + ",");
				c.print(out.startLon + ",");
				c.print(out.endLat + ",");
				c.print(out.endLon + ",");
				c.print(out.travelTime + ",");
				c.print((out.travelTime - avg.travelTime) + ",");
				c.print(out.directDistance + ",");
				c.print(out.length + ",");
				c.print((out.length - avg.length) + ",");
				c.print(out.bicycleRouteLength + ",");
				c.print((out.bicycleRouteLength - avg.bicycleRouteLength) + ",");
				c.print((out.bicycleRouteLength / out.length * 100) + ",");
				c.print(out.comfort + ",");
				c.print((out.comfort - avg.comfort) + ",");
				c.print(out.quietness + ",");
				c.print((out.quietness - avg.quietness) + ",");
				c.print(out.rises + ",");
				c.print((out.rises - avg.rises) + ",");
				c.print(out.drops + ",");
				c.print((out.drops - avg.drops) + ",");
				c.print(out.computationTime + ",");
				c.print((out.computationTime - avg.computationTime) + ",");
				c.print(out.nodesExpanded + ",");
				c.println((out.nodesExpanded - avg.nodesExpanded) + ",");
			}
			c.flush();
		}

		c.close();
	}

	private class Output {

		public final List<CycleEdge> path;
		public final double startLat, startLon, endLat, endLon;
		public final double travelTime, directDistance, length,
				bicycleRouteLength, comfort, quietness, rises, drops,
				computationTime, nodesExpanded;

		public Output(List<CycleEdge> path, double startLat, double startLon,
				double endLat, double endLon, double travelTime,
				double directDistance, double length,
				double bicycleRouteLength, double comfort, double quietness,
				double rises, double drops, double computationTime,
				double nodesExpanded) {

			this.path = path;
			this.startLat = startLat;
			this.startLon = startLon;
			this.endLat = endLat;
			this.endLon = endLon;
			this.travelTime = travelTime;
			this.directDistance = directDistance;
			this.length = length;
			this.bicycleRouteLength = bicycleRouteLength;
			this.comfort = comfort;
			this.quietness = quietness;
			this.rises = rises;
			this.drops = drops;
			this.computationTime = computationTime;
			this.nodesExpanded = nodesExpanded;
		}

		@Override
		public String toString() {
			return "Output [path=" + path + ", startLat=" + startLat
					+ ", startLon=" + startLon + ", endLat=" + endLat
					+ ", endLon=" + endLon + ", travelTime=" + travelTime
					+ ", directDistance=" + directDistance + ", length="
					+ length + ", bicycleRouteLength=" + bicycleRouteLength
					+ ", comfort=" + comfort + ", quietness=" + quietness
					+ ", rises=" + rises + ", drops=" + drops
					+ ", computationTime=" + computationTime
					+ ", nodesExpanded=" + nodesExpanded + "]";
		}
	}

	// private void writeResultsIntoFile(String fileName)
	// throws FileNotFoundException {
	// PrintStream c;
	// File csv = new File(fileName);
	//
	// double averageBicycleRouteLength = 0;
	// double averageLength = 0;
	// double averageTravelTime = 0;
	// double averageComfortness = 0;
	// double averageQuietness = 0;
	// double averageRises = 0;
	// double averageDrops = 0;
	// double averageComputationTime = 0;
	// double averageNodesExpanded = 0;
	//
	// c = new PrintStream(csv);
	// c.println("Iteration,Travel time,Length [m],Length on bicycle ways [m],Comfortness,Quietness,Route rises[m],Route drops[m],Search time[ms],Number of expanded nodes");
	// for (int i = 1; i < outputs.size(); i++) {
	// Output out = outputs.get(i);
	// c.print(i + ",");
	//
	// c.print(out.travelTime + ",");
	// averageTravelTime += out.travelTime;
	//
	// c.print(out.length + ",");
	// averageLength += out.length;
	//
	// c.print(out.bicycleRouteLength + ",");
	// averageBicycleRouteLength += out.bicycleRouteLength;
	//
	// c.print(out.comfort + ",");
	// averageComfortness += out.comfort;
	//
	// c.print(out.quietness + ",");
	// averageQuietness += out.quietness;
	//
	// c.print(out.rises + ",");
	// averageRises += out.rises;
	//
	// c.print(out.drops + ",");
	// averageDrops += out.drops;
	//
	// c.print(out.computationTime + ",");
	// averageComputationTime += out.computationTime;
	//
	// c.println(out.nodesExpanded + ",");
	// averageNodesExpanded += out.nodesExpanded;
	//
	// c.flush();
	// }
	// c.println("");
	// double size = outputs.size() - 1;
	// c.print(",,,,," + (averageTravelTime / size) + ",");
	// c.print((averageLength / size) + ",");
	// c.print((averageBicycleRouteLength / size) + ",");
	// c.print((averageComfortness / size) + ",");
	// c.print((averageQuietness / size) + ",");
	// c.print((averageRises / size) + ",");
	// c.print((averageDrops / size) + ",");
	// c.print((averageComputationTime / size) + ",");
	// c.println((averageNodesExpanded / size) + ",");
	//
	// c.close();
	// }

}
