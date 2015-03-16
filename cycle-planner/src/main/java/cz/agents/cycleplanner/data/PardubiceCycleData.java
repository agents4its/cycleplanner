package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PardubiceCycleData extends CityCycleData {

	public PardubiceCycleData() {
		super();

		osmFile = new File("osm-data/pardubice-full.osm");
		graphFileName = "pardubice-cycleway-graph.javaobject";
		streetNamesFileName = "pardubice-street-names.javaobject";

		top = 50087500;
		left = 15657600;
		right = 15881400;
		bottom = 49982600;

		city = City.PARDUBICE;
	}
}
