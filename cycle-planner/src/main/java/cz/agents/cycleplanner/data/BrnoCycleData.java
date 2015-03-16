package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class BrnoCycleData extends CityCycleData {

	public BrnoCycleData() {
		super();

		osmFile = new File("osm-data/brno-full.osm");
		graphFileName = "brno-cycleway-graph.javaobject";
		streetNamesFileName = "brno-street-names.javaobject";

		top = 49276800;
		left = 16420400;
		right = 16773400;
		bottom = 49082000;

		city = City.BRNO;
	}
}
