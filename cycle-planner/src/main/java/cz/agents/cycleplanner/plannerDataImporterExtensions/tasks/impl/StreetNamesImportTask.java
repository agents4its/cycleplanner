package cz.agents.cycleplanner.plannerDataImporterExtensions.tasks.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;

import cz.agents.cycleplanner.plannerDataImporterExtensions.RelationAndWayOsmSink;

public class StreetNamesImportTask implements RelationAndWayOsmSink<Map<Long, String>> {

	private static final Pattern STREET_NAME_OSM_KEY = Pattern.compile("name");

	@Override
	public Map<Long, String> sink(Iterator<Relation> relations, Iterator<Way> ways, MemoryDataSet dataset) {
		Map<Long, String> names = new HashMap<Long, String>();

		while (ways.hasNext()) {
			Way way = ways.next();
			for (Tag tag : way.getTags()) {
				if (STREET_NAME_OSM_KEY.matcher(tag.getKey()).matches()) {
					names.put(way.getId(), tag.getValue());
				}
			}

		}

		return names;
	}

}
