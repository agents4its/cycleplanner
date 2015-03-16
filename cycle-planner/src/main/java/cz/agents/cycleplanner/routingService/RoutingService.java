/*
Copyright 2013 Marcel Német

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cz.agents.cycleplanner.routingService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.api.datamodel.Response;
import cz.agents.cycleplanner.api.datamodel.mlcresponse.MLCResponse;
import cz.agents.cycleplanner.cyclewaygraphs.CyclewayGraphLoader;
import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.data.PragueMediumACycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.exceptions.OutOfBoundsException;
import cz.agents.cycleplanner.exceptions.PlannerException;
import cz.agents.cycleplanner.streetnames.StreetNamesLoader;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.plannerdataimporter.util.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;
import eu.superhub.wp5.wp5common.location.Location;

/**
 * RoutingService holds reference to a graph and provides functions for path
 * finding and returning path in Json format.
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @author Pavol Zilecky <pavol.zilecky@gmail.com>
 */
public enum RoutingService {

	INSTANCE;

	private final Logger log = Logger.getLogger(RoutingService.class);

	private final static int NUMBER_OF_THREADS = 5;
	private final static int MAX_DISTANCE_TO_THE_NEAREST_NODE = 30;

	private List<CityCycleData> citiesCycleData;

	private Map<City, Graph<CycleNode, CycleEdge>> cycleGraphs;

	private Map<City, Map<Long, String>> streetNames;

	private Map<City, NearestNodesUtilKdTreeImpl> kdTrees;

	private ExecutorService executorService;

	// TODO during initialization, get last responseId from database
	private static long responseId = 0;

	public EPSGProjection projection;

	private RoutingService() {

		log.info("RoutingService constructor called, initializing...");

		citiesCycleData = new ArrayList<CityCycleData>();
		// citiesCycleData.add(new PragueCycleData());
		// citiesCycleData.add(new BrnoCycleData());
		// citiesCycleData.add(new CeskeBudejoviceCycleData());
		// citiesCycleData.add(new HradecKraloveCycleData());
		// citiesCycleData.add(new PardubiceCycleData());
		// citiesCycleData.add(new PlzenCycleData());

		// Testing cities
		citiesCycleData.add(new PragueMediumACycleData());
		// citiesCycleData.add(new PragueMediumBCycleData());
		// citiesCycleData.add(new PragueMediumCCycleData());
		// citiesCycleData.add(new PragueSmallCycleData());

		executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

		try {
			cycleGraphs = new HashMap<City, Graph<CycleNode, CycleEdge>>();
			streetNames = new HashMap<City, Map<Long, String>>();

			kdTrees = new HashMap<City, NearestNodesUtilKdTreeImpl>();

			GraphProvider graphProvider;
			StreetNameProvider streetNameProvider;

			for (CityCycleData cycleData : citiesCycleData) {
				File g = ResourceToFile.getFileFromResource(CyclewayGraphLoader.getCyclewayGraphStream(cycleData
						.getGraphFileName()));
				File s = ResourceToFile.getFileFromResource(StreetNamesLoader.getStreetNamesStream(cycleData
						.getStreetNamesFileName()));

				graphProvider = new GraphProvider(g);
				streetNameProvider = new StreetNameProvider(s);

				log.info("Initializing " + cycleData.getCity() + " cycleway graph.");
				Graph<CycleNode, CycleEdge> cycleGraph = graphProvider.getGraph();

				cycleGraphs.put(cycleData.getCity(), cycleGraph);
				streetNames.put(cycleData.getCity(), streetNameProvider.getStreetNames());

				kdTrees.put(cycleData.getCity(), new NearestNodesUtilKdTreeImpl(MAX_DISTANCE_TO_THE_NEAREST_NODE,
						cycleGraph));
			}

			projection = new EPSGProjection(2065);

			System.gc();
			log.info("Cycleplanner initialization was successful!");

		} catch (Exception ex) {
			log.error("Initializing failed");
			ex.printStackTrace();
		}
	}

	public long getResponseID() {
		return responseId++;
	}

	public Iterator<CityCycleData> getSupportedCities() {
		return citiesCycleData.iterator();
	}

	public Graph<CycleNode, CycleEdge> getCycleGraph(City city) {
		return cycleGraphs.get(city);
	}

	public String getStreetName(long wayId) {
		for (Map<Long, String> cityStreetNames : this.streetNames.values()) {
			if (cityStreetNames.containsKey(wayId)) {
				return cityStreetNames.get(wayId);
			}
		}
		return null;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void shutdown() {
		executorService.shutdown();
	}

	public Future<Response> planJourney(Request request) {

		Future<Response> future = executorService.submit(new PlannerCallback(new BicyclePlanner(), request));

		return future;
	}

	public Future<Response> planJourneyForElectricBicycles(Request request) {

		Future<Response> future = executorService.submit(new PlannerCallback(new ElecticBicyclesPlanner(), request));

		return future;
	}

	public MLCResponse planJourneyByMLC(Request request, String mlcAlgorithm) {

		MLCBicyclePlanner mlcBicyclePlanner = new MLCBicyclePlanner(mlcAlgorithm);
		MLCResponse mlcResponse = mlcBicyclePlanner.plan(request);

		return mlcResponse;
	}

	public PlanningInstance getPlanningInstance(Request request) throws OutOfBoundsException, PlannerException {

		// Get CityCycleData for given origin and destination
		CityCycleData data = getCityCycleDataForRequest(request);

		CycleNode originInGraph, destinationInGraph;
		// Obtain closest pair of origin and destination in graph to the given
		// pair
		try {
			originInGraph = getOrigin(request.getStartLatitude(), request.getStartLongitude(), data.getCity());
			destinationInGraph = getDestination(request.getEndLatitude(), request.getEndLongitude(), data.getCity());

		} catch (MismatchedDimensionException | TransformException e) {
			log.error(request, e);
			throw new PlannerException();
		}

		// construct PlanningInstance
		PlanningInstance planningInstance = new PlanningInstance(request, responseId++, originInGraph,
				destinationInGraph, getCycleGraph(data.getCity()), data);

		return planningInstance;
	}

	/**
	 * Origin and destination must lie inside same city
	 * 
	 * @param request
	 * @return
	 * @throws OutOfBoundsException
	 */
	public CityCycleData getCityCycleDataForRequest(Request request) throws OutOfBoundsException {

		CityCycleData startData = getCycleDataForLocation(request.getStartLatitude(), request.getStartLongitude());
		CityCycleData endData = getCycleDataForLocation(request.getEndLatitude(), request.getEndLongitude());

		log.debug("Start data city: " + startData.getCity().toString());
		log.debug("End data city: " + endData.getCity().toString());

		// if latitude and longitude for origin and destination are not in
		// bounding box of any city, then return status OUT_OF_BOUND
		if (startData.getCity().equals(City.OTHER) || endData.getCity().equals(City.OTHER)
				|| !startData.getCity().equals(endData.getCity())) {
			throw new OutOfBoundsException();
		}

		return startData;
	}

	public CityCycleData getCycleDataForLocation(double lat, double lon) {

		for (CityCycleData data : citiesCycleData) {
			if (data.locationInside(lat, lon)) {
				return data;
			}
		}
		return CityCycleData.getDataForCity(City.OTHER);
	}

	// Look for origin
	public CycleNode getOrigin(double lat, double lon, City city) throws MismatchedDimensionException,
			TransformException {

		Location originLocation = projection.getProjectedLocation(new GPSLocation(lat, lon));
		long originIds = kdTrees.get(city).findNearestOriginIds(originLocation);
		return cycleGraphs.get(city).getNodeByNodeId(originIds);
	}

	// Look for destination
	public CycleNode getDestination(double lat, double lon, City city) throws MismatchedDimensionException,
			TransformException {

		Location destinationLocation = projection.getProjectedLocation(new GPSLocation(lat, lon));
		long destinationIds = kdTrees.get(city).findNearestOriginIds(destinationLocation);
		return cycleGraphs.get(city).getNodeByNodeId(destinationIds);
	}

}
