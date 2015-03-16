package cz.agents.cycleplanner.util;

import cz.agents.cycleplanner.dataStructures.CycleNode;

/**
 * 
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 * @author Qing Song <qing.song@agents.fel.cvut.cz>
 */
public final class AngleUtil {

	/**
	 * 
	 * @return angle in degrees
	 */
	public static double getAngle(CycleNode from, CycleNode center, CycleNode to) {

		double theta1 = computeAngle(from, center);
		double theta2 = computeAngle(to, center);

		double delta = normalizeAngle(theta2 - theta1);

		return Math.toDegrees(delta);
	}

	private static double computeAngle(CycleNode p1, CycleNode p2) {
		double angleFromXAxis = Math.atan2(
				p2.getProjectedLatitude() - p1.getProjectedLatitude(),
				p2.getProjectedLongitude() - p1.getProjectedLongitude());
		return angleFromXAxis;
	}

	private static double normalizeAngle(double angle) {
		return angle < 0 ? angle + 2 * Math.PI : angle;
	}
}
