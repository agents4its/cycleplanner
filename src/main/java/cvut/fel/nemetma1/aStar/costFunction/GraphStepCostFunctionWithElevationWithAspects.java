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
package cvut.fel.nemetma1.aStar.costFunction;

import cvut.fel.nemetma1.aStar.search.CycleAction;
import aima.core.agent.Action;
import aima.core.search.framework.StepCostFunction;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.evaluate.aspects.Aspect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An edge cost function that takes Aspects into account to calculate the cost of an edge.
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class GraphStepCostFunctionWithElevationWithAspects implements StepCostFunction {

    private List<Aspect> aspects;
    private List<Double> weightsNormalized;
    private double averageSpeedMetersPerSecond;
/**
 * 
 * @param aspects a list of aspects that are considered when calculating cost of an edge
 * @param weights weights for the aspects
 * @param averageSpeedMetersPerSecond average speed of cyclist in meters per second
 * @throws IllegalArgumentException 
 */
    public GraphStepCostFunctionWithElevationWithAspects(List<Aspect> aspects, List<Double> weights, double averageSpeedMetersPerSecond) throws IllegalArgumentException {
        this.aspects = aspects;
        double sum = 0;
        for (double d : weights) {
            sum = sum + d;
        }
        weightsNormalized = new ArrayList<>();

        for (double d : weights) {
            weightsNormalized.add(d / sum);
        }
        System.out.println("weihts normalized costfunction "+weightsNormalized);
        this.averageSpeedMetersPerSecond = averageSpeedMetersPerSecond;
    }
/**
 * 
 * @param s current state (node)
 * @param a an action which represents edge in graph, needs to extend CycleAction
 * @param sDelta state (node) after taking an action
 * @return cost of an action
 */
    @Override
    public double c(Object s, Action a, Object sDelta) {
        if (a instanceof CycleAction) {
            CycleAction aa = (CycleAction) a;
            CycleEdge edge = aa.getEdgeToTake();
            double cost = 0;
            Iterator<Double> itWeightsNormalized = weightsNormalized.iterator();
            for (Iterator<Aspect> it = aspects.iterator(); it.hasNext();) {
                Aspect aspect = it.next();
                Double weight = itWeightsNormalized.next();
                if (weight > 0.001) {
                    cost = cost + aspect.evaluate(edge, averageSpeedMetersPerSecond) * weight;
                }
            }
            return cost;
        } else {
            System.out.println("Wrong Action");
            return 0;
        }
    }
}
