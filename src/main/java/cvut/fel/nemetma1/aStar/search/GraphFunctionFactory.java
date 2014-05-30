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
import aima.core.search.framework.ActionsFunction;
import aima.core.search.framework.ResultFunction;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.graphWrapper.GraphWrapper;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides implementations of ActionsFunction and ResultFunction
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class GraphFunctionFactory {

    private static ResultFunction _resultFunction = null;

    /**
     * returns an implementation of ActionsFunction which returns all actions representing all outcoming edges from a node in a graph.
     *
     * @param graph
     */
    public static ActionsFunction getActionsFunction(GraphWrapper graph) {
        return new GraphFunctionFactory.GraphActionsFunction(graph);
    }

    /**
     * returns an implementation of ResultFunction which result returns an end node of an directed edge(=action).
     * @return an implementation of ResultFunction which result returns an end node of an directed edge(=action).
     */
    public static ResultFunction getResultFunction() {
        if (null == _resultFunction) {
            _resultFunction = new GraphFunctionFactory.GraphResultFunction();
        }
        return _resultFunction;
    }

    private static class GraphActionsFunction implements ActionsFunction {

        private GraphWrapper graph = null;

        public GraphActionsFunction(GraphWrapper graph) {
            this.graph = graph;
        }

        @Override
        public Set<Action> actions(Object state) {
            Set<Action> actions = new LinkedHashSet<>();
            if (state instanceof Node) {
                CycleNode currentLocation = (CycleNode) state;
                List<CycleEdge> linkedEdges = graph.getNodeOutcomingEdges(currentLocation.getId());
                for (CycleEdge linkEdge : linkedEdges) {
                    actions.add(new CycleAction(linkEdge));
                }
                return actions;

            } else {
                return actions;
            }
        }
    }

    private static class GraphResultFunction implements ResultFunction {

        public GraphResultFunction() {
        }

        @Override
        public Object result(Object s, Action a) {

            if (a instanceof CycleAction) {
                CycleAction gta = (CycleAction) a;

                return gta.getDestinationNode();
            }
            return s;
        }
    }
}
