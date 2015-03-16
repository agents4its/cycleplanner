package cz.agents.cycleplanner.cyclewaygraphs;

import java.io.InputStream;

public class CyclewayGraphLoader {

	public static InputStream getCyclewayGraphStream(String fileName) {
		return CyclewayGraphLoader.class.getResourceAsStream("/" + fileName);
	}
}
