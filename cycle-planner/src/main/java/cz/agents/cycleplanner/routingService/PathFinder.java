package cz.agents.cycleplanner.routingService;

import java.util.Collection;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.aStar.AStarSearch;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.graphWrapper.AdditionalGraphElements;
import cz.agents.cycleplanner.nearestNode.NearestEdgePointServiceWithEdgeIndex;
import cz.agents.cycleplanner.nearestNode.NearestNodeService;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.wp5common.location.GPSLocation;
import eu.superhub.wp5.wp5common.location.Location;

public class PathFinder {
	private final static Logger log = Logger.getLogger(PathFinder.class);

	private AStarSearch aStarSearch;
	private Graph<CycleNode, CycleEdge> cycleGraph;
	private NearestEdgePointServiceWithEdgeIndex nearestEdgePointService;
	private NearestNodeService<CycleNode, CycleEdge> nearestNodeService;

	/**
	 * Creates an instance of PathFinder.
	 * 
	 * @param aStarSearch
	 *            A* search with specific cost and heuristic function that will
	 *            be used for routing
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

		Location startLocation = new GPSLocation(lat, lon);
		// CycleNode startEdgePoint =
		// nearestEdgePointService.getNearestPoint(startLocation,
		// additionalGraphElements);
		// if (startEdgePoint == null) {
		// startEdgePoint = nearestNodeService.getNearestNode(startLocation);
		// }
		//
		// return startEdgePoint;
		return nearestNodeService.getNearestNode(startLocation);
	}

	private CycleNode findClosestNode(double lat, double lon) {

		Location startLocation = new GPSLocation(lat, lon);
		return nearestNodeService.getNearestNode(startLocation);
	}

	/**
	 * finds a route with optimal cost from specified origin to destination,
	 * using the A* search
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

			log.info("start LatLon: " + startLat + " , " + startLon + " end LatLon: " + endLat + ", "
					+ endLon);
			long time = System.currentTimeMillis();
			CycleNode startNode = findClosestNode(startLat, startLon);
			CycleNode endNode = findClosestNode(endLat, endLon);
			time = System.currentTimeMillis() - time;

			// log.debug("Start point " + startNode);
			// log.debug("End point " + endNode);
			log.info("Found start and end point in " + time + " ms");

			// ROUTING
			time = System.currentTimeMillis();

			Collection<CycleEdge> path = aStarSearch.findPathEdges(startNode, endNode, cycleGraph);

			time = System.currentTimeMillis() - time;
			log.info("Routing time " + time);

			return path;
		} catch (Exception ex) {

			log.error(ex.getMessage());
		}
		return null;
	}

}
