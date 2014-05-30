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
package cvut.fel.nemetma1.nearestNode;

import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import java.util.ArrayList;
import java.util.Collections;

/**
 * provides geographic and geometry functions
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class GeoCalculationsHelper {

    /**
     *
     * @param latitude latitude in degrees
     * @return length of a longitude degree at given latitude
     */
    public static double lengthOfLongitudeDegree(double latitude) {
        return (Math.PI) / 180 * 6371000 * Math.cos(Math.toRadians(latitude));
    }

    /**
     *
     * @param latitude latitude in degrees
     * @return length of a latitude degree at given latitude
     *
     */
    public static double lengthOfLatitudeDegree(double latitude) {
        return 111132.954 - 559.822 * Math.cos(Math.toRadians(2 * latitude)) + 1.175 * Math.cos(Math.toRadians(4 * latitude));
    }

    /**
     * Euclidean distance of 2D points a,b. Possible intermediate overflow or underflow.
     *
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return Euclidean distance of points a,b
     */
    public static double distanceE2(double ax, double ay, double bx, double by) {
        return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
    }

    /**
     * Euclidean distance of 2D points a,b. Without intermediate overflow or underflow.
     *
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return Euclidean distance of points a,b
     */
    public static double distanceE2OwerflowSafe(double ax, double ay, double bx, double by) {
        return Math.hypot((ax - bx), (ay - by));
    }

    /**
     * computes squared euclidean distance
     *
     * @param ax
     * @param ay
     * @param bx
     * @param by
     * @return Euclidean distance squared of points a,b
     *
     */
    public static double distanceE2Squared(double ax, double ay, double bx, double by) {
        return (ax - bx) * (ax - bx) + (ay - by) * (ay - by);
    }
    
    // TODO consider
    public static double distanceE2SquaredNew(double ax, double ay, double bx, double by) {
    	double x = (ax - bx);
    	double y = (ay - by);
        return (x * x) + (y * y);
    }

    /**
     * computes haversine distance(distance of points at the sphere)
     *
     * @param aLat latitude of point a
     * @param aLon longitude of point a
     * @param bLat latitude of point b
     * @param bLon longitude of point b
     * @return haversine distance
     */
    public static double distanceHaversine(double aLat, double aLon, double bLat, double bLon) {
        final int AVERAGE_RADIUS_OF_EARTH = 6371000;

        double latDistance = Math.toRadians(aLat - bLat);
        double lngDistance = Math.toRadians(aLon - bLon);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2))
                + (Math.cos(Math.toRadians(aLat)))
                * (Math.cos(Math.toRadians(bLat)))
                * (Math.sin(lngDistance / 2))
                * (Math.sin(lngDistance / 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return AVERAGE_RADIUS_OF_EARTH * c;
    }

    /**
     * for each node in the graph, finds the closest node to it and prints statistics
     * @param <TNode>
     * @param <TEdge>
     * @param graph 
     */
    public static <TNode extends Node, TEdge extends Edge> void closestNodeToEachNodeStats(Graph<TNode, TEdge> graph) {
        TNode[] nodes = (TNode[]) graph.getAllNodes().toArray();
        ArrayList<DistanceNodes> a = new ArrayList<>();
        int count = 0;
        for (Node n1 : nodes) {
            double min = -1;
            Node nmin = null;
            double latMultip = GeoCalculationsHelper.lengthOfLatitudeDegree(n1.getLatitude());
            double longMultip = GeoCalculationsHelper.lengthOfLongitudeDegree(n1.getLatitude());
            double referenceLat = n1.getLatitude();
            double referenceLon = n1.getLongitude();
            for (Node n2 : nodes) {
                if (n1.getId() != n2.getId()) {
                    double dist = GeoCalculationsHelper.distanceE2Squared(
                            referenceLat * latMultip, referenceLon * longMultip,
                            n2.getLatitude() * latMultip, n2.getLongitude() * longMultip);
                    if (dist < min || min == -1) {
                        min = dist;
                        nmin = n2;
                    }
                    count++;
                }
            }
            a.add(new DistanceNodes(min, n1, nmin));

        }
        Collections.sort(a);
        int asize = a.size();
        for (int i = 0; i <= 100; i++) {
            System.out.println(i + ": " + a.get((int) asize / 100 * i));
        }
        for (int i = (asize - 1) - 100; i <= (asize - 1); i++) {
            System.out.println(i + ": " + a.get(i));
        }
        System.out.println(a.get(asize - 1));
        System.out.println("count " + count);
    }

  /**
   * A pair of nodes that is comparable. Comparison is done by the distance of nodes in the pair.
   */
    private static class DistanceNodes implements Comparable<DistanceNodes> {

        double distance;
        Node n1;
        Node n2;

        public DistanceNodes(double distance, Node n1, Node n2) {
            this.distance = distance;
            this.n1 = n1;
            this.n2 = n2;
        }

        @Override
        public String toString() {
            return "distance: " + distance + " n1: " + n1 + "n2: " + n2;
        }

        @Override
        public int compareTo(DistanceNodes o) {
            if (this.distance < o.distance) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
