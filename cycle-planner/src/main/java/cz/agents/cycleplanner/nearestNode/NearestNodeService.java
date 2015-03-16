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

import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.wp5common.location.Location;


public interface NearestNodeService<TNode extends Node, TEdge extends Edge> {  
    /**
      * returns the closest point to a specified location

     * @param location location
     * @return closest point to a specified location
     */
    public TNode getNearestNode(Location location);    
}
