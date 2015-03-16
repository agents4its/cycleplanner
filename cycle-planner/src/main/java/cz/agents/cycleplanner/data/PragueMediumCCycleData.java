package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PragueMediumCCycleData extends CityCycleData {
	public PragueMediumCCycleData() {
		super();

		osmFile = new File("osm-data/prague-medium-C.osm");
		graphFileName = "prague-medium-C-cycleway-graph.javaobject";
		streetNamesFileName = "prague-street-names.javaobject";

		top = 50134200;
		left = 14466900;
		right = 14506100;
		bottom = 50102900;

		city = City.PRAGUE_MEDIUM_C;
	}
}
