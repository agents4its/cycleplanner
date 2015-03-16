package cz.agents.cycleplanner.util;

import cz.agents.cycleplanner.api.datamodel.BoundingBox;
import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.routingService.City;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;
import eu.superhub.wp5.wp5common.location.GPSLocation;

/**
 * TODO javadoc
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class CityBoundingBoxDiagonalDistanceCalculator {

	/**
	 * For given city calculates maximal possible distance between two points.
	 * It takes city's bounding box and computes distance between the top left
	 * corner and the right bottom corner.
	 * 
	 * @param city
	 * @return maximal distance between two points in city
	 */
	public static double calculate(City city) {
		CityCycleData cityCycleData = CityCycleData.getDataForCity(city);
		BoundingBox boundingBox = cityCycleData.getBoundingBox();

		double distance = EdgeUtil.computeDirectDistanceInM(
				new GPSLocation(boundingBox.getTopE6(), boundingBox.getLeftE6()),
				new GPSLocation(boundingBox.getBottomE6(), boundingBox.getRightE6()));

		return distance;
	}
}
