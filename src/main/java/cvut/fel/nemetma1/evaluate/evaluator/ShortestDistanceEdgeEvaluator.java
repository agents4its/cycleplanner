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

import cvut.fel.nemetma1.dataStructures.CycleEdge;

/**
 * Evaluates the edge by its length and elevation gain
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class ShortestDistanceEdgeEvaluator extends AbstractEdgeEvaluator {

    /**
     * ShortestDistanceEdgeEvaluator evaluates the edge by its length and elevation gain.
     *
     * @param edge edge to evaluate
     */
    public ShortestDistanceEdgeEvaluator(CycleEdge edge) {
        super(edge, null);
    }

    /**
     * ShortestDistanceEdgeEvaluator evaluates the edge by its length and elevation gain.
     *
     * @param edge edge to evaluate
     * @param enableLog if true, enables the log
     */
    public ShortestDistanceEdgeEvaluator(CycleEdge edge, boolean enableLog) {
        super(edge, null, enableLog);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double evaluateEdge(double averageSpeedMetersPerSecond) {

        evaluateEdgeWithoutAverageSpeed();
        edgeTime = edgeLengthMultiplied / (averageSpeedMetersPerSecond);
        addToLog("\nEdgeSpeedMultiplier: " + 1 + ". EdgePenalisationInSeconds: " + 0);
        addToLog("\nEdge rises: " + edge.getRises() + ". Edge drops: " + edge.getDrops());
        addToLog("\nAvg speed: " + averageSpeedMetersPerSecond + ". Edge calculated speed: " + averageSpeedMetersPerSecond);
        addToLog("\nEdge length: " + edge.getLengthInMetres() + ". EdgeTime: " + edgeTime);
        evaluated = true;
        return edgeTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void evaluateEdgeWithoutAverageSpeed() {
        addToLog("Evaluating edge:" + edge + "\n");
        addToLog("with tags: " + edge.getOSMtags() + "\n");
        edgeLengthMultiplied = (edge.getLengthInMetres() + edge.getRises() * 4);
        edgePenalisationInSeconds = 0;
    }
}
