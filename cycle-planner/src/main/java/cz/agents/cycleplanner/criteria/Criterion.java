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
package cz.agents.cycleplanner.criteria;

import cz.agents.cycleplanner.dataStructures.CycleEdge;

/**
 * An aspect based no which the edge can be evaluated
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public interface Criterion {

    /**
     * returns maximum multiplier of this aspect. this is the maximum number by which the average cruising speed could be multiplied.
     * It is important for the heuristic function so the heuristic to be admissible.
     * @return value of maximum multiplier for an aspect
     */
    public double getHeuristicValue();
    
    public double getWeightedHeuristicValue();
    
    /**
     * evaluates the edge
     * @param edge edge to be evaluated
     * @param oneOverAverageSpeed average cruising speed of a cyclist
     * @return cost of the edge by this aspect
     */
    public double evaluate(CycleEdge edge, double base);
    
    public double evaluateWithWeight(CycleEdge edge, double base);
}
