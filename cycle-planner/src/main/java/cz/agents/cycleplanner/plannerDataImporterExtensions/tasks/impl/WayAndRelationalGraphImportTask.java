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
package cz.agents.cycleplanner.plannerDataImporterExtensions.tasks.impl;

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

import cz.agents.cycleplanner.plannerDataImporterExtensions.RelationAndWayOsmSink;
import eu.superhub.wp5.graphcommon.graph.Graph;
import eu.superhub.wp5.graphcommon.graph.GraphBuilder;

/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 * 
 */
public abstract class WayAndRelationalGraphImportTask<TNode extends eu.superhub.wp5.graphcommon.graph.elements.Node, TEdge extends eu.superhub.wp5.graphcommon.graph.elements.Edge>
		implements RelationAndWayOsmSink<Graph> {

	private static final Pattern ONEWAY_KEY_PATTERN = Pattern
			.compile("oneway|junction|highway");
	private static final Pattern ONEWAY_VALUE_PATTERN = Pattern
			.compile("yes|true|1|roundabout|motorway");
	private static final Pattern REVERSE_ONEWAY_VALUE_PATTERN = Pattern
			.compile("-1|reverse");
	private static final Pattern NOT_ONEWAY_FOR_CYCLEWAY_KEY_PATTERN = Pattern
			.compile("cycleway|cycleway_left|cycleway_right");
	private static final Pattern NOT_ONEWAY_FOR_CYCLEWAY_VALUE_PATTERN = Pattern
			.compile("opposite|opposite_lane|opposite_track");
	private static final Pattern NOT_ONEWAY_FOR_BICYCLE_KEY_PATTERN = Pattern
			.compile("oneway:bicycle");
	private static final Pattern NOT_ONEWAY_FOR_BICYCLE_VALUE_PATTERN = Pattern
			.compile("no");

	private static final Logger log = Logger
			.getLogger(WayAndRelationalGraphImportTask.class);
	/**
	 * Cache for nodes.
	 */
	private final Map<Long, TNode> nodeCache = new HashMap<>();
	private final Map<TupletEdgeId, TEdge> edgeCache = new HashMap<>();

	private final GraphBuilder builder = new GraphBuilder();

	/**
	 * creates a graph from provided relations, ways and an OSM dataset
	 * 
	 * @param relations
	 *            relations that should be included into graph
	 * @param ways
	 *            ways that should be included into graph
	 * @param dataset
	 *            OSM dataset
	 * @return graph containing edges and nodes from specified ways and
	 *         relations
	 */
	@Override
	public Graph<TNode, TEdge> sink(Iterator<Relation> relations,
			Iterator<Way> ways, MemoryDataSet dataset) {
		
		ArrayList<Way> waysList;
		while (relations.hasNext()) {
			waysList = new ArrayList<>();
			Relation relation = relations.next();
			for (RelationMember relationMember : relation.getMembers()) {
				if (relationMember.getMemberType() == EntityType.Way) {
					Way way = dataset.getWaysByID(relationMember.getMemberId());
					if (way != null) {
						waysList.add(way);
					}
				}
			}

			// put edges from ways that are contained by this relation into
			// builder

			waySink(waysList.iterator(), dataset, relation.getTags());
		}

		// put ways that were provided as argument into builder
		waySink(ways, dataset, null);
		
		return builder.createGraph();
	}

	private void waySink(Iterator<Way> ways, MemoryDataSet dataset,
			Collection<Tag> relationInfo) {

		while (ways.hasNext()) {
			Way way = ways.next();
			ArrayList<Node> nodes = new ArrayList<>();
			for (WayNode wayNode : way.getWayNodes()) {
				Node node = dataset.getNodeByID(wayNode.getNodeId());
				if (node != null) {
					nodes.add(node);
				} else {
					log.warn("Way includes node with id "
							+ wayNode.getNodeId()
							+ ", which has not representation in OSM data");
				}
			}
			if (nodes.size() >= 2) {

				if (!isReverseOneWayForBicycles(way)) {
					setEdgesToBuilder(
							builder,
							createEdgesFromOsm(nodes, way.getTags(),
									relationInfo, way.getId()));
				}

				if (!isOneWayForBicycles(way)) {
					ArrayList<Node> reverseNodes = new ArrayList<>();
					for (int i = nodes.size() - 1; i >= 0; i--) {
						reverseNodes.add(nodes.get(i));
					}
					setEdgesToBuilder(
							builder,
							createEdgesFromOsm(reverseNodes, way.getTags(),
									relationInfo, way.getId()));
				}

				// takes all the nodes in the way and puts them into builder
				// together with edges
			} else {
				Iterator<Node> i = nodes.iterator();
				log.info("Found way that has less than two nodes: " + way);
				while (i.hasNext()) {
					Node n = i.next();
				}
			}
		}

	}

	private void setEdgesToBuilder(GraphBuilder builder, List<TEdge> edges) {
		for (TEdge edge : edges) {
			builder.addEdge(edge);
		}
	}

	public List<TEdge> createEdgesFromOsm(ArrayList<Node> nodes,
			Collection<Tag> wayInfo, Collection<Tag> relationInfo, long wayId) {
		assert nodes != null;
		assert nodes.size() > 1;
		List<TEdge> edges = new ArrayList<>();
		// takes nodes from the first to last and for each pair an edge is
		// created
		for (int i = 0; i < nodes.size() - 1; i++) {
			Node fromNodeOsm = nodes.get(i);
			Node toNodeOsm = nodes.get(i + 1);

			TupletEdgeId tupletEdgeId = new TupletEdgeId(fromNodeOsm.getId(),
					toNodeOsm.getId());

			TEdge edge = edgeCache.get(tupletEdgeId);

			if (edge == null) {
				double comulativeLength = LatLon.distanceInMeters(
						new LatLon(fromNodeOsm.getLatitude(), fromNodeOsm
								.getLongitude()),
						new LatLon(toNodeOsm.getLatitude(), toNodeOsm
								.getLongitude()));

				TNode fromNode = getFromCacheOrCreateNew(fromNodeOsm,
						fromNodeOsm.getTags());
				TNode toNode = getFromCacheOrCreateNew(toNodeOsm,
						toNodeOsm.getTags());

				edge = createEdge(fromNode, toNode, comulativeLength, wayInfo,
						relationInfo, wayId);

				edgeCache.put(tupletEdgeId, edge);
				edges.add(edge);
			}
		}
		return edges;
	}

	private TNode getFromCacheOrCreateNew(Node inputNodeOsm,
			Collection<Tag> info) {
		TNode node = nodeCache.get(inputNodeOsm.getId());
		if (node == null) {
			node = createNode(
					inputNodeOsm.getId(),
					new LatLon(inputNodeOsm.getLatitude(), inputNodeOsm
							.getLongitude()), info);
			nodeCache.put(inputNodeOsm.getId(), node);
			builder.addNode(node);
		}
		return node;
	}

	private boolean isOneWayForBicycles(Way way) {
		for (Tag tag : way.getTags()) {
			if (ONEWAY_KEY_PATTERN.matcher(tag.getKey()).matches()) {
				if (ONEWAY_VALUE_PATTERN.matcher(tag.getValue()).matches()) {
					// if it says oneway, check for exception for bicycles
					for (Tag tag2 : way.getTags()) {
						if (NOT_ONEWAY_FOR_CYCLEWAY_KEY_PATTERN.matcher(
								tag2.getKey()).matches()) {
							if (NOT_ONEWAY_FOR_CYCLEWAY_VALUE_PATTERN.matcher(
									tag2.getValue()).matches()) {
								return false;
							}
						}
					}
					for (Tag tag3 : way.getTags()) {
						if (NOT_ONEWAY_FOR_BICYCLE_KEY_PATTERN.matcher(
								tag3.getKey()).matches()) {
							if (NOT_ONEWAY_FOR_BICYCLE_VALUE_PATTERN.matcher(
									tag3.getValue()).matches()) {
								return false;
							}
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	private boolean isReverseOneWayForBicycles(Way way) {

		for (Tag tag : way.getTags()) {
			if (ONEWAY_KEY_PATTERN.matcher(tag.getKey()).matches()) {
				if (REVERSE_ONEWAY_VALUE_PATTERN.matcher(tag.getValue())
						.matches()) {
					// if it says oneway, check for exception for bicycles
					for (Tag tag2 : way.getTags()) {
						if (NOT_ONEWAY_FOR_CYCLEWAY_KEY_PATTERN.matcher(
								tag2.getKey()).matches()) {
							if (NOT_ONEWAY_FOR_CYCLEWAY_VALUE_PATTERN.matcher(
									tag2.getValue()).matches()) {
								return false;
							}
						}
					}

					for (Tag tag3 : way.getTags()) {
						if (NOT_ONEWAY_FOR_BICYCLE_KEY_PATTERN.matcher(
								tag3.getKey()).matches()) {
							if (NOT_ONEWAY_FOR_BICYCLE_VALUE_PATTERN.matcher(
									tag3.getValue()).matches()) {
								return false;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public abstract TNode createNode(final long id, final LatLon latLon,
			final Collection<Tag> info);

	public abstract TEdge createEdge(final TNode fromNode, final TNode toNode,
			final double cumulativeLength, Collection<Tag> wayInfo,
			Collection<Tag> relationInfo, long wayId);

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

		private WayAndRelationalGraphImportTask getOuterType() {
			return WayAndRelationalGraphImportTask.this;
		}
	}
}
