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
package cvut.fel.nemetma1.plannerDataImporterExtensions.osmBinder;

import cvut.fel.nemetma1.plannerDataImporterExtensions.RelationAndWaySelector;
import eu.superhub.wp5.planner.planningstructure.PermittedMode;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.AtLeastOneIncludedTagSatisfiedEvaluator;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.TagConditionsEvaluator;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.graph.PermittedModeEvaluator;
import java.util.HashSet;
import java.util.Set;
import org.openstreetmap.osm.data.Selector;

/**
 *
 * @author Marcel
 */
public class RelationAndWayBicycleGraphOsmBinder {

    public RelationAndWayBicycleGraphOsmBinder() {
    }
    private static Selector selector;
    private static PermittedModeEvaluator permittedModeEvaluator;

    static {
        Set<String> includeRelations = new HashSet<>();
        Set<String> excludeRelations = new HashSet<>();

        includeRelations.add(TagConditionsEvaluator.createConditionTag("route", "bicycle"));

        AtLeastOneIncludedTagSatisfiedEvaluator evaluatorRelations = new AtLeastOneIncludedTagSatisfiedEvaluator(includeRelations,
                excludeRelations);

        Set<String> includeWays = new HashSet<>();
        Set<String> excludeWays = new HashSet<>();


        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "primary"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "primary_link"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "secondary"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "secondary_link"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "tertiary"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "tertiary_link"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "residential"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "unclassified"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "road"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "living_street"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "service"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "track"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "services"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "rest_area"));

        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "path"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "cycleway"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "bridleway"));

        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "footway"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "steps"));
        includeWays.add(TagConditionsEvaluator.createConditionTag("highway", "pedestrian"));

        excludeWays.add(TagConditionsEvaluator.createConditionTag("bicycle", "no"));
        excludeWays.add(TagConditionsEvaluator.createConditionTag("access", "customers"));
        excludeWays.add(TagConditionsEvaluator.createConditionTag("access", "delivery"));
        excludeWays.add(TagConditionsEvaluator.createConditionTag("access", "private"));
        excludeWays.add(TagConditionsEvaluator.createConditionTag("access", "no"));
        AtLeastOneIncludedTagSatisfiedEvaluator evaluatorWays = new AtLeastOneIncludedTagSatisfiedEvaluator(includeWays,excludeWays);

        selector = new RelationAndWaySelector(evaluatorRelations, evaluatorWays);

        Set<PermittedMode> modes = new HashSet<>();
        modes.add(PermittedMode.BIKE);

        permittedModeEvaluator = new PermittedModeEvaluator(modes, evaluatorWays);
    }

    public static Selector getSelector() {
        return selector;

    }

    public static PermittedModeEvaluator getPermittedModeEvaluator() {
        return permittedModeEvaluator;
    }
}
