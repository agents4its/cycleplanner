package cz.agents.cycleplanner.aStar.aima;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.aStar.aima.Heuristic;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.evaluate.Evaluator;
import eu.superhub.wp5.plannerdataimporter.util.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class HeuristicTest {
	private static final double COMFORT_MIN_MULTIPLIER = 0.5;
	private static final double QUIETNESS_MIN_MULTIPLIER = 0.2;
	private CycleNode goal, state;
	private double averageSpeedMetersPerSecond, averageSpeedKMpH;
	private double flatness, travelTime;
	private Profile profile;

	@Before
	public void setUp() throws Exception {
		EPSGProjection projection = new EPSGProjection(2065);

		goal = new CycleNode(0, projection.getProjectedGPSLocation(new GPSLocation(50.102775, 14.383461, 400d)), "TEST");
		state = new CycleNode(1, projection.getProjectedGPSLocation(new GPSLocation(50.091928, 14.380028, 300d)), "TEST");

		averageSpeedMetersPerSecond = 3.8;
		averageSpeedKMpH = 13.68;
		double elevation = ((state.getElevation() - goal.getElevation()) > 0) ? (state.getElevation() - goal
				.getElevation()) : 0;

		flatness = elevation * Evaluator.PERCEPTION_UPHILL_MULTIPLIER / averageSpeedMetersPerSecond;
		double x = state.getProjectedLatitude() - goal.getProjectedLatitude();
		double y = state.getProjectedLongitude() - goal.getProjectedLongitude();
		travelTime = Math.sqrt(x * x + y * y)
				/ (averageSpeedMetersPerSecond * Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER);
	}

	@Test
	public void testTravelTime() {
		profile = new Profile(1, 0, 0, 0);
		Heuristic heuristic = new Heuristic(goal, profile, averageSpeedKMpH);
		double actual = heuristic.h(state);
		assertEquals(travelTime, actual, 0);
	}

	@Test
	public void testComfort() {
		profile = new Profile(0, 1, 0, 0);
		Heuristic heuristic = new Heuristic(goal, profile, averageSpeedKMpH);
		double actual = heuristic.h(state);
		double expected = travelTime * COMFORT_MIN_MULTIPLIER;
		assertEquals(expected, actual, 0);
	}

	@Test
	public void testQuietness() {
		profile = new Profile(0, 0, 1, 0);
		Heuristic heuristic = new Heuristic(goal, profile, averageSpeedKMpH);
		double actual = heuristic.h(state);
		double expected = travelTime * QUIETNESS_MIN_MULTIPLIER;
		assertEquals(expected, actual, 0);
	}

	@Test
	public void testFlatness() {
		profile = new Profile(0, 0, 0, 1);
		Heuristic heuristic = new Heuristic(goal, profile, averageSpeedKMpH);
		double actual = heuristic.h(state);

		assertEquals(flatness, actual, 0);
	}
}
