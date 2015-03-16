package cz.agents.cycleplanner;

import java.util.HashMap;
import java.util.Iterator;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.routingService.RoutingService;
import eu.superhub.wp5.graphcommon.graph.Graph;

public class GraphStatistics {
	private static RoutingService service;
	private static Graph<CycleNode, CycleEdge> cycleGraph;

	public static void main(String[] args) {

		service = RoutingService.INSTANCE;
		cycleGraph = service.getCycleGraph(City.PRAGUE);

		double countTags = 0;
		HashMap<String, Integer> tagsStatistic = new HashMap<String, Integer>();

		for (CycleEdge edge : cycleGraph.getAllEdges()) {

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
			System.out.println(key + ", " + tagsStatistic.get(key));
		}

		System.out.println("Priemerny pocet tagov na hranu "
				+ (countTags / (double) cycleGraph.getAllEdges().size()));

		int countNodesWithZeroOutDegree = 0;
		for (CycleNode node : cycleGraph.getAllNodes()) {
			if (cycleGraph.getNodeOutcomingEdges(node.getId()).size() == 0)
				countNodesWithZeroOutDegree++;
		}
		System.out.println("Pocet vrchlov ktore nemaju vystupnu hranu: "+countNodesWithZeroOutDegree);
	}

}
