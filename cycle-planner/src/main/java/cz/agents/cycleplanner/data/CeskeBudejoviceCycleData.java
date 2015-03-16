package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class CeskeBudejoviceCycleData extends CityCycleData {

	public CeskeBudejoviceCycleData() {
		super();

		osmFile = new File("osm-data/ceskebudejovice-full.osm");
		graphFileName = "ceske-budejovice-cycleway-graph.javaobject";
		streetNamesFileName = "ceske-budejovice-street-names.javaobject";

		top = 49168700;
		left = 14217700;
		right = 14606300;
		bottom = 48892300;

		city = City.CESKE_BUDEJOVICE;
	}
}
