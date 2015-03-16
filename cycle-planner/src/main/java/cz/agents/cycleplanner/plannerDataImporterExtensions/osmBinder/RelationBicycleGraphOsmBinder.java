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
package cz.agents.cycleplanner.plannerDataImporterExtensions.osmBinder;

import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.osm.data.Selector;

import eu.superhub.wp5.plannercore.structures.base.PermittedMode;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.AtLeastOneIncludedTagSatisfiedEvaluator;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.TagConditionsEvaluator;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.graph.PermittedModeEvaluator;
import eu.superhub.wp5.plannerdataimporter.graphimporter.selector.RelationSelector;

/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class RelationBicycleGraphOsmBinder {

    public RelationBicycleGraphOsmBinder() {
    }
    private static Selector selector;
    private static PermittedModeEvaluator permittedModeEvaluator;

    static {
        Set<String> include = new HashSet<>();
        Set<String> exclude = new HashSet<>();

        include.add(TagConditionsEvaluator.createConditionTag("route", "bicycle"));

        AtLeastOneIncludedTagSatisfiedEvaluator evaluator = new AtLeastOneIncludedTagSatisfiedEvaluator(include,
                exclude);
        

        selector = new RelationSelector(evaluator);

        Set<PermittedMode> modes = new HashSet<>();
        modes.add(PermittedMode.BIKE);

        permittedModeEvaluator = new PermittedModeEvaluator(modes, evaluator);
    }

    public static Selector getSelector() {
        return selector;

    }

    public static PermittedModeEvaluator getPermittedModeEvaluator() {
        return permittedModeEvaluator;
    }
}
