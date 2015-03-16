package cz.agents.cycleplanner.routingService;

import java.io.Serializable;
import java.util.Map;

public class StreetNames implements Serializable {

	private static final long serialVersionUID = -4517376979614653778L;

	private Map<Long, String> streetNames;

	public StreetNames(Map<Long, String> streetNames) {
		this.streetNames = streetNames;
	}

	public String getStreetName(long wayId) {
		return streetNames.get(wayId);
	}
}	
