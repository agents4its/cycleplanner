package cz.agents.cycleplanner.junctions;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class DeriveLocationTest {

	private final static Logger logger = Logger
			.getLogger(DeriveLocationTest.class);


	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testEightJunctionCoordinates() {
		CycleNode a = new CycleNode(1, new GPSLocation(3d, 3d, 3d, 3d, 0d), "");
		CycleNode b = new CycleNode(2, new GPSLocation(3d, 1d, 3d, 1d, 0d), "");
		CycleNode c = new CycleNode(3, new GPSLocation(5d, 3d, 5d, 3d, 0d), "");
		CycleNode d = new CycleNode(4, new GPSLocation(3d, 5d, 3d, 5d, 0d), "");
		CycleNode e = new CycleNode(5, new GPSLocation(1d, 3d, 1d, 3d, 0d), "");

		GPSLocation ab = JunctionCoordinatesUtil.deriveCoordinatesJunctionOutcomingNode(a, b);
		GPSLocation ba = JunctionCoordinatesUtil.deriveCoordinatesJunctionIncomingNode(a, b);
		logger.info("ab: " + ab);
		logger.info("ba: " + ba);

		GPSLocation ac = JunctionCoordinatesUtil.deriveCoordinatesJunctionOutcomingNode(a, c);
		GPSLocation ca = JunctionCoordinatesUtil.deriveCoordinatesJunctionIncomingNode(a, c);
		logger.info("ac: " + ac);
		logger.info("ca: " + ca);

		GPSLocation ad = JunctionCoordinatesUtil.deriveCoordinatesJunctionOutcomingNode(a, d);
		GPSLocation da = JunctionCoordinatesUtil.deriveCoordinatesJunctionIncomingNode(a, d);
		logger.info("ad: " + ad);
		logger.info("da: " + da);
		
		GPSLocation ae = JunctionCoordinatesUtil.deriveCoordinatesJunctionOutcomingNode(a, e);
		GPSLocation ea = JunctionCoordinatesUtil.deriveCoordinatesJunctionIncomingNode(a, e);
		logger.info("ae: " + ae);
		logger.info("ea: " + ea);
		
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ab.getLatitude(), ab.getLongitude(), ab.getLatitude(), ab.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ba.getLatitude(), ba.getLongitude(), ba.getLatitude(), ba.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ac.getLatitude(), ac.getLongitude(), ac.getLatitude(), ac.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ca.getLatitude(), ca.getLongitude(), ca.getLatitude(), ca.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ad.getLatitude(), ad.getLongitude(), ad.getLatitude(), ad.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(da.getLatitude(), da.getLongitude(), da.getLatitude(), da.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ae.getLatitude(), ae.getLongitude(), ae.getLatitude(), ae.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
		assertEquals(EdgeUtil.computeDirectDistanceInM(a.getGpsLocation(), new GPSLocation(ea.getLatitude(),ea.getLongitude(), ea.getLatitude(), ea.getLongitude())), JunctionCoordinatesUtil.RADIUS, 0d);
	}

//	public static GPSLocation deriveCoordinatesOutcomingNode(CycleNode from,
//			CycleNode to) {
//
//		double length = 2;
//
//		logger.info("Length outcomming: " + length);
//
//		double xFrom = from.getLatitude();
//		double yFrom = from.getLongitude();
//
//		double xTo = to.getLatitude();
//		double yTo = to.getLongitude();
//
//		
//		double x = xFrom + ((xFrom - xTo) * COS) / length
//				+ ((yFrom - yTo) * SIN) / length;
//		double y = yFrom + ((yFrom - yTo) * COS) / length
//				- ((xFrom - xTo) * SIN) / length;
//		return new GPSLocation(x, y, from.getElevation());
//	}
//
//	public static GPSLocation deriveCoordinatesIncomingNode(CycleNode from,
//			CycleNode to) {
//
//		double length = 2;
//
//		logger.info("Length incomming: " + length);
//
//		double xFrom = from.getLatitude();
//		double yFrom = from.getLongitude();
//
//		double xTo = to.getLatitude();
//		double yTo = to.getLongitude();
//
//		double x = xFrom + ((xFrom - xTo) * RADIUS * COS) / length
//				- ((yFrom - yTo) * RADIUS * SIN) / length;
//		double y = yFrom + ((yFrom - yTo) * RADIUS * COS) / length
//				+ ((xFrom - xTo) * RADIUS * SIN) / length;
//
//		return new GPSLocation(x, y, to.getElevation());
//	}

}
