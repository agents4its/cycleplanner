package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.routingService.City;

public class HradecKraloveCycleData extends CityCycleData {

	public HradecKraloveCycleData() {
		super();

		osmFile = new File("osm-data/hradeckralove-full.osm");
		graphFileName = "hradec-kralove-cycleway-graph.javaobject";
		streetNamesFileName = "hradec-kralove-street-names.javaobject";

		top = 50276800;
		left = 15716300;
		right = 15962400;
		bottom = 50147900;

		city = City.HRADEC_KRALOVE;
	}
}
