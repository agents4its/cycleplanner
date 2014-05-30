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

import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;
import eu.superhub.wp5.plannerdataimporter.graphimporter.interfaces.OsmRelationSink;
import eu.superhub.wp5.plannerdataimporter.graphimporter.tasks.GraphImportTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.EntityType;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.RelationMember;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;

/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @param <TNode>
 * @param <TEdge> 
 */
public abstract class RelationalGraphImportTask
<TNode extends eu.superhub.wp5.graphcommon.graph.elements.Node, TEdge extends eu.superhub.wp5.graphcommon.graph.elements.Edge> 
implements OsmRelationSink <Graph> {

    private static final Pattern ONEWAY_KEY_PATTERN = Pattern.compile("oneway|junction|highway");
    private static final Pattern ONEWAY_VALUE_PATTERN = Pattern.compile("yes|true|1|roundabout|motorway");
    private static final Logger logger = Logger.getLogger(GraphImportTask.class);
    /**
     * Cache for nodes.
     */
    private final Map<Long, TNode> nodeCache = new HashMap<>();
    private final Map<TupletEdgeId, TEdge> edgeCache = new HashMap<>();
    private final GraphBuilder builder = new GraphBuilder();

    @Override
    public Graph sink(Iterator<Relation> relations, MemoryDataSet dataset) {
        ArrayList<Way> ways = new ArrayList<>();

        while (relations.hasNext()) {
            Relation relation = relations.next();
            for (RelationMember relationMember : relation.getMembers()) {
                if (relationMember.getMemberType() == EntityType.Way) {
                    Way way = dataset.getWaysByID(relationMember.getMemberId());
                    if (way != null) {
                        ways.add(way);
                    } else {
//                        logger.warn("Relation includes way with id " + relationMember.getMemberId()
//                                + ", which has not representation in OSM data");
                    }
                }
            }
        }


        return waySink(ways.iterator(), dataset);

    }

    public Graph<TNode, TEdge> waySink(Iterator<Way> ways, MemoryDataSet dataset) {

        while (ways.hasNext()) {
            Way way = ways.next();
            ArrayList<Node> nodes = new ArrayList<>();
            for (WayNode wayNode : way.getWayNodes()) {
                Node node = dataset.getNodeByID(wayNode.getNodeId());
                if (node != null) {
                    nodes.add(node);
                } else {
                    logger.warn("Way includes node with id " + wayNode.getNodeId()
                            + ", which has not representation in OSM data");
                }
            }

            //@todo
            //if (isOneWay(way) == false) {
            if (true) {
                ArrayList<Node> reverseNodes = new ArrayList<>();
                for (int i = nodes.size() - 1; i >= 0; i--) {
                    reverseNodes.add(nodes.get(i));
                }
                setEdgesToBuilder(builder, createEdgesFromOsm(reverseNodes, way.getTags()));
            }
            // vezme všetky nody v danej ceste a setne ich do buildera aj s ich tagmi
            setEdgesToBuilder(builder, createEdgesFromOsm(nodes, way.getTags()));
        }


        return builder.createGraph();
    }

    private void setEdgesToBuilder(GraphBuilder builder, List<TEdge> edges) {

        for (TEdge edge : edges) {
            builder.addEdge(edge);
        }


    }

    public List<TEdge> createEdgesFromOsm(ArrayList<Node> nodes, Collection<Tag> info) {

        assert nodes != null;
        assert nodes.size() > 1;

        List<TEdge> edges = new ArrayList<>();

        // vezme nodes od prvej po poslednu a pre kazde dve vytvori hranu
        for (int i = 0; i < nodes.size() - 1; i++) {


            Node fromNodeOsm = nodes.get(i);
            Node toNodeOsm = nodes.get(i + 1);

            RelationalGraphImportTask.TupletEdgeId tupletEdgeId = new RelationalGraphImportTask.TupletEdgeId(fromNodeOsm.getId(), toNodeOsm.getId());

            TEdge edge = edgeCache.get(tupletEdgeId);

            if (edge == null) {

                double comulativeLength = LatLon.distanceInMeters(new LatLon(fromNodeOsm.getLatitude(), fromNodeOsm.getLongitude()), new LatLon(toNodeOsm.getLatitude(), toNodeOsm.getLongitude()));

                TNode formNode = getFromCacheOrCreateNew(fromNodeOsm, fromNodeOsm.getTags());
                TNode toNode = getFromCacheOrCreateNew(toNodeOsm, toNodeOsm.getTags());

                edge = createEdge(formNode, toNode, comulativeLength,
                        info);

                edgeCache.put(tupletEdgeId, edge);
                edges.add(edge);

            }

        }

        return edges;

    }

    private TNode getFromCacheOrCreateNew(Node inputNodeOsm, Collection<Tag> info) {
        TNode node = nodeCache.get(inputNodeOsm.getId());
        if (node == null) {
            node = createNode(inputNodeOsm.getId(), new LatLon(inputNodeOsm.getLatitude(),
                    inputNodeOsm.getLongitude()), info);
            nodeCache.put(inputNodeOsm.getId(), node);
            builder.addNode(node);
        }

        return node;

    }

    private boolean isOneWay(Way way) {

        for (Tag tag : way.getTags()) {

            if (ONEWAY_KEY_PATTERN.matcher(tag.getKey()).matches()) {
                if (ONEWAY_VALUE_PATTERN.matcher(tag.getValue()).matches()) {
                    return true;
                }

            }
        }

        return false;
    }

    public abstract TNode createNode(final long id, final LatLon latLon,
            final Collection<Tag> info);

    public abstract TEdge createEdge(final TNode fromNode, final TNode toNode,
            final double cumulativeLength, Collection<Tag> info);

    private class TupletEdgeId {

        private final long from;
        private final long to;

        public TupletEdgeId(long from, long to) {
            super();
            this.from = from;
            this.to = to;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + (int) (from ^ (from >>> 32));
            result = prime * result + (int) (to ^ (to >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TupletEdgeId other = (TupletEdgeId) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (from != other.from) {
                return false;
            }
            if (to != other.to) {
                return false;
            }
            return true;
        }

        private RelationalGraphImportTask getOuterType() {
            return RelationalGraphImportTask.this;
        }
    }
}
