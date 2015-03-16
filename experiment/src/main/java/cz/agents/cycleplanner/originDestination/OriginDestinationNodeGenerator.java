package cz.agents.cycleplanner.originDestination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;

/**
 * A random generator of origin and destination.
 * 
 * Origin and destination are instances of <code>CycleNode</code>.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class OriginDestinationNodeGenerator implements OriginDestinationGenerator<CycleNode> {

	private Random random;
	private CycleNode[] nodes;
	private int maxDirectDistance;
	private int minDirectDistance;

	public OriginDestinationNodeGenerator(long seed, City city, int maxDirectDistance, int minDirectDistance) {
		this.random = new Random(seed);
		this.maxDirectDistance = maxDirectDistance;
		this.minDirectDistance = minDirectDistance;

		RoutingService routingService = RoutingService.INSTANCE;
		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(city);
		this.nodes = graph.getAllNodes().toArray(new CycleNode[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OriginDestinationPair<CycleNode> getNextOriginDestination() {
		return generate(random);
	}

	/**
	 * Method create new instance of random generator and generate list of
	 * originDestination pairs.
	 * 
	 * This method does not have effect on <code>getNextOriginDestion</code>
	 * method.
	 * 
	 * @param seed
	 * @param size
	 * @return
	 */
	public List<OriginDestinationPair<CycleNode>> getListOfOriginDestinationPairs(long seed, int size) {
		Random generator = new Random(seed);
		List<OriginDestinationPair<CycleNode>> originDestinationPairs = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {

			originDestinationPairs.add(generate(generator));
		}

		return originDestinationPairs;
	}

	/**
	 * Using random generator specified as argument randomly choose origin and
	 * destination from set of nodes which meets direct distance restrictions
	 * 
	 * @param generator
	 * @return OriginDestinationPair<CycleNode> - origin and destination as
	 *         instance of CycleNode
	 */
	private OriginDestinationPair<CycleNode> generate(Random generator) {
		// Randomly pick origin from set of nodes
		CycleNode origin = nodes[generator.nextInt(nodes.length)];
		CycleNode destination;
		int directDistance;

		do {
			// Randomly pick destination from set of nodes
			destination = nodes[generator.nextInt(nodes.length)];

			// Compute direct between origin and current destination
			directDistance = (int) Math.round(EdgeUtil.computeDirectDistanceInM(origin.getGpsLocation(),
					destination.getGpsLocation()));

			// Check whether origin equals destination or whether direct
			// distance is less then maximum allowed or whether direct distance
			// is more then allowed minimum
		} while (origin.equals(destination) || directDistance > maxDirectDistance || directDistance < minDirectDistance);

		OriginDestinationPair<CycleNode> originDestinationPair = new OriginDestinationPair<CycleNode>(origin,
				destination, directDistance);

		return originDestinationPair;
	}

}
