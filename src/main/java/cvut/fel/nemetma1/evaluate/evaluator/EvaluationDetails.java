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

import java.io.Serializable;

/**
 * This class holds evaluation details for an edge. Can be used to save pre-calculated parts of a cost function of an edge and later when average
 * cruising speed of a cyclist is specified can be used to calculate the complete cost of an edge.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class EvaluationDetails implements Serializable {

    private double mutliplier;
    private double constant;
/**
 * creates evaluation details multiplier
 * @param mutliplier a number by which average cruising speed or an user is multiplied
 * @param constant a penalisation in seconds for an edge
 */
    public EvaluationDetails(double mutliplier, double constant) {
        this.mutliplier = mutliplier;
        this.constant = constant;
    }
/**
 * get  a multiplier
 * @return a number by which average cruising speed or an user is multiplied
 */
    public double getMutliplier() {
        return mutliplier;
    }
/**
 * set a multiplier
 * @param mutliplier a number by which average cruising speed or an user is multiplied
 */
    public void setMutliplier(double mutliplier) {
        this.mutliplier = mutliplier;
    }
/**
 * get a constant
 * @return a penalisation in seconds for an edge
 */
    public double getConstant() {
        return constant;
    }
/**
 * set a constant
 * @param constant a penalisation in seconds for an edge
 */
    public void setConstant(double constant) {
        this.constant = constant;
    }
}
