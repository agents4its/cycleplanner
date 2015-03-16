package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PlzenCycleData extends CityCycleData {

	public PlzenCycleData() {
		super();

		osmFile = new File("osm-data/plzen-full.osm");
		graphFileName = "plzen-cycleway-graph.javaobject";
		streetNamesFileName = "plzen-street-names.javaobject";

		top = 49800300;
		left = 13269400;
		right = 13518000;
		bottom = 49678700;

		city = City.PLZEN;
	}
}
