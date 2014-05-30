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
import cvut.fel.nemetma1.evaluate.evaluator.DefaultEdgeEvaluator;
import cvut.fel.nemetma1.evaluate.evaluator.EdgeEvaluator;
import cvut.fel.nemetma1.evaluate.evaluator.ParametersOfRelevantTags;
import java.io.File;

/**
 * An aspect which evaluates the edge from the speed point of view
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class SpeedAspect extends AbstractAspect {

    private static final ParametersOfRelevantTags evaluationParameters;
    private static final double maximumValue;
    private static final double downhillMaxSpeedup = 0.4;

    static {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String filePath = cl.getResource("").getPath();
        File f = new File(filePath + "osm tags 2013 6.csv");
        evaluationParameters = new ParametersOfRelevantTags(f, 0, 1, 2, 3, 4);
        double max = -1;
        for (String s : evaluationParameters.getParametersKeySet()) {
            double d = evaluationParameters.getMultiplierForEntityKeyValue(s);
            if (d > max || max == -1) {
                max = d;
            }
        }
        System.out.println("speed max " + max);
        maximumValue = max * (downhillMaxSpeedup * max + 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getMaximumMultiplier() {
        return maximumValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EdgeEvaluator getEvaluator(CycleEdge edge) {
        return new DefaultEdgeEvaluator(edge, evaluationParameters, true, 8, downhillMaxSpeedup);
    }

    /**
     * returns "SpeedAspect"
     *
     * @return String "SpeedAspect"
     */
    @Override
    public String toString() {
        return "SpeedAspect"; //To change body of generated methods, choose Tools | Templates.
    }
}
