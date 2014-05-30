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
package cvut.fel.nemetma1.graphCreator;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;

/**
 * Provides a graph
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public interface GraphProvider {

    /**
     * Returns a graph
     * @return graph
     */
    public Graph<CycleNode, CycleEdge> getGraph();

    /**
     * recreates the graph
     */
    public void recreateGraphFromOSM();
    //@todo test saving and retrieving and recreating or remove
}
