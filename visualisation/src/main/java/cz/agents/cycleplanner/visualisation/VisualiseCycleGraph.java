package cz.agents.cycleplanner.visualisation;

import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;

public class VisualiseCycleGraph {

	private static final Logger log = Logger.getLogger(VisualiseCycleGraph.class);

	public static void main(String[] args) {

		// init cycle planner routing service
		RoutingService service = RoutingService.INSTANCE;

		Graph<CycleNode, CycleEdge> graph;
		CycleGraphVisualisation vis = null;
		CityCycleData cycleData;

		try {
			// Iterate over all supported cities
			for (Iterator<CityCycleData> iterator = service.getSupportedCities(); iterator.hasNext();) {

				cycleData = iterator.next();

				log.info("Cycle data city: " + cycleData.getCity());
				graph = service.getCycleGraph(cycleData.getCity());

				log.info(cycleData.getCity() + " cycle graph: " + graph.getAllNodes().size() + " nodes, "
						+ graph.getAllEdges().size() + " edges");

				vis = VisualizationUtil.initVisualisation(cycleData, "_cycleways");
				vis.visualizeGraph(graph.getAllNodes(), graph.getAllEdges(), "");
			}

			if (vis != null) {
				vis.saveAndCreateIndexesAndClose();
			}

		} catch (SQLException | TransformException e) {
			System.out.println(e.getMessage());
		}

	}
}
