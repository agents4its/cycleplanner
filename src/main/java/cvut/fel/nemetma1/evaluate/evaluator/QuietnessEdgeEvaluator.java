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
 * default edge evaluator evaluates the edge based on the 7 groups of tags: for bicycle, dismount, surface quality, crossings, steps, offroad, motor
 * roads.
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class QuietnessEdgeEvaluator extends AbstractEdgeEvaluator {

    private static final Pattern QUIET_ROADS_PATTERN = Pattern.compile("way::bicycle::designated|way::highway::cycleway|way::highway::path|way::highway::track|way::cycleway::track|way::footway::sidewalk|way::highway::footway|way::highway::footway;path|way::highway::pedestrian|way::access::agricultural|way::access::forestry|way::highway::bridleway|way::highway::living_street|way::cycleway::lane|way::cycleway:left::lane|way::cycleway:right::lane|way::highway::service|way::cycleway::share_busway|way::cycleway:left::share_busway|way::cycleway:right::share_busway|way::cycleway::shared_lane|way::cycleway:left::shared_lane|way::cycleway:right::shared_lane|relation::route::bicycle");
    private static final Pattern BUSY_ROADS_PATTERN = Pattern.compile("way::highway::residential|way::footway::crossing|way::highway::tertiary|way::highway::tertiary_link|way::highway::secondary|way::highway::secondary_link|way::highway::primary|way::highway::primary_link|node::highway::steps|node::crossing::island|node::crossing::uncontrolled|node::crossing::unmarked|node::crossing::yes|node::crossing::zebra|node::highway::crossing|node::highway::elevator|node::crossing::traffic_signals|node::highway::traffic_signals");
    private static final Pattern NEUTRAL_ROADS_PATTERN = Pattern.compile("way::bicycle::permissive|way::bicycle::yes|way::bicycle::dismount|way::smoothness::bad|way::smoothness::excellent|way::smoothness::horrible|way::smoothness::intermediate|way::smoothness::very_bad|way::surface::cobblestone|way::surface::compacted|way::surface::dirt|way::surface::grass|way::surface::gravel|way::surface::ground|way::surface::mud|way::surface::paving_stones|way::surface::sand|way::surface::setts|way::surface::unpaved|way::surface::wood|way::highway::steps");

    public QuietnessEdgeEvaluator(CycleEdge edge, ParametersOfRelevantTags evaluationParameters) {
        super(edge, evaluationParameters);
    }

    public QuietnessEdgeEvaluator(CycleEdge edge, ParametersOfRelevantTags evaluationParameters, boolean enableLog) {
        super(edge, evaluationParameters, enableLog);
    }
/**
{@inheritDoc}
 */
    @Override
    protected void evaluateEdgeWithoutAverageSpeed() {
        if (evaluated) {
            return;
        }

        addToLog("Evaluating edge:" + edge + "\n");
        addToLog("with tags: " + edge.getOSMtags() + "\n");

        PatternGroup quiet = new PatternGroup("quiet", QUIET_ROADS_PATTERN, PatternGroupRule.MAX, PatternGroupRule.MAX);
        PatternGroup busy = new PatternGroup("busy", BUSY_ROADS_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        PatternGroup neutral = new PatternGroup("neutral", NEUTRAL_ROADS_PATTERN, PatternGroupRule.MIN, PatternGroupRule.MAX);
        ArrayList<PatternGroup> patternGroupList = new ArrayList<>();

        patternGroupList.add(quiet);
        patternGroupList.add(busy);
        patternGroupList.add(neutral);

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

        edgeSpeedMultiplier = 1 * quiet.getMultiplier() * busy.getMultiplier();
        edgePenalisationInSeconds = 0 + busy.getConstant();
        edgeLengthMultiplied = edge.getLengthInMetres() / edgeSpeedMultiplier;
        addToLog("\nEdgeSpeedMultiplier: " + edgeSpeedMultiplier + ". EdgePenalisationInSeconds: " + edgePenalisationInSeconds);
        addToLog("\nEdge rises: " + edge.getRises() + ". Edge drops: " + edge.getDrops());
        addToLog("\nEdge length: " + edge.getLengthInMetres());
        evaluated = true;

    }
/**
{@inheritDoc}
 */
    @Override
    public double evaluateEdge(double averageSpeedMetersPerSecond) {
        evaluateEdgeWithoutAverageSpeed();
        return calculateEdgeTime(averageSpeedMetersPerSecond);
    }

    private double calculateEdgeTime(double averageSpeedMetersPerSecond) {
        edgeTime = edgeLengthMultiplied / averageSpeedMetersPerSecond + edgePenalisationInSeconds;
        addToLog("\nAvg speed: " + averageSpeedMetersPerSecond + ". Edge calculated speed: " + edgeSpeedMultiplier * averageSpeedMetersPerSecond);
        addToLog("\nEdgeTime: " + edgeTime);
        return edgeTime;
    }
}
