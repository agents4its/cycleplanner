package cz.agents.cycleplanner.junctions;

import org.apache.log4j.Logger;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class JunctionCoordinatesUtil {

	private final static Logger log = Logger
			.getLogger(JunctionCoordinatesUtil.class);

	public final static double RADIUS = 1d;
	public final static double NEW_NODE_ANGLE = Math.toRadians(10d);
	private final static double COS = Math.cos(NEW_NODE_ANGLE);
	private final static double SIN = Math.sin(NEW_NODE_ANGLE);
	private static EPSGProjection projection;

	static {
		try {
			projection = new EPSGProjection(2065);
		} catch (FactoryException | TransformException e) {
			log.error(e.getMessage(), e.fillInStackTrace());
		}
	}

	/**
	 * x3=x1+((x2-x1)*r*cosθ)/l+((y2-y1)*r*sinθ)/l
	 * y3=y1+((y2-y1)*r*cosθ)/l-((x2-x1)*r*sinθ)/l
	 * 
	 * @param junction
	 * @param node
	 * @return
	 */
	public static GPSLocation deriveCoordinatesJunctionOutcomingNode(
			CycleNode junction, CycleNode node) {
		
		double length = EdgeUtil.computeDirectDistanceInM(junction, node);

		double xJunction = junction.getProjectedLongitude();
		double yJunction = junction.getProjectedLatitude();

		double xNode = node.getProjectedLongitude();
		double yNode = node.getProjectedLatitude();

		double x = xJunction + ((xNode - xJunction) * RADIUS * COS) / length
				+ ((yNode - yJunction) * RADIUS * SIN) / length;
		double y = yJunction + ((yNode - yJunction) * RADIUS * COS) / length
				- ((xNode - xJunction) * RADIUS * SIN) / length;
		
		// log.debug("Junction: " + xJunction + " " + yJunction);
		// log.debug("Junction: " + junction.getLongitudeE6() + " " +
		// junction.getLatitudeE6());
		// log.debug("Old outcoming node: " + xNode + " " + yNode);
		// log.debug("Old outcoming node: " + node.getLongitudeE6() + " " +
		// node.getLatitudeE6());
		// log.debug("New outcomming node: " + x + " " + y);

		return projection.getSphericalGPSLocation(new GPSLocation(y, x, y,
				x, junction.getElevation()));
	}

	/**
	 * x3=x1+((x2-x1)*r*cosθ)/l-((y2-y1)*r*sinθ)/l
	 * y3=y1+((y2-y1)*r*cosθ)/l+((x2-x1)*r*sinθ)/l
	 * 
	 * @param junction
	 * @param node
	 * @return
	 */
	public static GPSLocation deriveCoordinatesJunctionIncomingNode(
			CycleNode junction, CycleNode node) {

		double length = EdgeUtil.computeDirectDistanceInM(junction, node);

		double xJunction = junction.getProjectedLongitude();
		double yJunction = junction.getProjectedLatitude();

		double xNode = node.getProjectedLongitude();
		double yNode = node.getProjectedLatitude();
		
		double x = xJunction + ((xNode - xJunction) * RADIUS * COS) / length
				- ((yNode - yJunction) * RADIUS * SIN) / length;
		double y = yJunction + ((yNode - yJunction) * RADIUS * COS) / length
				+ ((xNode - xJunction) * RADIUS * SIN) / length;

		// log.debug("Junction: " + xJunction + " " + yJunction);
		// log.debug("Junction: " + junction.getLongitudeE6() + " " +
		// junction.getLatitudeE6());
		// log.debug("Old incoming node: " + xNode + " " + yNode);
		// log.debug("Old outcoming node: " + node.getLongitudeE6() + " " +
		// node.getLatitudeE6());
		// log.debug("New incomming node: " + x + " " + y);
		
		return projection.getSphericalGPSLocation(new GPSLocation(y, x, y,
				x, junction.getElevation()));
	}
}
