package cz.agents.cycleplanner.MLC;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.evaluate.EvaluationDetails;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class MLCCycleEdgeTest {
	public static Logger log = Logger.getLogger(MLCCycleEdgeTest.class);

	/**
	 * Test if Comfort criterion is define as sum of traffic and surface
	 * coefficients
	 */
	// @Test
	public void testSumOfTrafficSurfaceCoefficients() {
		CycleNode origin = new CycleNode(0, new GPSLocation(50.102775, 14.383461, 300d), "");
		CycleNode destination = new CycleNode(1, new GPSLocation(50.102775, 14.383461, 300d), "");
		// CycleEdge edge = new CycleEdge(origin, destination, 0);
		CycleEdge edge = new CycleEdge(origin, destination, 1, new HashSet<>(), new HashSet<>(), 0l, 0d);
		double averageSpeedKMpH = 13.68;
		MLCCostFunction<CycleNode, CycleEdge> costFunction = new MLCCycleCostFunction(averageSpeedKMpH);

		int travelTime = (int) Math.round(10.0 / 3.8 + 10.0);
		int flatness = (int) Math.round(12.0 / 3.8);

		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 9, 12, 12));
		log.info("1. Expected: [" + travelTime + ", 210, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 210, flatness }, costFunction.getCostVector(null, null, edge));

		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 1, 1, 12));
		log.info("2. Expected: [" + travelTime + ", 10, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 10, flatness }, costFunction.getCostVector(null, null, edge));

		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 1, 12, 12));
		log.info("3. Expected: [" + travelTime + ", 120, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 120, flatness }, costFunction.getCostVector(null, null, edge));

		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 9, 1, 12));
		log.info("4. Expected: [" + travelTime + ", 90, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 90, flatness }, costFunction.getCostVector(null, null, edge));
	}

	/**
	 * Test if Comfort criterion is define as max of traffic and surface
	 * coefficients
	 */
	@Test
	public void testMaxTrafficSurfaceCoefficients() {
		CycleNode origin = new CycleNode(0, new GPSLocation(50.102775, 14.383461, 300d), "");
		CycleNode destination = new CycleNode(1, new GPSLocation(50.102775, 14.383461, 300d), "");
		CycleEdge edge = new CycleEdge(origin, destination, 1, new HashSet<>(), new HashSet<>(), 0l, 0d);
		double averageSpeedKMpH = 13.68;

		int travelTime = (int) Math.round(10.0 / 3.8 + 10.0);
		int flatness = (int) Math.round(12.0 / 3.8);

		// We need to initialize cost function before each change on the edge,
		// because MLCCycleCostFunction is caching costs for each edge
		MLCCostFunction<CycleNode, CycleEdge> costFunction = new MLCCycleCostFunction(averageSpeedKMpH);
		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 9, 12, 12));
		log.info("1. Expected: [" + travelTime + ", 120, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 120, flatness }, costFunction.getCostVector(null, null, edge));

		costFunction = new MLCCycleCostFunction(averageSpeedKMpH);
		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 1, 1, 12));
		log.info(edge.getEvaluationDetails().getComfortMultiplier() + " "
				+ edge.getEvaluationDetails().getQuietnessMultiplier());
		log.info("2. Expected: [" + travelTime + ", 10, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 10, flatness }, costFunction.getCostVector(null, null, edge));

		costFunction = new MLCCycleCostFunction(averageSpeedKMpH);
		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 1, 12, 12));
		log.info("3. Expected: [" + travelTime + ", 120, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 120, flatness }, costFunction.getCostVector(null, null, edge));

		costFunction = new MLCCycleCostFunction(averageSpeedKMpH);
		edge.setEvaluationDetails(new EvaluationDetails(10, 10, 9, 1, 12));
		log.info("4. Expected: [" + travelTime + ", 90, " + flatness + "] Actual: "
				+ Arrays.toString(costFunction.getCostVector(null, null, edge)));
		Assert.assertArrayEquals(new int[] { travelTime, 90, flatness }, costFunction.getCostVector(null, null, edge));
	}

}
