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
package cz.agents.cycleplanner.aStar.aima;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import aima.core.agent.Action;
import aima.core.search.framework.DefaultGoalTest;
import aima.core.search.framework.GraphSearch;
import aima.core.search.framework.HeuristicFunction;
import aima.core.search.framework.Problem;
import aima.core.search.framework.Search;
import aima.core.search.framework.SearchAgent;
import aima.core.search.framework.StepCostFunction;
import cz.agents.cycleplanner.aStar.AStarSearch;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.elements.Edge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;

/**
 * @author Marcel
 */
public abstract class AbstractAStarSearch implements AStarSearch {

    public AbstractAStarSearch() {
    }

    /**
     * Finds an optimal path in a graph provided with a cost and a heuristic function
     *
     * @param startNode start of path
     * @param graph graph in which search occurs
     * @param endNode goal of the path
     * @param costFunction cost function to evaluate edges
     * @param heuristic heuristic function to evaluate edges
     * @return list of actions necessary to get from origin to destination
     * @throws Exception
     */
    protected List<Action> findPathAStar(CycleNode startNode, Graph<? extends Node, ? extends Edge> graph, CycleNode endNode, StepCostFunction costFunction, HeuristicFunction heuristic) throws Exception {
        
    	Problem problem = new Problem(startNode,
                GraphFunctionFactory.getActionsFunction(graph),
                GraphFunctionFactory.getResultFunction(), new DefaultGoalTest(endNode),
                costFunction);
        
    	Search search = new aima.core.search.informed.AStarSearch(new GraphSearch(),
                heuristic);
        
        SearchAgent agent;
        agent = new SearchAgent(problem, search);
        List<Action> actions = agent.getActions();
        
        agent.getInstrumentation().list(System.out);
        
        return actions;
    }

    /**
     * returns an ordered list of CycleNode-s that are on the path from origin to destination
     *
     * @param startNode
     * @param actions list of actions
     * @return ordered list of CycleNode-s that are on the path from origin to destination 
     */
    protected static List<CycleNode> buildPathCycleNode(CycleNode startNode, List<Action> actions) {
        ArrayList<CycleNode> path = new ArrayList<>();
        path.add(startNode);
        for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
            Action a = it.next();
            if (a.isNoOp()) {
                path.add(startNode);
                return path;
            }
            CycleAction acycle = (CycleAction) a;
            path.add(acycle.getDestinationNode());
        }
        return path;
    }

    /**
     * returns an ordered list of CycleEdge-s that are on the path from origin to destination
     *
     * @param startNode
     * @param actions list of actions
     * @return ordered list of CycleEdge-s that are on the path from origin to destination
     */
    protected static List<CycleEdge> buildPathCycleEdge(CycleNode startNode, List<Action> actions) {
        ArrayList<CycleEdge> path = new ArrayList<>();
        for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
            Action a = it.next();
            if (a.isNoOp()) {
                path.add(new CycleEdge(startNode, startNode, 0));
                return path;
            }
            CycleAction acycle = (CycleAction) a;
            path.add(acycle.getEdgeToTake());
        }
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CycleNode> findPath(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph) throws Exception {
        List<Action> actions = findActions(startNode, endNode, graph);
        return buildPathCycleNode(startNode, actions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CycleEdge> findPathEdges(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph) throws Exception {
        long time = System.currentTimeMillis();
    	List<Action> actions = findActions(startNode, endNode, graph);
    	System.out.println("findActions time: "+(System.currentTimeMillis() - time));
    	
        return buildPathCycleEdge(startNode, actions);
    }

    protected abstract List<Action> findActions(CycleNode startNode, CycleNode endNode, Graph<? extends Node, ? extends Edge> graph) throws Exception;
}
