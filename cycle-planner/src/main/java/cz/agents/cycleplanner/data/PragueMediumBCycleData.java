package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PragueMediumBCycleData extends CityCycleData {
	public PragueMediumBCycleData() {
		super();

		osmFile = new File("osm-data/prague-medium-B.osm");
		graphFileName = "prague-medium-B-cycleway-graph.javaobject";
		streetNamesFileName = "prague-street-names.javaobject";

		top = 50091300;
		left = 14355400;
		right = 14409500;
		bottom = 50070500;

		city = City.PRAGUE_MEDIUM_B;
	}
}
