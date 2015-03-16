package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class PragueCycleData extends CityCycleData {

	public PragueCycleData() {
		super();

		osmFile = new File("osm-data/prague-full.osm");
		graphFileName = "prague-cycleway-graph.javaobject";
		streetNamesFileName = "prague-street-names.javaobject";

		top = 50209400;
		left = 14084500;
		right = 14787600;
		bottom = 49920300;

		city = City.PRAGUE;
	}
}
