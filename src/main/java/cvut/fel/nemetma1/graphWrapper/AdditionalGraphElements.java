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
package cvut.fel.nemetma1.graphWrapper;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.openstreetmap.osm.data.coordinates.LatLon;

/**
 * Instances of this class can wrap additional graph elements (edges and nodes).
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class AdditionalGraphElements {

    private ArrayList<CycleEdge> additionalEdges;
    private HashMap<CycleEdge, CycleNode> onEdgesMap;
    private ArrayList<CycleNode> additionalNodes;
    int count = -1;

    public AdditionalGraphElements() {
        this.onEdgesMap = new HashMap<>();
        additionalNodes = new ArrayList<>();
        additionalEdges = new ArrayList<>();

    }

    public void addEdge(CycleEdge edge) {
        additionalEdges.add(edge);
    }

    public void addNode(CycleNode node) {

        additionalNodes.add(node);
    }
/**
 * adds node and connects it to a graph
 * @param node node to be added
 * @param onEdges edges of original graph that a node lies on
 * @param graph original graph to which append the node
 */
    public void addNodeAndCreateConnections(CycleNode node, List<CycleEdge> onEdges, Graph<CycleNode, CycleEdge> graph) {
        for (CycleEdge onEdge : onEdges) {
            if (onEdgesMap.containsKey(onEdge)) {
                CycleNode node2 = onEdgesMap.get(onEdge);
                CycleNode targetNode = onEdge.getToNode();
                double distanceN1 = LatLon.distanceInMeters(node.getLatitude(), node.getLongitude(), targetNode.getLatitude(), targetNode.getLongitude());
                double distanceN2 = LatLon.distanceInMeters(node2.getLatitude(), node2.getLongitude(), targetNode.getLatitude(), targetNode.getLongitude());
                if (distanceN1 < distanceN2) {
                    this.addEdge(createEdge(node2, node, onEdge));
//                    System.out.println("adding edge from " + node2 + " to " + node);
                } else {
                    this.addEdge(createEdge(node, node2, onEdge));
//                    System.out.println("adding edge from " + node + " to " + node2);
                }
            } else if (onEdge != null) {
                onEdgesMap.put(onEdge, node);
            }
        }
        additionalNodes.add(node);
    }
/**
 * returns negative IDs for additional elements
 * @return next unused ID for additional elements
 */
    public int getNextFreeID() {
        return --count;
    }

    public Collection<CycleEdge> getAdditionalEdges() {
        return additionalEdges;
    }

    public Collection<CycleNode> getAdditionalNodes() {
        return additionalNodes;
    }
/**
 * creates an edge
 * @param nodeFrom start node of an edge
 * @param nodeTo end node of an edge
 * @param edge edge of original graph on which nodes lie
 */
    private CycleEdge createEdge(CycleNode nodeFrom, CycleNode nodeTo, CycleEdge edge) {
        double comulativeLength = LatLon.distanceInMeters(
                new LatLon(nodeFrom.getLatitude(), nodeFrom.getLongitude()),
                new LatLon(nodeTo.getLatitude(), nodeTo.getLongitude()));
        float rises = (float) (edge.getRises() * (comulativeLength / edge.getLengthInMetres()));
        float drops = (float) (edge.getDrops() * (comulativeLength / edge.getLengthInMetres()));
        CycleEdge newedge = new CycleEdge(nodeFrom, nodeTo, comulativeLength, rises, drops);
        return newedge;
    }
}
