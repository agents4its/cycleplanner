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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import cvut.fel.nemetma1.aStar.search.AStarSearch;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.graphWrapper.AdditionalGraphElements;
import cvut.fel.nemetma1.graphWrapper.GraphWrapper;
import cvut.fel.nemetma1.nearestNode.NearestEdgePointServiceWithEdgeIndex;
import cvut.fel.nemetma1.nearestNode.NearestNodeService;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.wp5common.GPSLocation;
import eu.superhub.wp5.wp5common.Location;

/**
 * Path finder class provides finding of path based on provided AStarSearch implementation, graph and nearest point
 * calculating services.
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class PathFinder {

	private AStarSearch aStarSearch;
	private Graph<CycleNode, CycleEdge> cycleGraph;
	private NearestEdgePointServiceWithEdgeIndex nearestEdgePointService;
	private NearestNodeService<CycleNode, CycleEdge> nearestNodeService;

	/**
	 * Creates an instance of PathFinder.
	 * 
	 * @param aStarSearch
	 *            A* search with specific cost and heuristic function that will be used for routing
	 * @param cycleGraph
	 *            graph in which routing is done
	 * @param nearestEdgePointService
	 *            service for finding nearest point on graph
	 * @param nearestNodeService
	 *            service for finding nearest of the graph nodes
	 */
	public PathFinder(AStarSearch aStarSearch, Graph<CycleNode, CycleEdge> cycleGraph,
			NearestEdgePointServiceWithEdgeIndex nearestEdgePointService,
			NearestNodeService<CycleNode, CycleEdge> nearestNodeService) {
		this.aStarSearch = aStarSearch;
		this.cycleGraph = cycleGraph;
		this.nearestEdgePointService = nearestEdgePointService;
		this.nearestNodeService = nearestNodeService;
	}

	private CycleNode findClosestNode(double lat, double lon, AdditionalGraphElements additionalGraphElements) {

		// Location location = RoutingService.INSTANCE.projection.getProjectedGPSLocation(new GPSLocation(lat, lon));
		Location startLocation = new GPSLocation(lat, lon);
		CycleNode startEdgePoint = nearestEdgePointService.getNearestPoint(startLocation, additionalGraphElements);
		if (startEdgePoint == null) {
			startEdgePoint = nearestNodeService.getNearestNode(startLocation);
		}

		return startEdgePoint;
	}

	/**
	 * finds a route with optimal cost from specified origin to destination, using the A* search
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @return collection of CycleNodes on path from origin to destination
	 */
	// TODO not used
	public Collection<CycleNode> findRoute(double startLat, double startLon, double endLat, double endLon) {
		try {
			AdditionalGraphElements additionalGraphElements = new AdditionalGraphElements();
			System.out.println("start LatLon: " + startLat + " , " + startLon + " end LatLon: " + endLat + ", "
					+ endLon);
			long startEdgePointTime = System.currentTimeMillis();
			CycleNode startNode = findClosestNode(startLat, startLon, additionalGraphElements);
			CycleNode endNode = findClosestNode(endLat, endLon, additionalGraphElements);
			long edgepoint = System.currentTimeMillis() - startEdgePointTime;
			System.out.println("startEdgePoint " + startNode);
			System.out.println("endEdgePoint " + endNode);
			System.out.println("edgepoint time" + edgepoint);
			GraphWrapper graphWrapper = new GraphWrapper(cycleGraph, additionalGraphElements);
			// ROUTING
			long startTime = System.currentTimeMillis();
			Collection<CycleNode> path = aStarSearch.findPath(startNode, endNode, graphWrapper);
			long routingTime = System.currentTimeMillis() - startTime;
			System.out.println("routingTime " + routingTime);
			return path;
		} catch (Exception ex) {
			Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * finds a route with optimal cost from specified origin to destination, using the A* search
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @return collection of CycleNodes on path from origin to destination
	 */
	public Collection<CycleEdge> findRouteEdges(double startLat, double startLon, double endLat, double endLon) {
		try {
			AdditionalGraphElements additionalGraphElements = new AdditionalGraphElements();
			System.out.println("start LatLon: " + startLat + " , " + startLon + " end LatLon: " + endLat + ", "
					+ endLon);
			long startEdgePointTime = System.currentTimeMillis();
			CycleNode startNode = findClosestNode(startLat, startLon, additionalGraphElements);
			CycleNode endNode = findClosestNode(endLat, endLon, additionalGraphElements);
			long edgepoint = System.currentTimeMillis() - startEdgePointTime;
			System.out.println("startEdgePoint " + startNode);
			System.out.println("endEdgePoint " + endNode);

			System.out.println("edgepoint time " + edgepoint + " ms");
			GraphWrapper graphWrapper = new GraphWrapper(cycleGraph, additionalGraphElements);

			// ROUTING
			long startTime = System.currentTimeMillis();
			Collection<CycleEdge> path = aStarSearch.findPathEdges(startNode, endNode, graphWrapper);
			long routingTime = System.currentTimeMillis() - startTime;
			System.out.println("routingTime " + routingTime);
			return path;
		} catch (Exception ex) {
			Logger.getLogger(PathFinder.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
}
