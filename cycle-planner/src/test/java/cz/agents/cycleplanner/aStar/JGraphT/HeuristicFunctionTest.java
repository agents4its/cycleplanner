package cz.agents.cycleplanner.aStar.JGraphT;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.aStar.JGraphT.HeuristicFunction;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.evaluate.Evaluator;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;
import eu.superhub.wp5.plannerdataimporter.util.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class HeuristicFunctionTest {
	private static final double COMFORT_MIN_MULTIPLIER = 0.5;
	private static final double QUIETNESS_MIN_MULTIPLIER = 0.2;
	private CycleNode goal;
	private TimedNode timedCurrent;
	private double averageSpeedKMpH;
	private double flatness, travelTime;
	private Profile profile;

	@Before
	public void setUp() throws Exception {
		EPSGProjection projection = new EPSGProjection(2065);

		goal = new CycleNode(0,
				projection.getProjectedGPSLocation(new GPSLocation(50.102775,
						14.383461, 300d)), "TEST");
		CycleNode current = new CycleNode(1,
				projection.getProjectedGPSLocation(new GPSLocation(50.091928,
						14.380028, 400d)), "TEST");
		timedCurrent = new TimedNode(current, new DateTime(), 0);
		averageSpeedKMpH = 13.68;
		
		double elevation = ((timedCurrent.getElevation() - goal.getElevation()) > 0) ? (timedCurrent
				.getElevation() - goal.getElevation()) : 0;

		flatness = elevation * Evaluator.PERCEPTION_UPHILL_MULTIPLIER
				/ (averageSpeedKMpH/3.6);
		double x = timedCurrent.getProjectedLatitude() - goal.getProjectedLatitude();
		double y = timedCurrent.getProjectedLongitude()
				- goal.getProjectedLongitude();
		travelTime = Math.sqrt(x * x + y * y)
				/ ((averageSpeedKMpH/3.6) * Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER);
	}

	@Test
	public void testTravelTime() {
		profile = new Profile(1, 0, 0, 0);
		HeuristicFunction heuristic = new HeuristicFunction(goal, profile,
				averageSpeedKMpH);
		double actual = heuristic.getCostToGoalEstimate(timedCurrent);
		System.out.println(actual + "   " + travelTime);
		assertEquals(travelTime, actual, 0);
	}

	@Test
	public void testComfort() {
		profile = new Profile(0, 1, 0, 0);
		HeuristicFunction heuristic = new HeuristicFunction(goal, profile,
				averageSpeedKMpH);
		double actual = heuristic.getCostToGoalEstimate(timedCurrent);
		double expected = travelTime * COMFORT_MIN_MULTIPLIER;
		System.out.println(actual + "   " + expected);
		assertEquals(expected, actual, 0);
	}

	@Test
	public void testQuietness() {
		profile = new Profile(0, 0, 1, 0);
		HeuristicFunction heuristic = new HeuristicFunction(goal, profile,
				averageSpeedKMpH);
		double actual = heuristic.getCostToGoalEstimate(timedCurrent);
		double expected = travelTime * QUIETNESS_MIN_MULTIPLIER;
		System.out.println(actual + "   " + expected);
		assertEquals(expected, actual, 0);
	}

	@Test
	public void testFlatness() {
		profile = new Profile(0, 0, 0, 1);
		HeuristicFunction heuristic = new HeuristicFunction(goal, profile,
				averageSpeedKMpH);
		double actual = heuristic.getCostToGoalEstimate(timedCurrent);
		System.out.println(actual + "   " + flatness);
		assertEquals(flatness, actual, 0);
	}
}
