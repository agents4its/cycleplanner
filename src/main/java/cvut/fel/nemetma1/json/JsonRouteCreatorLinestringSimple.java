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

import cvut.fel.nemetma1.dataStructures.CycleNode;
import java.util.Collection;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * creates string in JSON format that represent path.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class JsonRouteCreatorLinestringSimple {

    public JsonRouteCreatorLinestringSimple() {
    }

    /**
     * creates a string in GeoJSON format that represent a path from provided nodes. A path is wrapped in a GeoJSON LineString.
     *
     * @param nodes nodes that specify the path
     * @return string in JSON format that represent a path
     * @throws JSONException
     */
    public String createJsonPath(Collection< CycleNode> nodes) throws JSONException {
        JSONObject feature = new JSONObject();
        feature.put("type", "Feature");
        JSONObject geometry = new JSONObject();
        geometry.put("type", "LineString");
        JSONArray coordinates = new JSONArray();
        for (CycleNode node : nodes) {
            JSONArray coordinate = new JSONArray();
            coordinate.put(node.getLongitude());
            coordinate.put(node.getLatitude());
            coordinates.put(coordinate);
        }
        geometry.put("coordinates", coordinates);
        //feature.put("geometry", geometry);
        //        System.out.print(geometry);
        return geometry.toString();
    }
}
