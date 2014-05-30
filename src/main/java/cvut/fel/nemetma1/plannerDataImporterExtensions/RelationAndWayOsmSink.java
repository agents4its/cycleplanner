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

import java.util.Iterator;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @param <TOutput> 
 */
public interface RelationAndWayOsmSink<TOutput> {
        public TOutput sink(Iterator<Relation> relations, Iterator<Way> ways, MemoryDataSet dataset);

    
}
