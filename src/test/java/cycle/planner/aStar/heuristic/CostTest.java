package cycle.planner.aStar.heuristic;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import cvut.fel.nemetma1.aStar.search.CycleAction;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cycle.planner.aStar.Cost;
import cycle.planner.aStar.Profile;
import cycle.planner.util.EPSGProjection;
import eu.superhub.wp5.wp5common.GPSLocation;

public class CostTest {

	private CycleEdge edgeToTake;
	private double averageSpeedMetersPerSecond;
	private double travelTime;
	private Profile profile;

	@Before
	public void setUp() throws Exception {
		EPSGProjection projection = new EPSGProjection(2065);
		CycleNode toNode = new CycleNode(0, projection.getProjectedGPSLocation(new GPSLocation(50.102775, 14.383461)),
				"TEST", 400);
		CycleNode fromNode = new CycleNode(1,
				projection.getProjectedGPSLocation(new GPSLocation(50.091928, 14.380028)), "TEST", 300,
				getFromNodeTags());
		double x = fromNode.getProjectedLatitude() - toNode.getProjectedLatitude();
		double y = fromNode.getProjectedLongitude() - toNode.getProjectedLongitude();
		double lengthInMetres = Math.sqrt(x * x + y * y);
		travelTime = ((lengthInMetres + 800) / (3.8 * 0.6)) + 30;

		edgeToTake = new CycleEdge(fromNode, toNode, lengthInMetres, 100, 0, getEdgeWayTags(), getEdgeRelationTags());
		averageSpeedMetersPerSecond = 3.8;

	}

	@Test
	public void testTravelTime() {
		profile = new Profile(1, 0, 0, 0);
		Cost cost = new Cost(profile, averageSpeedMetersPerSecond);
		double actual = cost.c(null, new CycleAction(edgeToTake), null);
		System.out.println(actual + "   " + travelTime);
		assertEquals(travelTime, actual, 0.000001);
	}

	@Test
	public void testComfort() {
		profile = new Profile(0, 1, 0, 0);
		Cost cost = new Cost(profile, averageSpeedMetersPerSecond);
		double actual = cost.c(null, new CycleAction(edgeToTake), null);
		double expected = travelTime * 4;
		System.out.println(actual + "   " + expected);
		assertEquals(expected, actual, 0.000001);
	}

	@Test
	public void testQuietness() {
		profile = new Profile(0, 0, 1, 0);
		Cost cost = new Cost(profile, averageSpeedMetersPerSecond);
		double actual = cost.c(null, new CycleAction(edgeToTake), null);
		double expected = travelTime * 3;
		System.out.println(actual + "   " + expected);
		assertEquals(expected, actual, 0.000001);
	}

	@Test
	public void testFlatness() {
		profile = new Profile(0, 0, 0, 1);
		Cost cost = new Cost(profile, averageSpeedMetersPerSecond);
		double actual = cost.c(null, new CycleAction(edgeToTake), null);
		double expected = 342.1052631578947;
		System.out.println(actual + "   " + expected);
		assertEquals(expected, actual, 0.000001);
	}

	private Set<Tag> getFromNodeTags() {
		Set<Tag> tags = new HashSet<Tag>();

		tags.add(new Tag("highway", "traffic_signals")); // 1 30 1 3
		tags.add(new Tag("crossing", "traffic_signals"));// 1 30 1 1
		tags.add(new Tag("crossing", "zebra")); // 1 15 1 1

		return tags;
	}

	private Set<Tag> getEdgeWayTags() {
		Set<Tag> tags = new HashSet<Tag>();

		tags.add(new Tag("access", "forestry")); // 0.8 0 2 1
		tags.add(new Tag("smoothness", "bad")); // 0.7 0 3 1
		tags.add(new Tag("surface", "dirt")); // 0.7 0 3 1
		tags.add(new Tag("surface", "ground")); // 0.6 0 4 1
		return tags;
	}

	private Set<Tag> getEdgeRelationTags() {
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("route", "bicycle")); // 1 0 1 0.9
		return tags;
	}

}
