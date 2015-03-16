package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PragueMediumACycleData extends CityCycleData {
	public PragueMediumACycleData() {
		super();

		osmFile = new File("osm-data/prague-medium-A.osm");
		graphFileName = "prague-medium-A-cycleway-graph.javaobject";
		streetNamesFileName = "prague-street-names.javaobject";

		top = 50090750;
		left = 14416200;
		right = 14470270;
		bottom = 50069990;

		city = City.PRAGUE_MEDIUM_A;
	}
}
