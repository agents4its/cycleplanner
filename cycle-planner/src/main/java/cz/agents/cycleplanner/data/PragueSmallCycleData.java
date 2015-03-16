package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PragueSmallCycleData extends CityCycleData {
	public PragueSmallCycleData() {
		super();

		osmFile = new File("osm-data/prague-small.osm");
		graphFileName = "prague-small-cycleway-graph.javaobject";
		streetNamesFileName = "prague-street-names.javaobject";

		top = 50082030;
		left = 14437490;
		right = 14456970;
		bottom = 50074760;

		city = City.PRAGUE_SMALL;
	}
}
