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
import cvut.fel.nemetma1.evaluate.evaluator.ShortestDistanceEdgeEvaluator;
import cvut.fel.nemetma1.evaluate.evaluator.EdgeEvaluator;

/**
 * An aspect which evaluates the edge from the shorthest distance point of view
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class ShortestDistanceAspect extends AbstractAspect {

    private static final double MAXIMUM_MULTIPLIER = 1;
    /**
    {@inheritDoc}
     */
    @Override
    public double getMaximumMultiplier() {
        return MAXIMUM_MULTIPLIER;
    }
    /**
    {@inheritDoc}
     */
    @Override
    public EdgeEvaluator getEvaluator(CycleEdge edge) {
        return new ShortestDistanceEdgeEvaluator(edge, true);
    }

    /**
     * returns "ShortestDistanceAspect"
     *
     * @return String "ShortestDistanceAspect"
     */
    @Override
    public String toString() {
        return "ShortestDistanceAspect"; //To change body of generated methods, choose Tools | Templates.
    }
}
