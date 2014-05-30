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
package cvut.fel.nemetma1.evaluate.aspects;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.evaluate.evaluator.EdgeEvaluator;
import cvut.fel.nemetma1.evaluate.evaluator.EvaluationDetails;

/**
 * An aspect based no which the edge can be evaluated
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public interface Aspect {
    /**
     * an evaluator of an aspect for an edge
     * @param edge edge to be evaluated
     * @return EdgeEvaluator for the edge
     */
    public EdgeEvaluator getEvaluator(CycleEdge edge);
    /**
     * returns maximum multiplier of this aspect. this is the maximum number by which the average cruising speed could be multiplied.
     * It is important for the heuristic function so the heuristic to be admissible.
     * @return value of maximum multiplier for an aspect
     */
    public double getMaximumMultiplier();
    /**
     * evaluates the edge
     * @param edge edge to be evaluated
     * @param averageSpeedMetersPerSecond average cruising speed of a cyclist
     * @return cost of the edge by this aspect
     */
    public double evaluate(CycleEdge edge, double averageSpeedMetersPerSecond);
    /**
     * calculates evaluation details for the edge
     * @param edge edge to be evaluated
     * @return evaluation details of the edge by an aspect
     */
    public EvaluationDetails createEvaluationDetails(CycleEdge edge);
}
