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
import cvut.fel.nemetma1.evaluate.aspects.Aspect;
import cvut.fel.nemetma1.evaluate.aspects.ShortestDistanceAspect;
import cvut.fel.nemetma1.evaluate.aspects.ComfortAspect;
import cvut.fel.nemetma1.evaluate.evaluator.EdgeEvaluator;
import cvut.fel.nemetma1.evaluate.aspects.QuietnessAspect;
import cvut.fel.nemetma1.evaluate.aspects.SpeedAspect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Provides GeoJson FeatureCollection, each member of a collection is an edge of a graph with details about that edge and its cost.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class JsonRouteWithCostInformationCreator {

    private static final List<Aspect> aspects;
    private static final SpeedAspect SPEED_ASPECT = new SpeedAspect();

    static {
        aspects = new ArrayList<>();
        aspects.add(new SpeedAspect());
        aspects.add(new ComfortAspect());
        aspects.add(new QuietnessAspect());
        aspects.add(new ShortestDistanceAspect());
    }

    /**
     * creates GeoJson FeatureCollection, each member of a collection is an edge of a graph with details about that edge and its cost.
     *
     * @param edges edges of a path
     * @param averageSpeedMetersPerSecond average speed of a cyclist
     * @param speedWeight weight of a speed aspect
     * @param comfortWeight weight of a comfort aspect
     * @param quietnessWeight weight of a quietness aspect
     * @param shortestDistanceWeight weight of a shortest distance aspect
     * @return string formatted as GeoJson FeatureCollection
     * @throws JSONException
     */
    public static String createJsonPath(Collection<CycleEdge> edges, double averageSpeedMetersPerSecond, double speedWeight, double comfortWeight, double quietnessWeight, double shortestDistanceWeight) throws JSONException {
        List<Double> weights;
        weights = new ArrayList<>();
        weights.add(speedWeight);
        weights.add(comfortWeight);
        weights.add(quietnessWeight);
        weights.add(shortestDistanceWeight);
        double sum = 0;
        for (double d : weights) {
            sum = sum + d;
        }

        JSONArray featuresArray = new JSONArray();

        double length = 0;
        double time = 0;
        double rises = 0;
        double drops = 0;
        JSONArray elevationProfile = new JSONArray();

        elevationProfile.put(0);

        for (Iterator<CycleEdge> it = edges.iterator(); it.hasNext();) {
            CycleEdge edge = it.next();

            length += edge.getLengthInMetres();
            time += SPEED_ASPECT.evaluate(edge, averageSpeedMetersPerSecond);
            rises += edge.getRises();
            drops += edge.getDrops();
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            JSONObject properties = new JSONObject();
            JSONArray edgeTimeRatioarray = new JSONArray();
            JSONArray multiplierarray = new JSONArray();
            JSONArray constantarray = new JSONArray();
            JSONArray evaluationsarray = new JSONArray();
            JSONArray colorarray = new JSONArray();
            JSONArray gradearray = new JSONArray();


            for (Iterator<Aspect> itA = aspects.iterator(); itA.hasNext();) {
                Aspect aspect = itA.next();
                EdgeEvaluator ee = aspect.getEvaluator(edge);
                ee.evaluateEdge(averageSpeedMetersPerSecond);
                double multiplier = ee.getEdgeSpeedMultiplier();
                double constant = ee.getEdgePenalisationInSeconds();
                double fastestPosssibleTime = edge.getLengthInMetres() / (aspect.getMaximumMultiplier() * averageSpeedMetersPerSecond);
                double normalTime = edge.getLengthInMetres() / (averageSpeedMetersPerSecond);

                edgeTimeRatioarray.put(Double.toString(normalTime / aspect.evaluate(edge, averageSpeedMetersPerSecond)));
                multiplierarray.put(Double.toString(multiplier));
                constantarray.put(Double.toString(constant));
                evaluationsarray.put(ee.getLog());
                double grade = (edge.getRises() + edge.getDrops()) / edge.getLengthInMetres();
                gradearray.put(Double.toString(grade));
                colorarray.put(numberToColor(1, fastestPosssibleTime / normalTime, fastestPosssibleTime / aspect.evaluate(edge, averageSpeedMetersPerSecond)));
            }
            properties.put("slowdown", edgeTimeRatioarray);
            properties.put("multiplier", multiplierarray);
            properties.put("constant", constantarray);
            properties.put("evaluations", evaluationsarray);
            properties.put("color", colorarray);
            properties.put("grade", gradearray);
            elevationProfile.put(edge.getFromNode().getElevation());
            elevationProfile.put(edge.getLengthInMetres());

            if (!it.hasNext()) {
                properties.put("total_length", length);
                properties.put("total_time", time);
                properties.put("total_elevationGain", rises);
                properties.put("total_elevationDrop", drops);
                elevationProfile.put(edge.getToNode().getElevation());
                System.out.println("total_length " + length);
                System.out.println("total_time " + time);
                System.out.println("total_elevationGain " + rises);
                System.out.println("total_elevationDrop " + drops);
                properties.put("elevationProfile", elevationProfile);
            }
            JSONObject geometry = new JSONObject();
            geometry.put("type", "LineString");
            JSONArray coordinates = new JSONArray();
            JSONArray coordinate = new JSONArray();
            coordinate.put(edge.getFromNode().getLongitude());
            coordinate.put(edge.getFromNode().getLatitude());

            coordinates.put(coordinate);
            coordinate = new JSONArray();
            coordinate.put(edge.getToNode().getLongitude());
            coordinate.put(edge.getToNode().getLatitude());
            coordinates.put(coordinate);
            geometry.put("coordinates", coordinates);
            feature.put("properties", properties);
            feature.put("geometry", geometry);
            featuresArray.put(feature);
        }
        JSONObject featureCollection = new JSONObject();
        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", featuresArray);
        return featureCollection.toString();
    }

    public static String numberToColor(double max, double middle, double value) {

        double red = 0.0;
        double green = 0.0;
        if (-0.001 <= value && value < middle) {     //first, green stays at 100%, red raises to 100%
            green = value / middle;
            red = 1.0;
        }
        if (middle <= value && value <= max + 0.001) {       //then red stays at 100%, green decays
            green = 1.0;
            if (max - middle > 0) {
                red = 1.0 - (value - middle) / (max - middle);
            } else {
                red = 1.0;
            }
        }
        int b = 0;
        int r = (int) (red * 255);
        int g = (int) (green * 255);

        return String.format("#%02x%02x%02x", r, g, b);
    }
}
