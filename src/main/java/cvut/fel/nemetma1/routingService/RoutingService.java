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
package cvut.fel.nemetma1.routingService;

import java.io.File;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jettison.json.JSONException;

import cvut.fel.nemetma1.aStar.search.AStarSearchWithAspects;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.graphCreator.HighwayGraphProvider;
import cvut.fel.nemetma1.indexing.EdgeIndex;
import cvut.fel.nemetma1.json.JsonRouteWithCostInformationCreator;
import cvut.fel.nemetma1.json.JsonRouteWithTimeLength;
import cvut.fel.nemetma1.nearestNode.NearestEdgePointServiceWithEdgeIndex;
import cvut.fel.nemetma1.nearestNode.NearestNodeJavaApprox;
import cvut.fel.nemetma1.nearestNode.NearestNodeService;
import cycle.planner.aStar.Profile;
import cycle.planner.aStar.PseudoMultiCriteriaAStarSearch;
import cycle.planner.json.GeoJSONRouteCreator;
import cycle.planner.util.EPSGProjection;
import eu.superhub.wp5.graphcommon.graph.Graph;

/**
 * RoutingService holds reference to a graph and provides functions for path finding and returning path in GeoJson
 * format.
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public enum RoutingService {

	INSTANCE;
	private HighwayGraphProvider graphProvider;
	private Graph<CycleNode, CycleEdge> cycleGraph;
	private NearestNodeService<CycleNode, CycleEdge> nearestNodeJavaApprox;
	private NearestEdgePointServiceWithEdgeIndex nearestEdgePointService;

	public EPSGProjection projection;

	private RoutingService() {
		try {
			System.out.println("*************RoutingService constructor called, initializing");
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
			nearestNodeJavaApprox = new NearestNodeJavaApprox<>(cycleGraph);
			EdgeIndex<CycleNode, CycleEdge> edgeIndex = new EdgeIndex<>(cycleGraph, 0.004, 0.003);
			nearestEdgePointService = new NearestEdgePointServiceWithEdgeIndex(cycleGraph, 500, edgeIndex);

			projection = new EPSGProjection(2065);

			System.gc();
		} catch (Exception ex) {
			Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("Initializing failed, exception: " + ex);
		}
	}

	/**
	 * Finds a path from a specified origin to a destination, average cruising speed of a user and weights of speed,
	 * comfort, quietness and shortestDistnace aspects.
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @param averageSpeedMetersPerSecond
	 *            average cruising speed of a user
	 * @param speedWeight
	 *            speed aspect weight
	 * @param comfortWeight
	 *            comfort aspect weight
	 * @param quietnessWeight
	 *            quietness aspect weight
	 * @param shortestDistanceWeight
	 *            shortestDistnace aspect weight
	 * @return String in GeoJSON format that contains path
	 */
	public String findRouteJOSMLatLonWithAspects(double startLat, double startLon, double endLat, double endLon,
			double averageSpeedMetersPerSecond, double speedWeight, double comfortWeight, double quietnessWeight,
			double shortestDistanceWeight) {
		long time = System.currentTimeMillis();

		PathFinder pathFinder = new PathFinder(new AStarSearchWithAspects(averageSpeedMetersPerSecond, speedWeight,
				comfortWeight, quietnessWeight, shortestDistanceWeight), cycleGraph, nearestEdgePointService,
				nearestNodeJavaApprox);
		Collection<CycleEdge> path = pathFinder.findRouteEdges(startLat, startLon, endLat, endLon);
		String json = null;
		try {
			json = JsonRouteWithTimeLength.createJsonPath(path, averageSpeedMetersPerSecond, speedWeight,
					comfortWeight, quietnessWeight, shortestDistanceWeight);
		} catch (JSONException ex) {
			Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.out.println("total request time (JSON transfer not included):" + (System.currentTimeMillis() - time));
		return json;
	}

	/**
	 * Finds a path from a specified origin to a destination, average cruising speed of a user and weights of speed,
	 * comfort, quietness and shortestDistnace aspects.
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @param averageSpeedMetersPerSecond
	 *            average cruising speed of a user
	 * @param speedWeight
	 *            speed aspect weight
	 * @param comfortWeight
	 *            comfort aspect weight
	 * @param quietnessWeight
	 *            quietness aspect weight
	 * @param shortestDistanceWeight
	 *            shortestDistnace aspect weight
	 * @return Collection of edges in the path from origin to destination
	 */
	public Collection<CycleEdge> findRouteJOSMLatLonWithAspectsReturnEdges(double startLat, double startLon,
			double endLat, double endLon, double averageSpeedMetersPerSecond, double speedWeight, double comfortWeight,
			double quietnessWeight, double shortestDistanceWeight) {
		// long time = System.currentTimeMillis();

		PathFinder pathFinder = new PathFinder(new AStarSearchWithAspects(averageSpeedMetersPerSecond, speedWeight,
				comfortWeight, quietnessWeight, shortestDistanceWeight), cycleGraph, nearestEdgePointService,
				nearestNodeJavaApprox);
		Collection<CycleEdge> path = pathFinder.findRouteEdges(startLat, startLon, endLat, endLon);

		// System.out.println("total request time (JSON transfer not included):" + (System.currentTimeMillis() - time));
		return path;
	}

	/**
	 * Finds a path from a specified origin to a destination, average cruising speed of a user and weights of speed,
	 * comfort, quietness and shortestDistnace aspects. Returned string in GeoJson format contains detailed information
	 * about each edge.
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @param averageSpeedMetersPerSecond
	 *            average cruising speed of a user
	 * @param speedWeight
	 *            speed aspect weight
	 * @param comfortWeight
	 *            comfort aspect weight
	 * @param quietnessWeight
	 *            quietness aspect weight
	 * @param shortestDistanceWeight
	 *            shortestDistnace aspect weight
	 * @return String in GeoJSON format that contains path
	 */
	public String findRouteJOSMLatLonWithAspectsDetailed(double startLat, double startLon, double endLat,
			double endLon, double averageSpeedMetersPerSecond, double speedWeight, double comfortWeight,
			double quietnessWeight, double shortestDistanceWeight) {
		long time = System.currentTimeMillis();

		PathFinder pathFinder = new PathFinder(new AStarSearchWithAspects(averageSpeedMetersPerSecond, speedWeight,
				comfortWeight, quietnessWeight, shortestDistanceWeight), cycleGraph, nearestEdgePointService,
				nearestNodeJavaApprox);
		Collection<CycleEdge> path = pathFinder.findRouteEdges(startLat, startLon, endLat, endLon);
		String json = null;
		try {
			json = JsonRouteWithCostInformationCreator.createJsonPath(path, averageSpeedMetersPerSecond, speedWeight,
					comfortWeight, quietnessWeight, shortestDistanceWeight);
		} catch (JSONException ex) {
			Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.out.println("total request time (JSON transfer not included):" + (System.currentTimeMillis() - time));
		return json;
	}

	public Collection<CycleEdge> findRoute(double startLat, double startLon, double endLat, double endLon,
			double averageSpeedMetersPerSecond, Profile profile) {
		PathFinder pathFinder = new PathFinder(
				new PseudoMultiCriteriaAStarSearch(profile, averageSpeedMetersPerSecond), cycleGraph,
				nearestEdgePointService, nearestNodeJavaApprox);
		return pathFinder.findRouteEdges(startLat, startLon, endLat, endLon);
	}

	public String findRouteAsJSON(double startLat, double startLon, double endLat, double endLon,
			double averageSpeedMetersPerSecond, Profile profile) {
		PathFinder pathFinder = new PathFinder(
				new PseudoMultiCriteriaAStarSearch(profile, averageSpeedMetersPerSecond), cycleGraph,
				nearestEdgePointService, nearestNodeJavaApprox);
		Collection<CycleEdge> path = pathFinder.findRouteEdges(startLat, startLon, endLat, endLon);
		String json = null;
		try {
			json = GeoJSONRouteCreator.createJsonPath(path, averageSpeedMetersPerSecond);
		} catch (JSONException ex) {
			Logger.getLogger(RoutingService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return json;
	}
}
