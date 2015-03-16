package cz.agents.cycleplanner.originDestination;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.ResourceToFile;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;

/**
 * A generator of origin and destination from file.
 * 
 * Origin and destination are instances of <code>CycleNode</code>.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class OriginDestinationNodeLoader implements OriginDestinationGenerator<CycleNode> {

	private static Logger log = Logger.getLogger(OriginDestinationNodeLoader.class);

	private List<OriginDestinationPair<CycleNode>> originDestinationPairs;
	private Iterator<OriginDestinationPair<CycleNode>> iterator = null;

	public OriginDestinationNodeLoader(City city) {
		originDestinationPairs = new ArrayList<>();

		try {
			File f = ResourceToFile.getFileFromResource(this.getClass().getResourceAsStream(
					"/" + city + "/requests.csv"));
			RoutingService routingService = RoutingService.INSTANCE;
			Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(city);

			CSVReader reader = new CSVReader(new FileReader(f));
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) {

				CycleNode origin = graph.getNodeByNodeId(Long.parseLong(nextLine[1]));
				CycleNode destination = graph.getNodeByNodeId(Long.parseLong(nextLine[2]));
				int directDistance = (int) Math.round(EdgeUtil.computeDirectDistanceInM(origin.getGpsLocation(),
						destination.getGpsLocation()));

				originDestinationPairs.add(new OriginDestinationPair<CycleNode>(origin, destination, directDistance));
			}
			reader.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OriginDestinationPair<CycleNode> getNextOriginDestination() {
		if (iterator == null) {
			iterator = originDestinationPairs.iterator();
		}

		return iterator.next();
	}

}
