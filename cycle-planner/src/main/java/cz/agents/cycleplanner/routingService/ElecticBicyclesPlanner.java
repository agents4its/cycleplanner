package cz.agents.cycleplanner.routingService;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.aStar.AStarSearch;
import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.api.datamodel.Response;
import cz.agents.cycleplanner.api.datamodel.ResponseStatus;
import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.ebike.ElectricBicycleSearchJGraphT;
import cz.agents.cycleplanner.exceptions.OutOfBoundsException;
import cz.agents.cycleplanner.util.ResponseFactory;

public class ElecticBicyclesPlanner implements Planner<Response, Request> {

	private final static Logger log = Logger.getLogger(ElecticBicyclesPlanner.class);

	private RoutingService routingService;

	public ElecticBicyclesPlanner() {

		this.routingService = RoutingService.INSTANCE;
	}

	@Override
	public Response plan(Request request) {


		// Get CityCycleData for given origin and destination
		CityCycleData data;

		try {
			data = routingService.getCityCycleDataForRequest(request);
		} catch (OutOfBoundsException e) {
			return ResponseFactory.getEmptyResponse(ResponseStatus.OUT_OF_BOUNDS);
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
			return ResponseFactory.getEmptyResponse(ResponseStatus.PLANNER_EXCEPTION);
		}

		long time = System.currentTimeMillis();

		AStarSearch search = new ElectricBicycleSearchJGraphT(request.getAverageSpeed());

		Collection<CycleEdge> path = null;
		try {
			path = search
					.findPathEdges(originInGraph, destinationInGraph, routingService.getCycleGraph(data.getCity()));
		} catch (Exception e) {
			log.error(request, e);
			return ResponseFactory.getEmptyResponse(ResponseStatus.PLAN_NOT_FOUND);
		}

		time = System.currentTimeMillis() - time;
		log.info("Path found in " + time + " ms");
		time = System.currentTimeMillis();

		ResponseFactory factory = new ResponseFactory(data.getBoundingBox(), request.getAverageSpeed());
		
		return factory.getResponse(path);
	}

}
