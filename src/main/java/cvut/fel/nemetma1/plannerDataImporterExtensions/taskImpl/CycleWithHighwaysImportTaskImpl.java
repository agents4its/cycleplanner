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
package cvut.fel.nemetma1.plannerDataImporterExtensions.taskImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cycle.planner.util.EPSGProjection;
import eu.superhub.wp5.plannerdataimporter.graphimporter.evaluator.graph.PermittedModeEvaluator;
import eu.superhub.wp5.wp5common.GPSLocation;

/**
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class CycleWithHighwaysImportTaskImpl extends WayAndRelationalGraphImportTask<CycleNode, CycleEdge> {

	private static final Pattern RELATION_KEYS_TO_SAVE = Pattern.compile("route");
	private static final Pattern WAY_KEYS_TO_SAVE = Pattern
			.compile("access|bicycle|cycleway|cycleway:left|cycleway:right|footway|highway|smoothness|surface");
	private static final Pattern NODE_KEYS_TO_SAVE = Pattern.compile("crossing|highway");
	private static EPSGProjection projection;

	public CycleWithHighwaysImportTaskImpl(double defaultAllowedMaxSpeedInKmph,
			List<PermittedModeEvaluator> permittedModeEvaluators) {
		super();

		// TODO make better
		try {
			if (projection == null) {
				projection = new EPSGProjection(2065);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public CycleNode createNode(long id, LatLon latLon, Collection<Tag> info) {
		float elevation = 0;
		Iterator<Tag> it = info.iterator();
		HashSet<Tag> relevantTags = new HashSet<>();
		while (it.hasNext()) {
			Tag tag = it.next();
			if (tag.getKey().equals("height")) {
				elevation = Float.parseFloat(tag.getValue());
				break;
			}
			if (NODE_KEYS_TO_SAVE.matcher(tag.getKey()).matches()) {
				relevantTags.add(tag);
			}
		}

		return new CycleNode(id, projection.getProjectedGPSLocation(new GPSLocation(latLon.getXCoord(), latLon
				.getYCoord())), "", elevation, relevantTags);
		// return new CycleNode(id, new GPSLocation(latLon.getXCoord(), latLon.getYCoord()), "", elevation,
		// relevantTags);
	}

	@Override
	public CycleEdge createEdge(CycleNode fromNode, CycleNode toNode, double cumulativeLength, Collection<Tag> wayInfo,
			Collection<Tag> relationInfo) {
		float dif = toNode.getElevation() - fromNode.getElevation();
		float rises = (dif > 0) ? dif : 0;
		float drops = (dif < 0) ? dif : 0;
		HashSet<Tag> relevantWayTags = new HashSet<>();
		if (wayInfo != null) {
			Iterator<Tag> it = wayInfo.iterator();
			while (it.hasNext()) {
				Tag tag = it.next();
				if (WAY_KEYS_TO_SAVE.matcher(tag.getKey()).matches()) {
					relevantWayTags.add(tag);
				}
			}
		}
		HashSet<Tag> relevantRelationTags = new HashSet<>();
		if (relationInfo != null) {
			Iterator<Tag> itRel = relationInfo.iterator();
			while (itRel.hasNext()) {
				Tag tag = itRel.next();
				if (RELATION_KEYS_TO_SAVE.matcher(tag.getKey()).matches()) {
					relevantRelationTags.add(tag);
				}
			}
		}
		return new CycleEdge(fromNode, toNode, cumulativeLength, rises, drops, relevantWayTags, relevantRelationTags);
	}

}
