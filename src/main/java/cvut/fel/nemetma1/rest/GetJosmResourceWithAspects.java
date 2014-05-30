/*
Copyright 2013 Marcel Német

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cvut.fel.nemetma1.rest;

import cvut.fel.nemetma1.routingService.RoutingService;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
@Path("getJosmAspects")
public class GetJosmResourceWithAspects {

    private static RoutingService routingServiceSingleton = RoutingService.INSTANCE;
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GetJosmResource
     */
    public GetJosmResourceWithAspects() {
    }

    /**
     * Retrieves representation of an instance of cvut.fel.nemetma1.rest.GetJosmResource
     *
     * @return an instance of java.lang.String in GeoJson format
     */
    @GET
    @Produces("application/json")
    public String getJson(@QueryParam("startLat") String startLat, @QueryParam("startLon") String startLon,
            @QueryParam("endLat") String endLat, @QueryParam("endLon") String endLon, @QueryParam("avgSpeed") String avgSpeed,
            @QueryParam("speedWeight") String speedWeight, @QueryParam("comfortWeight") String comfortWeight, @QueryParam("quietnessWeight") String quietnessWeight,
            @QueryParam("shortestDistanceWeight") String shortestDistanceWeight) {
        return routingServiceSingleton.findRouteJOSMLatLonWithAspects(
                Double.parseDouble(startLat), Double.parseDouble(startLon),
                Double.parseDouble(endLat), Double.parseDouble(endLon),
                Double.parseDouble(avgSpeed) / 3.6,
                Double.parseDouble(speedWeight), Double.parseDouble(comfortWeight),
                Double.parseDouble(quietnessWeight), Double.parseDouble(shortestDistanceWeight));
    }


}
