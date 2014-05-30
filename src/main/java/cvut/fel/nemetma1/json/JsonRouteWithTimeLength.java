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
package cvut.fel.nemetma1.json;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.evaluate.aspects.SpeedAspect;
import java.util.Collection;
import java.util.Iterator;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * creates a string in GeoJson format that contains the path and in properties, it contains an array of altitudes of nodes in the path
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class JsonRouteWithTimeLength {

    private static final SpeedAspect SPEED_ASPECT = new SpeedAspect();

    /**
     * creates a string in GeoJson format that contains the path and in properties, it contains an array of altitudes of nodes in the path, the time
     * of the journey , and the elevation gain
     *
     * @param edges edges of the path
     * @param averageSpeedMetersPerSecond average speed of a cyclist
     * @param speedWeight weight of a speed aspect
     * @param comfortWeight weight of a comfort aspect
     * @param quietnessWeight weight of a quietness aspect
     * @param shortestDistanceWeight weight of a shortest distance aspect
     * @return string in GeoJson format
     * @throws JSONException
     */
    public static String createJsonPath(Collection<CycleEdge> edges, double averageSpeedMetersPerSecond, double speedWeight, double comfortWeight, double quietnessWeight, double shortestDistanceWeight) throws JSONException {
        double length = 0;
        double time = 0;
        double rises = 0;
        double drops = 0;
        JSONObject feature = new JSONObject();
        feature.put("type", "Feature");
        JSONObject geometry = new JSONObject();
        geometry.put("type", "LineString");
        JSONArray coordinates = new JSONArray();
        JSONArray elevationProfile = new JSONArray();
        elevationProfile.put(0);
        for (Iterator<CycleEdge> it = edges.iterator(); it.hasNext();) {
            CycleEdge edge = it.next();
            JSONArray coordinate = new JSONArray();
            coordinate.put(edge.getFromNode().getLongitude());
            coordinate.put(edge.getFromNode().getLatitude());
            elevationProfile.put(edge.getFromNode().getElevation());
            elevationProfile.put(edge.getLengthInMetres());
            coordinates.put(coordinate);
            if (!it.hasNext()) {
                coordinate = new JSONArray();
                coordinate.put(edge.getToNode().getLongitude());
                coordinate.put(edge.getToNode().getLatitude());
                elevationProfile.put(edge.getFromNode().getElevation());
                coordinates.put(coordinate);
            }
            length += edge.getLengthInMetres();
            time += SPEED_ASPECT.evaluate(edge, averageSpeedMetersPerSecond);
            rises += edge.getRises();
            drops += edge.getDrops();
        }
        geometry.put("coordinates", coordinates);
        feature.put("geometry", geometry);
        JSONObject properties = new JSONObject();

        properties.put("total_length", length);
        properties.put("total_time", time);
        properties.put("total_elevationGain", rises);
        properties.put("total_elevationDrop", drops);
        properties.put("elevationProfile", elevationProfile);
        System.out.println("total_length " + length);
        System.out.println("total_time " + time);
        System.out.println("total_elevationGain " + rises);
        System.out.println("total_elevationDrop " + drops);
        feature.put("properties", properties);

        return feature.toString();
    }
}
