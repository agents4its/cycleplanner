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
package cvut.fel.nemetma1.connectivity;

import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import java.util.Iterator;
import java.util.List;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Allows to get components of a @li 
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @param <TNode>
 * @param <TEdge> 
 */
public class StronglyConnectedComponentsGenerator<TNode extends Node,TEdge extends Edge> {
    


   private class EE implements EdgeFactory<Node, Edge> {
        @Override
        public Edge createEdge(Node sourceVertex, Node targetVertex) {
            Edge e = new Edge(sourceVertex.getId(), targetVertex.getId(), 0);
            return e;
        }
    }
/**
 * returns 
 * @param graph
 * @return strongly connected components of graph 
 */
    public List<DirectedGraph<TNode, TEdge>> getcomponents(Graph<TNode, TEdge> graph) {
        DirectedGraph<TNode, TEdge> directedGraph = new DefaultDirectedGraph(new EE());
        Iterator<TEdge> ie = graph.getAllEdges().iterator();
        Iterator<TNode> in = graph.getAllNodes().iterator();
        while(in.hasNext()){
        directedGraph.addVertex(in.next());
        }
        while(ie.hasNext()){
            TEdge e=ie.next();
        directedGraph.addEdge(graph.getNodeByNodeId(e.getFromNodeId()), graph.getNodeByNodeId(e.getToNodeId()), e);
        }
        
        StrongConnectivityInspector sci =
            new StrongConnectivityInspector(directedGraph);
        List<DirectedGraph<TNode, TEdge>> stronglyConnectedSubgraphs = sci.stronglyConnectedSubgraphs();
        return stronglyConnectedSubgraphs;
        
    }
}
