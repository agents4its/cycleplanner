package cz.agents.cycleplanner.api;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.api.datamodel.Response;
import cz.agents.cycleplanner.api.datamodel.ResponseStatus;
import cz.agents.cycleplanner.api.datamodel.feedback.CycleplannerFeedback;
import cz.agents.cycleplanner.api.datamodel.mlcresponse.MLCResponse;
import cz.agents.cycleplanner.mongodb.BicycleJourneyPlanStorage;
import cz.agents.cycleplanner.mongodb.StorageMongoDbConnectionProvider;
import cz.agents.cycleplanner.routingService.RoutingService;
import cz.agents.cycleplanner.util.JSONUtils;
import cz.agents.cycleplanner.util.ResponseFactory;

/**
 * 
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 * 
 */
@Path("/api/v2")
public class BicycleJourneyPlanning implements ServletContextListener {

	@Context
	private UriInfo context;

	private final static Logger log = Logger.getLogger(BicycleJourneyPlanning.class);
	private static RoutingService routingService;
	private static BicycleJourneyPlanStorage bicycleJourneyPlanStorage;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		log.info("Call contextInitialized(...) method.");
		routingService = RoutingService.INSTANCE;
		
		StorageMongoDbConnectionProvider.setCvutProductionDbConnectionDetails();
		bicycleJourneyPlanStorage = BicycleJourneyPlanStorage.getStorage();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		routingService.shutdown();
	}

	/**
	 * Creates a new instance of BicycleJourneyPlanning
	 */
	public BicycleJourneyPlanning() {
		super();
	}

	/**
	 * Returns bicycle journey plan planned between specified origin and
	 * destination and with respect to defined average cruising speed. Method
	 * expects four more parameters, weight value for each criterion: travel
	 * time, comfort, quietness and flatness.
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @param avgSpeed
	 *            average cruising speed
	 * @param travelTimeWeight
	 *            weight value for Travel time criterion
	 * @param comfortWeight
	 *            weight value for Comfort criterion
	 * @param quietnessWeight
	 *            weight value for Quietness criterion
	 * @param flatnessWeight
	 *            weight value for Flatness criterion
	 * @return bicycle journey plan in JSON format
	 */
	@GET
	@Path("/journey")
	@Produces("application/json")
	public String planJourney(@QueryParam("startLat") String startLat, @QueryParam("startLon") String startLon,
			@QueryParam("endLat") String endLat, @QueryParam("endLon") String endLon,
			@QueryParam("avgSpeed") String avgSpeed, @QueryParam("travelTimeWeight") String travelTimeWeight,
			@QueryParam("comfortWeight") String comfortWeight, @QueryParam("quietnessWeight") String quietnessWeight,
			@QueryParam("flatnessWeight") String flatnessWeight) {

		Request request = new Request(Double.parseDouble(startLat), Double.parseDouble(startLon),
				Double.parseDouble(endLat), Double.parseDouble(endLon), Double.parseDouble(avgSpeed),
				Double.parseDouble(travelTimeWeight), Double.parseDouble(comfortWeight),
				Double.parseDouble(quietnessWeight), Double.parseDouble(flatnessWeight));
		Future<Response> future = routingService.planJourney(request);
		Response response;

		try {
			response = future.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(request, e);
			response = ResponseFactory.getEmptyResponse(ResponseStatus.PLANNER_EXCEPTION);
		}

		return JSONUtils.javaObjectToJson(response);
	}
	
	/**
	 * 
	 * Returns bicycle journey plan for predefined query. This method serves to
	 * test planner's functionality.
	 * 
	 * @return bicycle journey plan for predefined query in JSON format
	 */
	@GET
	@Path("/journey/test")
	@Produces("application/json")
	public String planTestJourney() {
		Request request = new Request(50.0726, 14.3918, 50.0732, 14.4416, 20, 1, 3, 5, 2);
		Future<Response> future = routingService.planJourney(request);
		Response response;

		try {
			response = future.get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(request, e);
			response = ResponseFactory.getEmptyResponse(ResponseStatus.PLANNER_EXCEPTION);
		}
		return JSONUtils.javaObjectToJson(response);
	}
	
	/**
	 * 
	 * Uses multi-label correcting algorithm to search Pareto set of bicycle
	 * journeys between origin and destination.
	 * 
	 * Returns set of edges together with number of journeys from Pareto set
	 * that includes it. Information is represented as suggested width and color
	 * for the edge.
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @param mlcAlgorithm
	 *            implementation of <i>MLC</i> algorithm to use
	 * @return set of edges with suggested width and color for render program
	 */
	// TODO returns only how many times edge occurs in journeys
	// TODO rename and create other method which returns small portion of journeys from Pareto set
	@GET
	@Path("/journey/mlc")
	@Produces("application/json")
	public String planMLCJourney(@QueryParam("startLat") String startLat, @QueryParam("startLon") String startLon,
			@QueryParam("endLat") String endLat, @QueryParam("endLon") String endLon,
			@QueryParam("mlcAlgorithm") String mlcAlgorithm) {

		Request request = new Request(Double.parseDouble(startLat), Double.parseDouble(startLon),
				Double.parseDouble(endLat), Double.parseDouble(endLon), Double.MAX_VALUE, Double.MAX_VALUE,
				Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

		MLCResponse mlcResponse = routingService.planJourneyByMLC(request, mlcAlgorithm);

		if (mlcResponse == null) {
			return "";
		}

		return JSONUtils.javaObjectToJson(mlcResponse);
	}
	
	// TODO /journey/{journeyId} -- return journey by ID from database

	/**
	 * Returns four bicycle journey plans planned between specified origin and
	 * destination and with respect to defined average cruising speed. Plans
	 * corresponds to four profiles: Commuting, Bike friendly, Flat and Fast.
	 * 
	 * @param startLat
	 *            latitude of origin
	 * @param startLon
	 *            longitude of origin
	 * @param endLat
	 *            latitude of destination
	 * @param endLon
	 *            longitude of destination
	 * @param avgSpeed
	 *            average cruising speed
	 * @return four planned bicycle journey plans in JSON format
	 */
	@GET
	@Path("/journeys")
	@Produces("application/json")
	public String planJourneys(@QueryParam("startLat") String startLat, @QueryParam("startLon") String startLon,
			@QueryParam("endLat") String endLat, @QueryParam("endLon") String endLon,
			@QueryParam("avgSpeed") String avgSpeed) {

		Map<Integer, Future<Response>> futures = new HashMap<>();
		Response[] responses = new Response[4];

		double startLatitude = Double.parseDouble(startLat);
		double startLongitude = Double.parseDouble(startLon);
		double endLatitude = Double.parseDouble(endLat);
		double endLongitude = Double.parseDouble(endLon);
		double averageSpeed = Double.parseDouble(avgSpeed);

		Request commutingRequest = new Request(startLatitude, startLongitude, endLatitude, endLongitude, averageSpeed,
				2, 1, 1, 1);
		
		futures.put(0, routingService.planJourney(commutingRequest));

		Request bikeFriendlyRequest = new Request(startLatitude, startLongitude, endLatitude, endLongitude,
				averageSpeed, 1, 3, 5, 2);
	
		futures.put(1, routingService.planJourney(bikeFriendlyRequest));

		// Be aware, combining with other criteria and not using raw flatness
		// criterion can yield to journeys with not smaller slope along route,
		// but are more suitable for cyclists
		Request flatRequest = new Request(startLatitude, startLongitude, endLatitude, endLongitude, averageSpeed, 1, 1,
				1, 5);
		
		futures.put(2, routingService.planJourney(flatRequest));
		// instead of flat profile use profile for electric bicycles ->
		// futures.put(2, routingService.planJourneyForElectricBicycles(flatRequest));

		Request fastRequest = new Request(startLatitude, startLongitude, endLatitude, endLongitude, averageSpeed, 1, 0,
				0, 0);
		
		futures.put(3, routingService.planJourney(fastRequest));

		for (int i = 0; i < responses.length; i++) {
			try {
				responses[i] = futures.get(i).get();
			} catch (InterruptedException | ExecutionException e) {
				log.error(e);
				responses[i] = ResponseFactory.getEmptyResponse(ResponseStatus.PLANNER_EXCEPTION);
			}
		}

		return JSONUtils.javaObjectToJson(responses);
	}

	/**
	 * 
	 * Saves feedback to database.
	 * 
	 * @param feedback
	 *            JSON format of feedback to bicycle journey plan
	 * @return response with status code 201 when saving runs successfully,
	 *         otherwise returns response with status code 400 or 401
	 */
	@POST
	@Path("/feedback")
	@Consumes(MediaType.APPLICATION_JSON)
	public javax.ws.rs.core.Response feedback(String feedback) {

		CycleplannerFeedback cycleplannerFeedback = JSONUtils.jsonToJavaObject(feedback, CycleplannerFeedback.class);
		
		if (cycleplannerFeedback != null) {
			boolean success = bicycleJourneyPlanStorage.storeCyclePlannerFeedback(cycleplannerFeedback);

			log.info("Was feedback sucessfully saved? " + success);

			if (success) {
				URI location = null; // TODO Temporary, we do not provide API to reading feedback from DB 
				return javax.ws.rs.core.Response.created(location).build();
			} else {
				return javax.ws.rs.core.Response.status(404).entity("Problem with saving feedback to database!")
						.type("text/plain").build();
			}

		} else {
			log.warn("Someone tried to store incompatible feedback format!");

			return javax.ws.rs.core.Response.status(400).entity("Incompatible JSON feedback format!")
					.type("text/plain").build();
		}

	}

}
