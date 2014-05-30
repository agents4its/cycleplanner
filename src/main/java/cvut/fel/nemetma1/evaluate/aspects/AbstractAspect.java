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
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public abstract class AbstractAspect implements Aspect {

    /**
    {@inheritDoc}
     */
    @Override
    public double evaluate(CycleEdge edge, double averageSpeedMetersPerSecond) {
//        EvaluationDetails ed = edge.getEvaluationDetails();
//        if (ed == null) {
//            ed = this.getEvaluator(edge).getEvaluationDetails();
//            edge.setEvaluationDetails(this, ed);
//        }
//        double evaluation = ed.getMutliplier() / averageSpeedMetersPerSecond + ed.getConstant();
//        return evaluation;
    	return 0;
    }
    /**
    {@inheritDoc}
     */
    @Override
    public EvaluationDetails createEvaluationDetails(CycleEdge edge) {
        EvaluationDetails ed;
        ed = this.getEvaluator(edge).getEvaluationDetails();
        return ed;
    }
    /**
    {@inheritDoc}
     */
    @Override
    public abstract EdgeEvaluator getEvaluator(CycleEdge edge);
    /**
    {@inheritDoc}
     */
    @Override
    public abstract double getMaximumMultiplier();
}
