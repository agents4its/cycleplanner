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
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Wraps the graph and allows to add additional edges and nodes without modifying the original graph. Additional nodes and edges must not have IDs
 * already used in the original graph.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class GraphWrapper {

    Collection<CycleNode> additionalNodes = new ArrayList<>();
    Collection<CycleEdge> additionalEdges = new ArrayList<>();
    Graph<CycleNode, CycleEdge> graph;

    public GraphWrapper(Graph<CycleNode, CycleEdge> graph, AdditionalGraphElements additionalGraphElements) {
        if (additionalGraphElements != null) {
            this.additionalNodes = additionalGraphElements.getAdditionalNodes();
            this.additionalEdges = additionalGraphElements.getAdditionalEdges();
        }
        this.graph = graph;

    }

    public CycleNode getNodeByNodeId(long nodeId) {
        for (CycleNode node : additionalNodes) {
            if (node.getId() == nodeId) {
                return node;
            }
        }
        return graph.getNodeByNodeId(nodeId);
    }

    public CycleEdge getEdge(long fromNodeId, long toNodeId) {
        for (CycleEdge edge : additionalEdges) {
            if (edge.getFromNodeId() == fromNodeId && edge.getToNodeId() == toNodeId) {
                return edge;
            }
        }
        return graph.getEdge(fromNodeId, toNodeId);
    }

    public Collection<CycleNode> getAllNodes() {
        ArrayList<CycleNode> list = new ArrayList<>();
        list.addAll(additionalNodes);
        Collection<CycleNode> listgraph = graph.getAllNodes();
        if (listgraph != null) {
            list.addAll(graph.getAllNodes());
        }
        return list;
    }

    public List<CycleEdge> getNodeIncomingEdges(long nodeId) {
        ArrayList<CycleEdge> list = new ArrayList<>();
        for (CycleEdge edge : additionalEdges) {
            if (edge.getToNodeId() == nodeId) {
                list.add(edge);
            }
        }
        List<CycleEdge> graphlist = graph.getNodeIncomingEdges(nodeId);
        if (graphlist != null) {
            list.addAll(graphlist);
        }

        return list;
    }

    public List<CycleEdge> getNodeOutcomingEdges(long nodeId) {
        ArrayList<CycleEdge> list = new ArrayList<>();
        for (CycleEdge edge : additionalEdges) {
            if (edge.getFromNodeId() == nodeId) {
                list.add(edge);
            }
        }
        List<CycleEdge> graphlist = graph.getNodeOutcomingEdges(nodeId);
        if (graphlist != null) {
            list.addAll(graphlist);
        }
        return list;
    }

    public Collection<CycleEdge> getAllEdges() {
        ArrayList<CycleEdge> list = new ArrayList<>();
        list.addAll(additionalEdges);
        Collection<CycleEdge> graphlist = graph.getAllEdges();
        if (graphlist != null) {
            list.addAll(graphlist);
        }
        return list;
    }

    public Set<Long> getAllSuccessors(long node) {
        final Set<Long> successors;

        successors = new HashSet<>();
        for (Edge edge : this.getNodeOutcomingEdges(node)) {
            successors.add(edge.getToNodeId());
        }

        return successors;
    }

    public Set<Long> getAllPredecessors(long node) {
        final Set<Long> predecessors;

        predecessors = new HashSet<>();
        for (Edge edge : this.getNodeIncomingEdges(node)) {
            predecessors.add(edge.getFromNodeId());
        }

        return predecessors;
    }

    public Set<Long> getAllNeighbors(long node) {
        final Set<Long> neighbors;

        neighbors = new HashSet<>();
        neighbors.addAll(this.getAllSuccessors(node));
        neighbors.addAll(this.getAllPredecessors(node));

        return neighbors;
    }

    @Override
    public String toString() {
        String s = "Graph [getAllNodesCount()=" + getAllNodes().size() + ", getAllEdgesCount()=" + getAllEdges().size()
                + "]\n";

        for (CycleNode node : getAllNodes()) {
            s = s.concat(node.toString() + "\n");
            for (CycleEdge edge : getNodeOutcomingEdges(node.getId())) {
                s = s.concat(" -> " + edge.toString() + "\n");
            }
        }

        return s;
    }

    public boolean containsNodeByNodeId(long nodeId) {
        for (CycleNode node : additionalNodes) {
            if (node.getId() == nodeId) {
                return true;
            }
        }
        return graph.containsNodeByNodeId(nodeId);
    }
}
