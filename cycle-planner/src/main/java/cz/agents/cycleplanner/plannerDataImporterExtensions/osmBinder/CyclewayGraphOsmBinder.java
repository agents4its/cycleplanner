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
import eu.superhub.wp5.plannerdataimporter.graphimporter.selector.WaySelector;

public class CyclewayGraphOsmBinder {

	private CyclewayGraphOsmBinder() {
	}

	private static Selector selector;
	private static PermittedModeEvaluator permittedModeEvaluator;

	static {

		Set<String> include = new HashSet<String>();
		Set<String> exclude = new HashSet<String>();

		include.add(TagConditionsEvaluator.createConditionTag("highway", "primary"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "primary_link"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "secondary"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "secondary_link"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "tertiary"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "tertiary_link"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "residential"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "unclassified"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "road"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "living_street"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "service"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "track"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "services"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "rest_area"));

		include.add(TagConditionsEvaluator.createConditionTag("highway", "path"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "cycleway"));
		include.add(TagConditionsEvaluator.createConditionTag("highway", "bridleway"));

		include.add(TagConditionsEvaluator.createConditionTag("cycleway", "*"));

		AtLeastOneIncludedTagSatisfiedEvaluator evaluator = new AtLeastOneIncludedTagSatisfiedEvaluator(include,
				exclude);

		selector = new WaySelector(evaluator);

		Set<PermittedMode> modes = new HashSet<PermittedMode>();
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
