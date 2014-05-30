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
package cvut.fel.nemetma1.evaluate.evaluator;

/**
 * EdgeEvaluator can evaluate the edge returning the cost of the edge
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public interface EdgeEvaluator {

    /**
     * evaluates edge provided the average cruising speed of a cyclist, reads the tags saved in the edge
     *
     * @param averageSpeedMetersPerSecond average cruising speed of a cyclist
     * @return cost of the edge
     */
    public double evaluateEdge(double averageSpeedMetersPerSecond);

    /**
     * returns total slowdown constant = penalisation in seconds for the edge being evaluated
     *
     * @return total slowdown constant = penalisation in seconds for the edge being evaluated
     */
    public double getEdgePenalisationInSeconds();

    /**
     * returns total multiplier for the edge being evaluated = multiplier with which the average speed is multiplied
     *
     * @return total multiplier for the edge being evaluated = multiplier with which the average speed is multiplied
     */
    public double getEdgeSpeedMultiplier();

    /**
     * returns evaluation details of the edge (containing total multiplier and total slowdown constant)
     *
     * @return evaluation details of the edge
     */
    public EvaluationDetails getEvaluationDetails();

    /**
     * Returns log of the evaluation process of null if evaluating is disabled.
     *
     * @return log of the evaluation process of null if evaluating is disabled
     */
    public String getLog();
}
