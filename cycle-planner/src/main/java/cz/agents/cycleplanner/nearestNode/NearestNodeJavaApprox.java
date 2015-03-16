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
package cz.agents.cycleplanner.nearestNode;

import java.util.ArrayList;

import cz.agents.cycleplanner.util.GeoCalculationsHelper;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.wp5common.location.Location;

public class NearestNodeJavaApprox<TNode extends Node, TEdge extends Edge> implements NearestNodeService<TNode, TEdge> {

    private ArrayList<TNode> nodes;

    public NearestNodeJavaApprox(Graph<TNode, TEdge> graph) {
        nodes = new ArrayList<>(graph.getAllNodes());
    }

    /**
     * returns the closest node in the graph from specified geographical location. Based on a distance function that calculates approximate distance.
     * Can not be used on large distances or in areas where some points lye north from and some lye south from equator. Cannot be used for points when
     * some lye west from prime meridian and some lye east of it.
     *
     * @param location
     * @return closest node in the graph from specified geographical location
     */
    @Override
    public TNode getNearestNode(Location location) {
    	// TODO use projected value
        double referenceLat = location.getLatitude();
        double referenceLon = location.getLongitude();
        final double longMultip = GeoCalculationsHelper.lengthOfLongitudeDegree(referenceLat);
        final double latMultip = GeoCalculationsHelper.lengthOfLatitudeDegree(referenceLat);
        double min = -1;
        int nodeNumber = -1;
        for (int i = 0; i < nodes.size(); i++) {
            TNode node = nodes.get(i);
            double dist = GeoCalculationsHelper.distanceE2Squared(
                    referenceLat * latMultip, referenceLon * longMultip,
                    node.getLatitude() * latMultip, node.getLongitude() * longMultip);
            if (dist < min || min == -1) {
                min = dist;
                nodeNumber = i;
            }
        }
        return nodes.get(nodeNumber);
    }

}
