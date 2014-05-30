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

import eu.superhub.wp5.plannerdataimporter.graphimporter.OsmDataGetter;
import eu.superhub.wp5.plannerdataimporter.graphimporter.OsmImporter;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.Bounds;

/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class RelationAndWayOsmImporter extends OsmImporter {

    private final OsmDataGetter osmDataGetter;

    public RelationAndWayOsmImporter(OsmDataGetter osmDataGetter) {
        super(osmDataGetter);
        this.osmDataGetter = osmDataGetter;
    }

    private MemoryDataSet filter(final Selector selector) {
        return osmDataGetter.filter(selector);
    }

    public <TOutput> TOutput executeTaskForWayAndRelation(RelationAndWayOsmSink<TOutput> sink, Selector selector) {
        return sink.sink(filter(selector).getRelations(Bounds.WORLD), filter(selector).getWays(Bounds.WORLD), osmDataGetter.getAllOsmData());
    }
}
