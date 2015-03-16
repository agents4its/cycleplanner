package cz.agents.cycleplanner.creators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.connectivity.StronglyConnectedComponentsGenerator;
import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.evaluate.Evaluator;
import cz.agents.cycleplanner.plannerDataImporterExtensions.RelationAndWayOsmImporter;
import cz.agents.cycleplanner.plannerDataImporterExtensions.osmBinder.RelationAndWayBicycleGraphOsmBinder;
import cz.agents.cycleplanner.plannerDataImporterExtensions.tasks.impl.CycleWithHighwaysImportTaskImpl;
import cz.agents.cycleplanner.routingService.City;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.plannerdataimporter.graphimporter.OsmDataGetter;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.graph.PermittedModeEvaluator;

public class GraphCreator implements Creator {

	private static final Logger log = Logger.getLogger(GraphCreator.class);

	@Override
	public void create(City city) {
		CityCycleData data = CityCycleData.getDataForCity(city);
		Graph<CycleNode, CycleEdge> graph = createCyclewayGraph(data.getOsmFile());
		saveGraphObjectToFile(graph, city.toString().toLowerCase().replace("_", "-"));
	}

	// TODO
	public void createGraphWithouJunctionExtension(City city) {
		CityCycleData data = CityCycleData.getDataForCity(city);
		Graph<CycleNode, CycleEdge> graph = createCyclewayGraph(data.getOsmFile());
		saveGraphObjectToFile(graph, city.toString().toLowerCase().replace("_", "-"));
	}

	public void createPragueAMediumGraph() {
		Graph<CycleNode, CycleEdge> graph = createCyclewayGraph(new File("osm-data/prague-medium-A.osm"));
		saveGraphObjectToFile(graph, "prague-medium-A");
	}

	public void createPragueBMediumGraph() {
		Graph<CycleNode, CycleEdge> graph = createCyclewayGraph(new File("osm-data/prague-medium-B.osm"));
		saveGraphObjectToFile(graph, "prague-medium-B");
	}

	public void createPragueCMediumGraph() {
		Graph<CycleNode, CycleEdge> graph = createCyclewayGraph(new File("osm-data/prague-medium-C.osm"));
		saveGraphObjectToFile(graph, "prague-medium-C");
	}

	public void createPragueSmallGraph() {
		Graph<CycleNode, CycleEdge> graph = createCyclewayGraph(new File("osm-data/prague-small.osm"));
		saveGraphObjectToFile(graph, "prague-small");
	}

	/**
	 * TODO javadoc
	 * 
	 * @param osm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Graph<CycleNode, CycleEdge> createCyclewayGraph(File osm) {

		log.info("Creating graph...");

		List<PermittedModeEvaluator> graphEvaluators = Arrays.asList(RelationAndWayBicycleGraphOsmBinder
				.getPermittedModeEvaluator());

		log.debug("Exist osm file? " + osm.exists());
		log.debug("Osm file path" + osm.getAbsolutePath());
		log.debug("Can read osm file? " + osm.canRead());
		log.debug("Can write osm file? " + osm.canWrite());
		log.debug("Osm file size " + (osm.length() / 1024) + " KB");

		OsmDataGetter osmDataGetter = OsmDataGetter.createOsmDataGetter(osm);
		log.info("Initializing importer...");
		RelationAndWayOsmImporter importer = new RelationAndWayOsmImporter(osmDataGetter);

		Graph<CycleNode, CycleEdge> graph;
		log.info("Importing...");
		graph = importer.executeTaskForWayAndRelation(new CycleWithHighwaysImportTaskImpl(50, graphEvaluators),
				RelationAndWayBicycleGraphOsmBinder.getSelector());

		log.info("Sinked in " + graph.getAllNodes().size() + " nodes and " + graph.getAllEdges().size() + " edges");

		// Find largest strongly connected component
		graph = new StronglyConnectedComponentsGenerator<CycleNode, CycleEdge>()
				.getLargestStronglyConnectedComponent(graph);

		log.info("Final strongly connected component has " + graph.getAllNodes().size() + " nodes and "
				+ graph.getAllEdges().size() + " edges");

		// Detect anomaly
		// GraphAnomalyDetector detector = new GraphAnomalyDetector(graph);
		// graph = detector.detect();
		//
		// log.info("After anomaly detection, graph has " +
		// graph.getAllNodes().size() + " nodes and "
		// + graph.getAllEdges().size() + " edges");

		// Simplified graph
		// EdgeSimplifier simplifier = new EdgeSimplifier();
		// cycleGraph = simplifier.simplifyGraph(cycleGraph);
		// System.out.println("Graf ma pocet hran: " +
		// cycleGraph.getAllEdges().size() + " a pocet vrcholov: "
		// + cycleGraph.getAllNodes().size());

		// Extend graph to junctions
		// JunctionExtension junctionExtension = new JunctionExtension(graph);
		// graph = junctionExtension.getExtendedGraph();
		//
		// log.info("Graph with junctions has " + graph.getAllNodes().size() +
		// " nodes and " + graph.getAllEdges().size()
		// + " edges");

		log.info("Evaluating edges...");
		Evaluator.evaluateGraph(graph);

		return graph;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param graph
	 * @param city
	 */
	private void saveGraphObjectToFile(Graph<CycleNode, CycleEdge> graph, String name) {
		log.info("Saving graph...");

		try {
			File f = new File(name + "-cycleway-graph.javaobject");
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(graph);
			oos.close();
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		log.info("Graph saved");
	}

	public static void main(String[] args) {
		GraphCreator creator = new GraphCreator();

		creator.create(City.PRAGUE);
		creator.create(City.BRNO);
		creator.create(City.CESKE_BUDEJOVICE);
		creator.create(City.HRADEC_KRALOVE);
		creator.create(City.PARDUBICE);
		creator.create(City.PLZEN);

		creator.createPragueAMediumGraph();
		creator.createPragueBMediumGraph();
		creator.createPragueCMediumGraph();
		creator.createPragueSmallGraph();
	}

}
