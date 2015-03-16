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
package cz.agents.cycleplanner.indexing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cz.agents.cycleplanner.util.GeoCalculationsHelper;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

/**
 * Spatial index of edges, that divides a graph into windows by longitude and latitude.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @param <TNode>
 * @param <TEdge>
 */
public class EdgeIndex<TNode extends Node, TEdge extends Edge> {

    private Graph<TNode, TEdge> graph;
    private HashMap<Long, HashSet<TEdge>> index;
    private double windowWidth;
    private double windowHeight;

    /**
     * Creates a spatial index for edges that divides a graph into windows by longitude and latitude. window width must be greater than 0.00000008
     * degrees , window height must be greater than 0.00000004 degrees.
     *
     * @param graph graph to index
     * @param windowWidth width of a window in degrees of longitude
     * @param windowHeight height of the window in degrees of latitude
     * @throws Exception
     */
    public EdgeIndex(Graph<TNode, TEdge> graph, double windowWidth, double windowHeight) throws Exception {
        this.graph = graph;
        if (windowWidth < 0.00000008 || windowHeight < 0.00000004) {
            throw new Exception("window side too small");
        }
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.index = new HashMap<>();

        System.out.println("indexing edges");
        for (Iterator<TEdge> it = graph.getAllEdges().iterator(); it.hasNext();) {
            TEdge edge = it.next();
            Node n1 = graph.getNodeByNodeId(edge.getFromNodeId());
            Node n2 = graph.getNodeByNodeId(edge.getToNodeId());
            long n1LonHash = (long) (n1.getLongitude() / windowWidth);
            long n2LonHash = (long) (n2.getLongitude() / windowWidth);
            long n1LatHash = (long) (n1.getLatitude() / windowHeight);
            long n2LatHash = (long) (n2.getLatitude() / windowHeight);
            for (long lonHash = Math.min(n1LonHash, n2LonHash); lonHash <= Math.max(n1LonHash, n2LonHash); lonHash++) {
                for (long latHash = Math.min(n1LatHash, n2LatHash); latHash <= Math.max(n1LatHash, n2LatHash); latHash++) {
                    Long hash = ((latHash << 32) + lonHash);
                    if (!index.containsKey(hash)) {
                        HashSet h = new HashSet();
                        h.add(edge);
                        index.put(hash, h);
                    } else {
                        HashSet h = index.get(hash);
                        h.add(edge);
                    }

                }
            }
        }
    }

    /**
     * returns a diameter (in meters) of the biggest circle that can be put in one window of index degree of longitude and latitude has different
     * length in meters in different latitudes, so the latitude must be provided as parameter
     *
     * @param latitude latitude at which the perimeter is calculated
     * @return a diameter (in meters) of the biggest circle that can be put in one window of index
     */
    public double getPerimeterInMForLatitude(double latitude) {
        return Math.min(
                GeoCalculationsHelper.lengthOfLatitudeDegree(latitude) * windowHeight / 2,
                GeoCalculationsHelper.lengthOfLongitudeDegree(latitude) * windowWidth / 2);
    }

    /**
     * returns edges in a window which has the provided hash
     *
     * @param hash hash value of index window
     * @return edges in a window which has the provided hash
     */
    public Set<TEdge> getEdgesInWindow(long hash) {
        return index.get(Long.valueOf(hash));
    }
/**
 * returns edges in a the 4 closest windows from the specified point
 * @param longitude longitude of a specified point 
 * @param latitude latitude of a specified point
 * @return edges in a the 4 closest windows from the specified point
 */
    public Set<TEdge> getEdgesIn4Windows(double longitude, double latitude) {
        long lonHash = (long) (longitude / windowWidth);
        double d = (longitude / windowWidth) - lonHash;
        long lonmove;
        if (d > 0.5) {
            lonmove = 1;
        } else {
            lonmove = -1;
        }
        long latHash = (long) (latitude / windowHeight);
        long latmove;
        if (d > 0.5) {
            latmove = 1;
        } else {
            latmove = -1;
        }
        long hash = ((latHash << 32) + lonHash);
        long hash2 = (((latHash + latmove) << 32) + lonHash);
        long hash3 = ((latHash << 32) + lonHash + lonmove);
        long hash4 = (((latHash + latmove) << 32) + lonHash + lonmove);
        HashSet<TEdge> hh = new HashSet<>();
        Set<TEdge> set = getEdgesInWindow(hash);
        if (set != null) {
            hh.addAll(set);
        }
        set = getEdgesInWindow(hash2);
        if (set != null) {
            hh.addAll(set);
        }
        set = getEdgesInWindow(hash3);
        if (set != null) {
            hh.addAll(set);
        }
        set = getEdgesInWindow(hash4);
        if (set != null) {
            hh.addAll(set);
        }
        return hh;
    }
/**
 * returns edges in a window in which the specified point lies
 * @param longitude longitude of a point
 * @param latitude latitude of a point
 * @return  edges in a window in which the specified point lies
 */
    public Set<TEdge> getEdgesInWindow(double longitude, double latitude) {
        return index.get(Long.valueOf(getHash(longitude, latitude)));
    }
/**
 * computes hash value that identifies a window in which the specified points lies
 * @param longitude longitude of a point
 * @param latitude latitude of a point
 * @return hash value of a window
 */
    public long getHash(double longitude, double latitude) {
        long lonHash = (long) (longitude / windowWidth);
        long latHash = (long) (latitude / windowHeight);
        long hash = ((latHash << 32) + lonHash);
        return hash;

    }
}
