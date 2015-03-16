package cz.agents.cycleplanner.json;

import java.util.Collection;

import cz.agents.cycleplanner.dataStructures.CycleEdge;

public class GeoJSONRouteCreator {
	/**
	 * creates a string in GeoJson format that contains the path and in
	 * properties, it contains an array of altitudes of nodes in the path, the
	 * time of the journey , and the elevation gain
	 * 
	 * @param edges
	 *            edges of the path
	 * @param averageSpeedKMpS
	 *            average speed of a cyclist
	 * @return string in GeoJson format
	 * @throws JSONException
	 */
	@Deprecated
	public static String createJsonPath(Collection<CycleEdge> edges, double averageSpeedKMpS) {
		// double oneOverAverageSpeedMetersPerSecond = 3.6 / averageSpeedKMpS;
		// double length = 0;
		// double time = 0;
		// double rises = 0;
		// double drops = 0;
		// JSONObject feature = new JSONObject();
		// feature.put("type", "Feature");
		// JSONObject geometry = new JSONObject();
		// geometry.put("type", "LineString");
		// JSONArray coordinates = new JSONArray();
		// JSONArray elevationProfile = new JSONArray();
		// elevationProfile.put(0);
		// for (Iterator<CycleEdge> it = edges.iterator(); it.hasNext();) {
		// CycleEdge edge = it.next();
		// JSONArray coordinate = new JSONArray();
		// coordinate.put(edge.getFromNode().getLongitude());
		// coordinate.put(edge.getFromNode().getLatitude());
		// elevationProfile.put(edge.getFromNode().getElevation());
		// elevationProfile.put(edge.getLengthInMetres());
		// coordinates.put(coordinate);
		// if (!it.hasNext()) {
		// coordinate = new JSONArray();
		// coordinate.put(edge.getToNode().getLongitude());
		// coordinate.put(edge.getToNode().getLatitude());
		// elevationProfile.put(edge.getFromNode().getElevation());
		// coordinates.put(coordinate);
		// }
		// length += edge.getLengthInMetres();
		// time += TravelTimeCriterion.evaluateWithSpeed(edge,
		// oneOverAverageSpeedMetersPerSecond);
		// rises += edge.getRises();
		// drops += edge.getDrops();
		// }
		// geometry.put("coordinates", coordinates);
		// feature.put("geometry", geometry);
		// JSONObject properties = new JSONObject();
		//
		// properties.put("total_length", length);
		// properties.put("total_time", time);
		// properties.put("total_elevationGain", rises);
		// properties.put("total_elevationDrop", drops);
		// properties.put("elevationProfile", elevationProfile);
		// System.out.println("total_length " + length);
		// System.out.println("total_time " + time);
		// System.out.println("total_elevationGain " + rises);
		// System.out.println("total_elevationDrop " + drops);
		// feature.put("properties", properties);
		//
		// return feature.toString();
		return "";
	}
}
