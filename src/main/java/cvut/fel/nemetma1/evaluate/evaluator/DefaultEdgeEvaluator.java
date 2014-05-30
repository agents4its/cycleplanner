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
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * default edge evaluator evaluates the edge based on the 7 groups of tags: for bicycle, dismount, surface quality, crossings, steps, offroad, motor roads.
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class DefaultEdgeEvaluator extends AbstractEdgeEvaluator {

    private static final Pattern FOR_BICYCLES_PATTERN = Pattern.compile("relation::route::bicycle|way::bicycle::designated|way::bicycle::permissive|"
            + "way::bicycle::yes|way::cycleway::lane|way::cycleway::share_busway|way::cycleway::shared_lane|way::cycleway::track|way::cycleway:left::lane|"
            + "way::cycleway:left::share_busway|way::cycleway:left::shared_lane|way::cycleway:right::lane|way::cycleway:right::share_busway|"
            + "way::cycleway:right::shared_lane|way::highway::cycleway");
    private static final Pattern DISMOUNT_PATTERN = Pattern.compile("way::bicycle::dismount|way::footway::crossing|way::footway::sidewalk|"
            + "way::highway::footway|way::highway::footway;path|way::highway::pedestrian");
    private static final Pattern SURFACE_QUALITY_PATTERN = Pattern.compile("way::smoothness::bad|way::smoothness::horrible|"
            + "way::smoothness::intermediate|way::smoothness::very_bad|way::surface::cobblestone|way::surface::compacted|"
            + "way::surface::dirt|way::surface::grass|way::surface::gravel|way::surface::ground|way::surface::mud|way::surface::paving_stones|"
            + "way::surface::sand|way::surface::setts|way::surface::unpaved|way::surface::wood|way::smoothness::excellent");
    private static final Pattern CROSSING_PATTERN = Pattern.compile("node::highway::crossing|node::crossing::island|node::crossing::traffic_signals|"
            + "node::crossing::uncontrolled|node::crossing::unmarked|node::crossing::yes|node::crossing::zebra|node::highway::traffic_signals");
    private static final Pattern STEPS_ELEVATOR_PATTERN = Pattern.compile("way::highway::steps|node::highway::elevator|node::highway::steps");
    private static final Pattern OFFROAD_PATTERN = Pattern.compile("way::access::agricultural|way::access::forestry|way::highway::bridleway|"
            + "way::highway::path|way::highway::track");
    private static final Pattern MOTOR_ROADS_PATTERN = Pattern.compile("way::highway::living_street|way::highway::primary|way::highway::primary_link|"
            + "way::highway::residential|way::highway::secondary|way::highway::secondary_link|way::highway::service|way::highway::tertiary|"
            + "way::highway::tertiary_link");
    protected final double UPHILL_MULTIPLIER;
    protected final double DOWNHILL_MAX_SPEEDUP;
    protected double downhillSpeedupCoeficient;
    private boolean steps = false;

    public DefaultEdgeEvaluator(CycleEdge edge, ParametersOfRelevantTags evaluationParameters, double uphillMultiplier, double downhillSpeedup) {
        super(edge, evaluationParameters);
        this.UPHILL_MULTIPLIER = uphillMultiplier;
        this.DOWNHILL_MAX_SPEEDUP = downhillSpeedup;

    }

    public DefaultEdgeEvaluator(CycleEdge edge, ParametersOfRelevantTags evaluationParameters, boolean enableLog, double uphillMultiplier, double downhillSpeedup) {
        super(edge, evaluationParameters, enableLog);
        this.UPHILL_MULTIPLIER = uphillMultiplier;
        this.DOWNHILL_MAX_SPEEDUP = downhillSpeedup;

    }

    @Override
    protected void evaluateEdgeWithoutAverageSpeed() {
        if (evaluated) {
            return;
        }

        addToLog("Evaluating edge:" + edge + "\n");
        addToLog("with tags: " + edge.getOSMtags() + "\n");

        PatternGroup forBicycle = new PatternGroup("forBicycle", FOR_BICYCLES_PATTERN, PatternGroupRule.MAX, PatternGroupRule.MAX);
        PatternGroup dismount = new PatternGroup("dismount", DISMOUNT_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        PatternGroup surfaceQuality = new PatternGroup("surfaceQuality", SURFACE_QUALITY_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        PatternGroup crossing = new PatternGroup("crossing", CROSSING_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        PatternGroup stepsElevator = new PatternGroup("stepsElevator", STEPS_ELEVATOR_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        PatternGroup offroad = new PatternGroup("offroad", OFFROAD_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        PatternGroup others = new PatternGroup("others", MOTOR_ROADS_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        ArrayList<PatternGroup> patternGroupList = new ArrayList<>();

        patternGroupList.add(forBicycle);
        patternGroupList.add(dismount);
        patternGroupList.add(surfaceQuality);
        patternGroupList.add(crossing);
        patternGroupList.add(stepsElevator);
        patternGroupList.add(offroad);
        patternGroupList.add(others);

        Set<String> edgeTags = edge.getOSMtags();
        if (edgeTags != null) {
            for (String edgeTag : edge.getOSMtags()) {
                if (parametersOfRelevantTags.containsEntityKeyValue(edgeTag)) {
                    double coeficient = parametersOfRelevantTags.getMultiplierForEntityKeyValue(edgeTag);
                    double constant = parametersOfRelevantTags.getConstantForEntityKeyValue(edgeTag);
                    for (PatternGroup patternGroup : patternGroupList) {
                        if (patternGroup.matchesTag(edgeTag)) {
                            patternGroup.updateIfMatches(edgeTag, coeficient, constant);
                            break;
                        }
                    }
                }
            }
        }

        for (PatternGroup patternGroup : patternGroupList) {
            if (patternGroup.isMatched()) {
                addToLog("Tag group: " + patternGroup.toString() + "\n");
            }
        }

        if (stepsElevator.isMatched()) {
            steps = true;
            edgeSpeedMultiplier *= stepsElevator.getMultiplier();
            edgeLengthMultiplied = (edge.getLengthInMetres() + edge.getRises() * UPHILL_MULTIPLIER) / edgeSpeedMultiplier;

            edgePenalisationInSeconds += stepsElevator.getConstant();
            return;
        }

        edgeSpeedMultiplier = 1;
        edgePenalisationInSeconds = 0;
        boolean ignoreDismount = false;


        if (forBicycle.isMatched()) {
            ignoreDismount = true;
        }
        double group1multiplier = Math.min(offroad.getMultiplier(), surfaceQuality.getMultiplier());
        if (!ignoreDismount) {
            group1multiplier = Math.min(group1multiplier, dismount.getMultiplier());
        }

        edgeSpeedMultiplier = 1 * forBicycle.getMultiplier() * group1multiplier;
        if (dismount.isMatched()) {
            edgePenalisationInSeconds = 0 + crossing.getConstant();
        }
        downhillSpeedupCoeficient = 1;
        if ((edge.getDrops() / edge.getLengthInMetres()) > 0.001) {
            downhillSpeedupCoeficient = (edgeSpeedMultiplier * DOWNHILL_MAX_SPEEDUP);
            if (edge.getDrops() / edge.getLengthInMetres() < 0.1) {
                downhillSpeedupCoeficient = downhillSpeedupCoeficient * (edge.getDrops() / edge.getLengthInMetres()) / 0.1 + 1;
            } else {
                downhillSpeedupCoeficient = downhillSpeedupCoeficient + 1;
            }
        }
        edgeLengthMultiplied = (edge.getLengthInMetres() + edge.getRises() * UPHILL_MULTIPLIER) / (edgeSpeedMultiplier * downhillSpeedupCoeficient);
        addToLog("\nEdgeSpeedMultiplier: " + edgeSpeedMultiplier + ". EdgePenalisationInSeconds: " + edgePenalisationInSeconds);
        addToLog("\nEdge rises: " + edge.getRises() + ". Edge drops: " + edge.getDrops());
        addToLog("\nEdge length: " + edge.getLengthInMetres());
        evaluated = true;

    }

    @Override
    public double evaluateEdge(double averageSpeedMetersPerSecond) {
        evaluateEdgeWithoutAverageSpeed();
        if (steps) {
            return steps(averageSpeedMetersPerSecond);
        }
        return calculateEdgeTime(averageSpeedMetersPerSecond);
    }

    ;

    private double steps(double averageSpeedMetersPerSecond) {
        edgeTime = edgeLengthMultiplied / averageSpeedMetersPerSecond + edgePenalisationInSeconds;
//            System.out.println("steps: edgetime " + edgeTime);
        return edgeTime;
    }

    private double calculateEdgeTime(double averageSpeedMetersPerSecond) {
        edgeTime = edgeLengthMultiplied / averageSpeedMetersPerSecond + edgePenalisationInSeconds;
        addToLog("\nAvg speed: " + averageSpeedMetersPerSecond + ". Edge calculated speed: " + edgeSpeedMultiplier * averageSpeedMetersPerSecond);
        addToLog("\nEdgeTime: " + edgeTime);
        return edgeTime;
    }
}
