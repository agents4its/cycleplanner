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
package cz.agents.cycleplanner.aStar;

import java.util.Collection;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

/**
 * Represents one A* search. Contains attributes for searching, based on them is able to return a path.
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public interface AStarSearch {

    /**
     * Returns path which is solution for the defined A* search. 
     * @param startNode start node (origin)
     * @param endNode end node (destination)
     * @param graphWrapper graphWrapper containing start and end node in which to search
     * @return collection of nodes on a path from the start node to the end node (both included)
     * @throws Exception
     */
    public Collection<CycleNode> findPath(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph) throws Exception;
    /**
     * Returns path which is solution for the defined A* search. 
     * @param startNode start node (origin)
     * @param endNode end node (destination)
     * @param graphWrapper graphWrapper containing start and end node in which to search
     * @return collection of edges on a path from the start node to the end node 
     * @throws Exception
 * @throws Exception 
 */
    public Collection<CycleEdge> findPathEdges(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph) throws Exception;
}
