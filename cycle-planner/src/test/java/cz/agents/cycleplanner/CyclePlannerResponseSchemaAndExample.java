package cz.agents.cycleplanner;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;

import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.api.datamodel.Response;
import cz.agents.cycleplanner.routingService.RoutingService;
import cz.agents.cycleplanner.util.JSONSchemaUtils;
import cz.agents.cycleplanner.util.JSONUtils;

public class CyclePlannerResponseSchemaAndExample {

	/**
	 * TODO javadoc
	 * 
	 * Finds a path from a specified origin to a destination, average cruising
	 * speed of a user and weights of speed, comfort, quietness and
	 * shortestDistnace aspects. Returned string in GeoJson format contains
	 * detailed information about each edge.
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws JsonProcessingException, FileNotFoundException, InterruptedException, ExecutionException {

		RoutingService routingService = RoutingService.INSTANCE;

		Request request = new Request(50.0726, 14.3918, 50.0732, 14.4416, 20, 1d, 3d, 5d, 2d);
		Response response = routingService.planJourney(request).get();

		System.out.println(JSONUtils.javaObjectToJson(response));

		JSONSchemaUtils.generateSchemaAndSaveToFile(Response.class, "CyclePlannerResponseSchema.json");
		routingService.shutdown();
	}

}
