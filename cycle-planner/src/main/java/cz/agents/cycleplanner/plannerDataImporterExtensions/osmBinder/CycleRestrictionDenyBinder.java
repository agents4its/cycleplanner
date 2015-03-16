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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.superhub.wp5.plannercore.structures.base.PermittedMode;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.graph.RestrictionDenyEvaluator;

public class CycleRestrictionDenyBinder {

	private CycleRestrictionDenyBinder() {
	}

	private static final Set<String> denyValues = new HashSet<>();
	private static RestrictionDenyEvaluator RESTRICTION_DENY_EVALUATOR;

	
	static {
		denyValues.add("customers");
		denyValues.add("delivery");
		denyValues.add("no");
		denyValues.add("private");
		
		Map<String, Set<PermittedMode>> permittedModes = new HashMap<>();
		permittedModes.put("bicycle", createSet(PermittedMode.BIKE));

		RESTRICTION_DENY_EVALUATOR = new RestrictionDenyEvaluator(permittedModes, denyValues);

	}

	public static RestrictionDenyEvaluator getRestrictionDenyEvaluator() {
		return RESTRICTION_DENY_EVALUATOR;
	}
	
	private static Set<PermittedMode> createSet(PermittedMode... permittedModes) {
		Set<PermittedMode> denySet = new HashSet<>();
            denySet.addAll(Arrays.asList(permittedModes));
		return denySet;
	}

}
