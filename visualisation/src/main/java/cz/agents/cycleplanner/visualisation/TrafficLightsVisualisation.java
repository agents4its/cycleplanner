package cz.agents.cycleplanner.visualisation;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.data.PragueCycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;

public class TrafficLightsVisualisation {

	private final static Logger log = Logger
			.getLogger(TrafficLightsVisualisation.class);

	private static Graph<CycleNode, CycleEdge> cycleGraph;
	
	private static final Pattern TRAFFIC_SIGNALS = Pattern
			.compile("node::crossing::traffic_signals|node::highway::traffic_signals");

	public static void main(String[] args) {
		log.info("************* Initializing *************");

		RoutingService service = RoutingService.INSTANCE;
		cycleGraph = service.getCycleGraph(City.PRAGUE);

		Set<CycleEdge> edges = new HashSet<>();
		Set<CycleNode> nodes = new HashSet<>();
		
		for (CycleEdge edge: cycleGraph.getAllEdges()) {
			if (edge.getOSMtags() != null) {
				for (String tag : edge.getOSMtags()) {
					
					if (TRAFFIC_SIGNALS.matcher(tag).matches()) {
						edges.add(edge);
						nodes.add(edge.getFromNode());
						nodes.add(edge.getToNode());
						break;
					}
					
				}
			}
		}
		
		visualise(nodes, edges);
	}

	private static void visualise(Collection<CycleNode> nodes, Collection<CycleEdge> edges) {
		PragueCycleData pragueFullCycleData = new PragueCycleData();

		CycleGraphVisualisation vis = VisualizationUtil.initVisualisation(
				pragueFullCycleData, "");

		try {
			vis.visualizeGraph(nodes, edges, "");
			vis.saveAndCreateIndexesAndClose();
		} catch (SQLException | TransformException e) {
			log.info(e.getMessage());
		}
	}
}
