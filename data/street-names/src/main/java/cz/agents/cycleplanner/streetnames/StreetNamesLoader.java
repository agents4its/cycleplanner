package cz.agents.cycleplanner.streetnames;

import java.io.InputStream;

public class StreetNamesLoader {

	public static InputStream getStreetNamesStream(String fileName) {
		return StreetNamesLoader.class.getResourceAsStream("/" + fileName);
	}
}
