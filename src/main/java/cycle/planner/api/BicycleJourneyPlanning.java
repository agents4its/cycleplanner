package cycle.planner.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import cvut.fel.nemetma1.routingService.RoutingService;
import cycle.planner.aStar.Profile;

@Path("bicycleJourneyPlanning")
public class BicycleJourneyPlanning {
	
	private static RoutingService routingService = RoutingService.INSTANCE;
    
	@Context
    private UriInfo context;

    /**
     * Creates a new instance of BicycleJourneyPlanning
     */
    public BicycleJourneyPlanning() {
    }

    /**
     * Method defines query syntax and returns planned cycle journey in GeoJSON format
     *
     * @return an instance of java.lang.String in GeoJson format
     */
    @GET
    @Path("/planJourney")
    @Produces("application/json")
    public String planJourney(@QueryParam("startLat") String startLat, @QueryParam("startLon") String startLon,
            @QueryParam("endLat") String endLat, @QueryParam("endLon") String endLon, @QueryParam("avgSpeed") String avgSpeed,
            @QueryParam("speedWeight") String travelTimeWeight, @QueryParam("comfortWeight") String comfortWeight, @QueryParam("quietnessWeight") String quietnessWeight,
            @QueryParam("shortestDistanceWeight") String flatnessWeight) {
        
    	return routingService.findRouteAsJSON(Double.parseDouble(startLat), Double.parseDouble(startLon),
                Double.parseDouble(endLat), Double.parseDouble(endLon),
                Double.parseDouble(avgSpeed)/3.6, new Profile(Double.parseDouble(travelTimeWeight), Double.parseDouble(comfortWeight), Double.parseDouble(quietnessWeight), Double.parseDouble(flatnessWeight)));
    	
    }

}
