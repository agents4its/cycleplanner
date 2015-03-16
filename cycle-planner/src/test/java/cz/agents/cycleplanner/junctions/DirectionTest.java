package cz.agents.cycleplanner.junctions;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.util.AngleUtil;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class DirectionTest {

	@Test
	public void right() {
		CycleNode a = new CycleNode(1, new GPSLocation(-1, 0, -1, 0), "");
		CycleNode b = new CycleNode(2, new GPSLocation(0, 0, 0, 0), "");
		CycleNode c = new CycleNode(3, new GPSLocation(0, 1, 0, 1), "");
		
		double actual = AngleUtil.getAngle(a, b, c);
		assertEquals(90d, actual, 0d);
	}
	
	@Test
	public void straight() {
		CycleNode a = new CycleNode(1, new GPSLocation(-1, 0, -1, 0), "");
		CycleNode b = new CycleNode(2, new GPSLocation(0, 0, 0, 0), "");
		CycleNode c = new CycleNode(3, new GPSLocation(1, 0, 1, 0), "");
		
		double actual = AngleUtil.getAngle(a, b, c);
		assertEquals(180d, actual, 0);
	}
	
	@Test
	public void straight2() {
		CycleNode a = new CycleNode(1, new GPSLocation(-1, 0, -1, 0), "");
		CycleNode b = new CycleNode(2, new GPSLocation(0, 0, 0, 0), "");
		GPSLocation location = JunctionCoordinatesUtil.deriveCoordinatesJunctionIncomingNode(b, a);
		GPSLocation location2 = new GPSLocation(location.getLatitude(), location.getLongitude(), -location.getProjectedLatitude(), location.getProjectedLongitude());
		CycleNode c = new CycleNode(3, location2, "");
		
		double actual = AngleUtil.getAngle(a, b, c);
		assertEquals(180d - Math.toDegrees(JunctionCoordinatesUtil.NEW_NODE_ANGLE), actual, 0.001d);
	}
	
	@Test
	public void uTurn() {
		CycleNode a = new CycleNode(1, new GPSLocation(-1, 0, -1, 0), "");
		CycleNode b = new CycleNode(2, new GPSLocation(0, 0, 0, 0), "");
		CycleNode c = new CycleNode(3, new GPSLocation(-1, 0, -1, 0), "");
		
		double actual = AngleUtil.getAngle(a, b, c);
		assertEquals(0d, actual, 0d);
	}
	
	@Test
	/**
	 * Creates node using JunctionCoordinateUtil, which should be part of U-turn set.
	 */
	public void uTurn2() {

		CycleNode a = new CycleNode(1, new GPSLocation(-1, 0, -1, 0), "");
		CycleNode b = new CycleNode(2, new GPSLocation(0, 0, 0, 0), "");
		CycleNode c = new CycleNode(3, JunctionCoordinatesUtil.deriveCoordinatesJunctionOutcomingNode(b, a), "");
		
		double actual = AngleUtil.getAngle(a, b, c);
		assertEquals(360d - Math.toDegrees(JunctionCoordinatesUtil.NEW_NODE_ANGLE), actual, 0d);
	}
	
	@Test
	public void left() {
		CycleNode a = new CycleNode(1, new GPSLocation(-1, 0, -1, 0), "");
		CycleNode b = new CycleNode(2, new GPSLocation(0, 0, 0, 0), "");
		CycleNode c = new CycleNode(3, new GPSLocation(0, -1, 0, -1), "");
		
		double actual = AngleUtil.getAngle(a, b, c);
		assertEquals(270d, actual, 0d);
	}
}
