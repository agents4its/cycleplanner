package cz.agents.cycleplanner.routingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.javaml.core.kdtree.KDTree;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.wp5common.location.Location;

/**
 * Class provides service for obtaining the closest node in a graph using
 * KD-tree data structure.
 * 
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 * 
 */
public class NearestNodesUtilKdTreeImpl {

	private final static Logger log = Logger.getLogger(NearestNodesUtilKdTreeImpl.class);

	private final static Pattern FOR_BICYCLES_PATTERN = Pattern.compile("relation::route::bicycle|"
			+ "way::bicycle::designated|way::bicycle::permissive|way::bicycle::yes|way::cycleway::lane|"
			+ "way::cycleway::share_busway|way::cycleway::shared_lane|way::cycleway::track|"
			+ "way::cycleway:left::lane|way::cycleway:left::share_busway|way::cycleway:left::shared_lane|"
			+ "way::cycleway:right::lane|way::cycleway:right::share_busway|"
			+ "way::cycleway:right::shared_lane|way::highway::cycleway");

	private final static Pattern ROADS_PATTERN = Pattern.compile("way::highway::cycleway|"
			+ "way::highway::living_street|way::highway::primary|way::highway::primary_link|"
			+ "way::highway::residential|way::highway::secondary|way::highway::secondary_link|"
			+ "way::highway::service|way::highway::tertiary|tertiary_link");

	private enum KDTreeIdentifier {
		PREFFERED_NODES_FEASIBLE_FROM_ORIGIN, PREFFERED_NODES_FEASIBLE_TO_DESTINATION, ALL_NODES
	};

	private final Graph<CycleNode, CycleEdge> cycleGraph;
	private final int NUMBER_OF_NEAREST_NODES = 1;
	private double maxDistanceInMeters;
	private Map<KDTreeIdentifier, KDTree> trees;

	public NearestNodesUtilKdTreeImpl(double maxDistanceInMeters, Graph<CycleNode, CycleEdge> cycleGraph) {
		this.maxDistanceInMeters = maxDistanceInMeters;

		this.trees = new HashMap<>();
		this.cycleGraph = cycleGraph;

		// initialize KD-trees
		initOriginPreferredTree();
		initDestinationPreferredTree();
		initAllTree();
	}

	private void initOriginPreferredTree() {
		KDTree originTree = new KDTree(2);

		for (Node node : cycleGraph.getAllNodes()) {

			// outgoing feasibility => origin
			List<CycleEdge> outcomingEdges = cycleGraph.getNodeOutcomingEdges(node.getId());
			for (CycleEdge cycleEdge : outcomingEdges) {
				if (isPreferred(cycleEdge)) {
					if (node.hasProjectedCoordinates()) {

						originTree.insert(new double[] { node.getProjectedLongitude(), node.getProjectedLatitude() },
								node);
					} else {
						log.warn("Node " + node + " has not projected coordinates.");
					}
				}
			}
		}

		trees.put(KDTreeIdentifier.PREFFERED_NODES_FEASIBLE_FROM_ORIGIN, originTree);
	}

	private void initDestinationPreferredTree() {
		KDTree destinationTree = new KDTree(2);

		for (Node node : cycleGraph.getAllNodes()) {

			// incoming feasibility => destination
			List<CycleEdge> incomingEdges = cycleGraph.getNodeIncomingEdges(node.getId());
			for (CycleEdge cycleEdge : incomingEdges) {
				if (isPreferred(cycleEdge)) {
					if (node.hasProjectedCoordinates()) {

						destinationTree.insert(
								new double[] { node.getProjectedLongitude(), node.getProjectedLatitude() }, node);
					} else {
						log.warn("Node " + node + " has not projected coordinates.");
					}
				}
			}
		}

		trees.put(KDTreeIdentifier.PREFFERED_NODES_FEASIBLE_TO_DESTINATION, destinationTree);
	}

	/**
	 * Control whether edge contains tags defining preferred option.
	 */
	private boolean isPreferred(CycleEdge cycleEdge) {
		if (cycleEdge.getOSMtags() != null) {
			for (String tag : cycleEdge.getOSMtags()) {

				if (FOR_BICYCLES_PATTERN.matcher(tag).matches() || ROADS_PATTERN.matcher(tag).matches()) {
					return true;
				}
			}
		}
		return false;
	}

	private void initAllTree() {
		KDTree tree = new KDTree(2);
		for (Node node : cycleGraph.getAllNodes()) {
			if (node.hasProjectedCoordinates()) {
				tree.insert(new double[] { node.getProjectedLongitude(), node.getProjectedLatitude() }, node);
			} else {
				log.warn("Node " + node + " has not projected coordinates.");
			}
		}

		trees.put(KDTreeIdentifier.ALL_NODES, tree);
	}

	/**
	 * Finds nearest node's id to origin.
	 * 
	 * @param location
	 *            - inserted origin location
	 * 
	 */
	public Long findNearestOriginIds(Location location) {
		return findNearestNodeIds(location, true);
	}

	/**
	 * Finds nearest node's id to destination.
	 * 
	 * @param location
	 *            - inserted destination location
	 * 
	 */
	public Long findNearestDestinationIds(Location location) {
		return findNearestNodeIds(location, false);
	}

	private Long findNearestNodeIds(Location location, boolean isOrigin) {

		Collection<Node> nearestNodes = getNearestNodes(location.getProjectedLatitude(),
				location.getProjectedLongitude(), NUMBER_OF_NEAREST_NODES, true);
		if (nearestNodes.iterator().hasNext()) {
			return nearestNodes.iterator().next().getId();
		} else {
			return Long.MAX_VALUE;
		}
	}

	private Collection<Node> getNearestNodes(double projectedLatitude, double projectedLongitude,
			int numberOfNearestNodes, boolean isOrigin) {

		// KDTree tree = (isOrigin) ?
		// trees.get(KDTreeIdentifier.PREFFERED_NODES_FEASIBLE_FROM_ORIGIN) :
		// trees
		// .get(KDTreeIdentifier.PREFFERED_NODES_FEASIBLE_FROM_ORIGIN);

		double[] key = new double[] { projectedLongitude, projectedLatitude };
		// Object[] searchResult = getNearestNodes(tree, key);
		//
		// Collection<Node> nearestNodes = filterByRadius(projectedLatitude,
		// projectedLongitude, searchResult,
		// maxDistanceInMeters);
		//
		// if (nearestNodes.isEmpty()) {

		KDTree tree = trees.get(KDTreeIdentifier.ALL_NODES);
		Object[] searchResult = getNearestNodes(tree, key);

			// Radius is set to maximum possible value, because at this point we
			// want the closest node without any restriction.
		Collection<Node> nearestNodes = filterByRadius(projectedLatitude, projectedLongitude, searchResult,
				Double.MAX_VALUE);
		// }

		return nearestNodes;
	}

	private Object[] getNearestNodes(KDTree tree, double[] key) {
		if (tree != null) {
			try {
				Object[] searchResult = tree.nearest(key, NUMBER_OF_NEAREST_NODES);
				return searchResult;
			} catch (Exception e) {
				log.warn("Exception in tree.nearest()!");
				return new Object[0];
			}
		} else {
			log.warn("There is no tree!");
			return new Object[0];
		}
	}

	private static Collection<Node> filterByRadius(double projectedLatitude, double projectedLongitude,
			Object[] searchResult, double radiusInMeters) {
		ArrayList<Node> set = new ArrayList<Node>(searchResult.length);
		for (Object o : searchResult) {
			Node t = (Node) o;
			double x = projectedLongitude - t.getProjectedLongitude();
			double y = projectedLatitude - t.getProjectedLatitude();

			// according to
			// http://stackoverflow.com/questions/3764978/why-hypot-function-is-so-slow
			// StrictMath.hypot(x, y); is too slow
			double distance = Math.sqrt(x * x + y * y);
			log.debug("Distance between wanted and found node is " + distance);

			// log.debug(projectedLatitude + "," + projectedLongitude + " -> " +
			// t + " distance " + distance);
			if (distance <= radiusInMeters) {
				set.add(t);
			}
		}
		return set;
	}
}
