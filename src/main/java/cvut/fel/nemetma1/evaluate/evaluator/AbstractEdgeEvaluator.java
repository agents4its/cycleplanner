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
import java.util.regex.Pattern;

/**
 * Implements methods that are equal for all the aspects.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public abstract class AbstractEdgeEvaluator implements EdgeEvaluator {

    protected CycleEdge edge;
    protected double edgePenalisationInSeconds = 0;
    protected double edgeSpeedMultiplier = 1;
    protected double edgeTime;
    protected double edgeLengthMultiplied;
    protected boolean enableLog;
    protected boolean evaluated = false;
    protected ParametersOfRelevantTags parametersOfRelevantTags;
    protected String log = "";

    /**
     * Creates an evaluator for an edge with a multipliers and slowdown constants. These are parameters that are specified for every relevant OSM tag.
     *
     * @param edge An edge to be evaluated
     * @param evaluationParameters Parameters of multiplier and slowdown constant for all relevant OSM tags
     * @param enableLog enables log if true
     */
    public AbstractEdgeEvaluator(CycleEdge edge, ParametersOfRelevantTags evaluationParameters, boolean enableLog) {
        this.enableLog = enableLog;
        this.edge = edge;
        this.parametersOfRelevantTags = evaluationParameters;
    }

    /**
     * Creates an evaluator for an edge with a multipliers and slowdown constants. These are parameters that are specified for every relevant OSM tag.
     * The log is disabled.
     *
     * @param edge An edge to be evaluated
     * @param evaluationParameters Parameters of multiplier and slowdown constant for all relevant OSM tags
     */
    public AbstractEdgeEvaluator(CycleEdge edge, ParametersOfRelevantTags evaluationParameters) {
        this(edge, evaluationParameters, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract double evaluateEdge(double averageSpeedMeterPerSecond);

    /**
     * evaluates edge without specifying the average speed. Even with average speed not specified it is possible to calculate other elements that
     * affect the cost function. Then when average speed is specified, the cost of an edge is calculated faster. This function thus can be called
     * after the graph is created to evaluate the edge.
     */
    protected abstract void evaluateEdgeWithoutAverageSpeed();

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgePenalisationInSeconds() {
        evaluateEdgeWithoutAverageSpeed();
        return edgePenalisationInSeconds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getEdgeSpeedMultiplier() {
        evaluateEdgeWithoutAverageSpeed();
        return edgeSpeedMultiplier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLog() {
        if (enableLog) {
            evaluateEdgeWithoutAverageSpeed();
            return log;
        }
        return "Edge evaluator log is disabled.";
    }

    /**
     * adds text to a log if logging is enabled
     *
     * @param text
     */
    protected void addToLog(String text) {
        if (enableLog) {
            this.log += text;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationDetails getEvaluationDetails() {
        evaluateEdgeWithoutAverageSpeed();
        return new EvaluationDetails(edgeLengthMultiplied, edgePenalisationInSeconds);
    }

    /**
     * PattertnGroup class is used to group OSM tags into groups.
     */
    protected class PatternGroup {

        private String name;
        private double multiplier = 1;
        private double constant = 0;
        private boolean matched = false;
        private String enteredTagsLog;
        private final Pattern PATTERN;
        private final PatternGroupRule groupRuleForMultiplier;
        private final PatternGroupRule groupRuleForConstant;

        /**
         * creates a group of OSM tags.
         *
         * @param PATTERN pattern specifies if an OSM tag belongs to this group or not
         * @param groupRuleForMultiplier specifies how to handle multiple values of tag multipliers
         * @param groupRuleForConstant specifies how to handle multiple values of tag slowdown constants
         */
        public PatternGroup(Pattern PATTERN, PatternGroupRule groupRuleForMultiplier, PatternGroupRule groupRuleForConstant) {
            this.enteredTagsLog = "";
            this.PATTERN = PATTERN;
            this.groupRuleForMultiplier = groupRuleForMultiplier;
            this.groupRuleForConstant = groupRuleForConstant;
        }

        /**
         * creates a group of OSM tags.
         *
         * @param name name of the pattern
         * @param PATTERN pattern specifies if an OSM tag belongs to this group or not
         * @param groupRuleForMultiplier specifies how to handle multiple values of tag multipliers
         * @param groupRuleForConstant specifies how to handle multiple values of tag slowdown constants
         */
        public PatternGroup(String name, Pattern PATTERN, PatternGroupRule groupRuleForMultiplier, PatternGroupRule groupRuleForConstant) {
            this(PATTERN, groupRuleForMultiplier, groupRuleForConstant);
            this.enteredTagsLog = "";
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getEnteredTagsLog() {
            return enteredTagsLog;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public double getConstant() {
            return constant;
        }

        /**
         * returns true if the instance was updated with at least one matching OSM tag
         *
         * @return true if the instance was updated with at least one matching OSM tag , otherwise false
         */
        public boolean isMatched() {
            return matched;
        }

        /**
         * if tag matches this group the values of group multiplier and slowdown constant are updated
         *
         * @param tag
         * @param newMultiplier
         * @param newConstant
         */
        public void updateIfMatches(String tag, double newMultiplier, double newConstant) {
            if (matchesTag(tag)) {
                this.matched = true;
                updateMultiplier(newMultiplier);
                updateConstant(newConstant);
                enteredTagsLog += "[" + tag + " multiplier: " + newMultiplier + " constant: " + newConstant + "]";
            }
        }

        /**
         * returns true if the tag matches the pattern specified when this PatternGroup was created
         *
         * @param tag tag to check
         * @return true if matches, otherwise false
         */
        public boolean matchesTag(String tag) {
            return PATTERN.matcher(tag).matches();
        }

        private void updateMultiplier(double newValue) {
            this.multiplier = updateValue(multiplier, newValue, groupRuleForMultiplier);
        }

        private void updateConstant(double newValue) {
            this.constant = updateValue(constant, newValue, groupRuleForConstant);

        }

        private double updateValue(double currentValue, double newValue, PatternGroupRule updateRule) {
            if (updateRule == PatternGroupRule.MAX) {
                if (newValue > currentValue) {
                    currentValue = newValue;
                }
            }
            if (updateRule == PatternGroupRule.MIN) {
                if (newValue < currentValue) {
                    currentValue = newValue;
                }
            }
            if (updateRule == PatternGroupRule.ADD) {
                currentValue = newValue + currentValue;
            }
            if (updateRule == PatternGroupRule.MULTIPLY) {
                currentValue = newValue * currentValue;
            }
            return currentValue;
        }

        /**
         * Returns a string with list of tags that were matched by this pattern group instance, their multipliers and slowdown constants. At the end *
         * the text contains the group multiplier and the group slowdown constant for an edge
         *
         * @return a string with a log
         */
        @Override
        public String toString() {
            String s = this.getName();
            s += "\n";
            if (!this.enteredTagsLog.equals("")) {
                s += this.enteredTagsLog;
                s += "\n";
            } else {
                s += "no tags matching this group";
                s += "\n";
            }
            s += " final multiplier: " + multiplier + ", final constant: " + constant;
            s += "\n";
            return s;
        }
    }

    /**
     * rules that specify what to do if more tags of same pattern groups are attached to an edge.
     * Selects maximum, minimum, adds tags to a sum, or multiplies tags.
     */
    protected enum PatternGroupRule {

        MAX, MIN, ADD, MULTIPLY;
    }
}
