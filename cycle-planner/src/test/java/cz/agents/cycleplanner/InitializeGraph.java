package cz.agents.cycleplanner;

import org.apache.log4j.Logger;

public class InitializeGraph {
	private final static Logger log = Logger.getLogger(InitializeGraph.class);


	public static void main(String[] args) {

		// createTestGraph();
		// createGraph();
	}

	// public static void createGraph() {
	// System.out.println("************* Initializing *************");
	//
	// try {
	// File osm;
	//
	// osm =
	// ResourceToFile.getFileFromResource(PragueOSMDataLoader.getOSMDataStream());
	//
	// graphProvider = new HighwayGraphProvider(osm);
	// graphProvider.setDestroyTags(false);
	// graphProvider.recreateGraphFromOSM();
	//
	// graphProvider.getGraph();
	//
	// System.gc();
	// } catch (IOException e) {
	// logger.error(e.getMessage(), e.fillInStackTrace());
	// }
	// }
	//
	// public static void createTestGraph() {
	// System.out.println("************* Initializing *************");
	//
	// try {
	// File osm;
	//
	// osm =
	// ResourceToFile.getFileFromResource(PragueOSMDataLoader.getTestOSMDataStream());
	//
	// graphProvider = new HighwayGraphProvider(osm);
	// graphProvider.setDestroyTags(false);
	// graphProvider.recreateGraphFromOSM();
	//
	// graphProvider.getGraph();
	//
	// System.gc();
	// } catch (IOException e) {
	// logger.error(e.getMessage(), e.fillInStackTrace());
	// }
	// }

}
