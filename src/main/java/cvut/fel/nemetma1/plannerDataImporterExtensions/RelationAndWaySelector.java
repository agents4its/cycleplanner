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
package cvut.fel.nemetma1.plannerDataImporterExtensions;

import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.TagConditionsEvaluator;
import eu.superhub.wp5.plannerdataimporter.graphimporter.selector.OsmSelector;
import org.openstreetmap.osm.data.IDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class RelationAndWaySelector implements Selector {

    TagConditionsEvaluator tagConditionsEvaluatorForWays;
    TagConditionsEvaluator tagConditionsEvaluatorForRelations;

    public RelationAndWaySelector(TagConditionsEvaluator tagConditionsEvaluatorForRelations, TagConditionsEvaluator tagConditionsEvaluatorForWays) {
        this.tagConditionsEvaluatorForWays = tagConditionsEvaluatorForWays;
        this.tagConditionsEvaluatorForRelations = tagConditionsEvaluatorForRelations;
    }



    @Override
    public boolean isAllowed(IDataSet arg0, Node arg1) {
        return false;
    }

    @Override
    public boolean isAllowed(IDataSet arg0, Way arg1) {
        return tagConditionsEvaluatorForWays.evaluateTagConditions(arg1.getTags());
    }

    @Override
    public boolean isAllowed(IDataSet arg0, Relation arg1) {
        return tagConditionsEvaluatorForRelations.evaluateTagConditions(arg1.getTags());
    }
}
