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

import java.util.ArrayList;
import java.util.List;

import aima.core.agent.Action;
import aima.core.search.framework.HeuristicFunction;
import aima.core.search.framework.StepCostFunction;
import cvut.fel.nemetma1.aStar.costFunction.GraphStepCostFunctionWithElevationWithAspects;
import cvut.fel.nemetma1.aStar.heuristicFunction.GraphStraightLineHeuristicWithMultiplier;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.evaluate.aspects.Aspect;
import cvut.fel.nemetma1.evaluate.aspects.ComfortAspect;
import cvut.fel.nemetma1.evaluate.aspects.QuietnessAspect;
import cvut.fel.nemetma1.evaluate.aspects.ShortestDistanceAspect;
import cvut.fel.nemetma1.evaluate.aspects.SpeedAspect;
import cvut.fel.nemetma1.graphWrapper.GraphWrapper;

/**
 * An implementation of A* search which finds a path based on cost functions of speed, comfort,quietness and straight-distance aspect.
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class AStarSearchWithAspects extends AbstractAStarSearch {

    private static final List<Aspect> aspects;
    private double averageSpeedMetersPerSecond;
    private List<Double> weights;

    static {
        aspects = new ArrayList<>();
        aspects.add(new SpeedAspect());
        aspects.add(new ComfortAspect());
        aspects.add(new QuietnessAspect());
        aspects.add(new ShortestDistanceAspect());
    }
/**
 * Creates an instance of A* search which considers following aspects when searching for route: speed, comfort,quietness, shortest-distance.
 * Weights determine importance of each aspect. 
 * @param averageSpeedMetersPerSecond
 * @param speedWeight
 * @param comfortWeight
 * @param quietnessWeight
 * @param shortestDistanceAspect 
 */
    public AStarSearchWithAspects(double averageSpeedMetersPerSecond, double speedWeight, double comfortWeight, double quietnessWeight, double shortestDistanceAspect) {

        weights = new ArrayList<>();
        weights.add(speedWeight);
        weights.add(comfortWeight);
        weights.add(quietnessWeight);
        weights.add(shortestDistanceAspect);
        System.out.println("weights"+weights);
        System.out.println("avgspeed"+averageSpeedMetersPerSecond);
        this.averageSpeedMetersPerSecond = averageSpeedMetersPerSecond;
    }
/**
 * Calculates actions necessary to take to obtain the optimal path from startNode to endNode in a graph.
 * @param startNode
 * @param endNode
 * @param graphWrapper
 * @return actions necessary to take to obtain the optimal path from startNode to endNode in a graph
 * @throws Exception 
 */
    @Override
    protected List<Action> findActions(CycleNode startNode, CycleNode endNode, GraphWrapper graphWrapper) throws Exception {
        StepCostFunction costWithElevation = new GraphStepCostFunctionWithElevationWithAspects(aspects, weights, averageSpeedMetersPerSecond);
        HeuristicFunction heuristicWithElevation = new GraphStraightLineHeuristicWithMultiplier(graphWrapper, endNode, aspects, weights, averageSpeedMetersPerSecond);
        List<Action> actions = findPathAStar(startNode, graphWrapper, endNode, costWithElevation, heuristicWithElevation);
        return actions;
    }
}
