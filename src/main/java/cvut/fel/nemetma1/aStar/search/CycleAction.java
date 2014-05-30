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
package cvut.fel.nemetma1.aStar.search;

import aima.core.agent.Action;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;

/**
 * An action in A* algorithm. Represents a directed edge in a graph.
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class CycleAction implements Action {

    private CycleNode destination;
    private CycleEdge edgeToTake;
/**
 * creates a CycleAction representing directed edge
 * @param edgeToTake directed edge to represent
 */
    public CycleAction(CycleEdge edgeToTake) {
        this.destination = edgeToTake.getToNode();
        this.edgeToTake = edgeToTake;
    }
/**
 {@inheritDoc}
 */
    @Override
    public boolean isNoOp() {
        return false;
    }
/**
 * returns the destination (end) node of the edge which is represented by this action
 * @return the destination (end) node of the edge which is represented by this action
 */
    public CycleNode getDestinationNode() {
        return destination;
    }
/**
 * A directed edge which is travelled when this action is taken
 * @return a directed edge which is travelled when this action is taken
 */
    public CycleEdge getEdgeToTake() {
        return edgeToTake;
    }


}
