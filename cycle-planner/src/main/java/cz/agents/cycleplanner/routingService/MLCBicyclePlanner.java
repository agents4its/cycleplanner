package cz.agents.cycleplanner.routingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.MLC.MLCCostFunction;
import cz.agents.cycleplanner.MLC.MLCCycleCostFunction;
import cz.agents.cycleplanner.MLC.alg.AbstractMultiLabelCorrectingAlgorithm;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseBuckets;
import cz.agents.cycleplanner.MLC.alg.mixEllipseOthers.MLCEllipseEpsilonDominance;
import cz.agents.cycleplanner.MLC.alg.mixEllipseRatioPruningOthers.MLCEllipseRatioPruningEpsilonDominance;
import cz.agents.cycleplanner.api.datamodel.Coordinate;
import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.api.datamodel.mlcresponse.EdgeUsage;
import cz.agents.cycleplanner.api.datamodel.mlcresponse.MLCResponse;
import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.exceptions.OutOfBoundsException;
import eu.superhub.wp5.graphcommon.graph.EdgeId;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

public class MLCBicyclePlanner implements Planner<MLCResponse, Request> {

	private static final double AVERAGE_SPEED_KILOMETERS_PER_HOUR = 15d;

	private static final Logger log = Logger.getLogger(MLCBicyclePlanner.class);

	private RoutingService routingService;

	private String mlcAlgorithm;

	public MLCBicyclePlanner(String mlcAlgorithm) {

		this.mlcAlgorithm = mlcAlgorithm;
		routingService = RoutingService.INSTANCE;
	}

	@Override
	public MLCResponse plan(Request request) {

		log.info("Request: " + request);

		// Get CityCycleData for given origin and destination
		CityCycleData data;

		try {
			data = routingService.getCityCycleDataForRequest(request);
		} catch (OutOfBoundsException e) {
			log.error(request, e);
			return null;
		}

		// Obtain closest pair of origin and destination in graph to the given
		// pair
		CycleNode originInGraph, destinationInGraph;

		try {
			originInGraph = routingService.getOrigin(request.getStartLatitude(), request.getStartLongitude(),
					data.getCity());
			destinationInGraph = routingService.getDestination(request.getEndLatitude(), request.getEndLongitude(),
					data.getCity());

		} catch (MismatchedDimensionException | TransformException e) {
			log.error(request, e);
			return null;
		}

		long time = System.currentTimeMillis();

		Graph<CycleNode, CycleEdge> graph = routingService.getCycleGraph(data.getCity());
		AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlc = getMLCImplementation(graph, mlcAlgorithm,
				originInGraph, destinationInGraph);

		if (mlc == null) {
			log.error("Problem with initializing mlc algorithm!");
			return null;
		}

		mlc.call();
		MLCResponse mlcResponse = buildMLCResponse(originInGraph, destinationInGraph, mlc.getPathsEdges());

		time = System.currentTimeMillis() - time;
		log.info("Path found in " + time + " ms");
		time = System.currentTimeMillis();

		return mlcResponse;
	}

	private AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> getMLCImplementation(
			Graph<CycleNode, CycleEdge> graph, String mlcAlgorithm, CycleNode origin, CycleNode destination) {

		MLCCostFunction<CycleNode, CycleEdge> costFunction = new MLCCycleCostFunction(AVERAGE_SPEED_KILOMETERS_PER_HOUR);
		AbstractMultiLabelCorrectingAlgorithm<CycleNode, CycleEdge> mlc;

		switch (mlcAlgorithm) {
		case "MLCEllipseEpsilonDominance":
			mlc = new MLCEllipseEpsilonDominance<CycleNode, CycleEdge>(graph, origin, destination, costFunction);
			break;
		case "MLCEllipseRatioPruningEpsilonDominance":
			mlc = new MLCEllipseRatioPruningEpsilonDominance<CycleNode, CycleEdge>(graph, origin, destination,
					costFunction);
			break;
		case "MLCEllipseBuckets":
			mlc = new MLCEllipseBuckets<CycleNode, CycleEdge>(graph, origin, destination, costFunction);
			break;

		default:
			mlc = null;
			break;
		}

		return mlc;
	}

	private MLCResponse buildMLCResponse(CycleNode origin, CycleNode destination,
			Collection<Collection<CycleEdge>> paretoSet) {

		Map<EdgeId, CycleEdge> edges = new HashMap<EdgeId, CycleEdge>();
		Map<EdgeId, Double> widths = new HashMap<EdgeId, Double>();
		Map<EdgeId, String> colours = new HashMap<EdgeId, String>();

		for (Collection<CycleEdge> journey : paretoSet) {

			for (CycleEdge edge : journey) {

				EdgeId edgeId = edge.getEdgeId();

				edges.put(edgeId, edge);

				if (widths.containsKey(edgeId)) {
					widths.put(edgeId, widths.get(edgeId) + 1);
				} else {
					widths.put(edgeId, 1d);
				}
			}
		}

		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;

		for (EdgeId id : widths.keySet()) {
			double value = widths.get(id);
			max = Math.max(value, max);
			min = Math.min(value, min);
		}

		for (EdgeId id : widths.keySet()) {
			// spocitam tak hrubku aby pre najmenej prekryvanu hranu bola
			// hrubka nula a pre najviac zase 12 (najvacsia zvolena hrubka
			// ciary)
			double width = widths.get(id);
			widths.put(id, ((width - min) / (max - min)) * 12);

			// spocitam odtien cervenej na stupncic od 150-255, teda
			// najmenej prekryvana bude mat najtmavsiu farbu a najviac
			// prekryvana najbledsiu

			int colour = (int) (150 + ((width - min) / (max - min)) * 105);
			colours.put(id, "#" + Integer.toHexString(colour) + "0000");
			// log.debug("Colour: #" + Integer.toHexString(colour) +
			// "0000");
		}

		List<EdgeUsage> edgesUsage = new ArrayList<EdgeUsage>();

		for (EdgeId id : widths.keySet()) {
			Node from = edges.get(id).getFromNode();
			Node to = edges.get(id).getToNode();

			EdgeUsage edgeUsage = new EdgeUsage(new Coordinate(from.getLatitudeE6(), from.getLongitudeE6()),
					new Coordinate(to.getLatitudeE6(), to.getLongitudeE6()), widths.get(id), colours.get(id));

			edgesUsage.add(edgeUsage);
		}

		MLCResponse mlcResponse = new MLCResponse(new Coordinate(origin.getLatitudeE6(), origin.getLongitudeE6()),
				new Coordinate(destination.getLatitudeE6(), destination.getLongitudeE6()), edgesUsage);

		return mlcResponse;
	}

}
