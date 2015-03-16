package cz.agents.cycleplanner.evaluate;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;
import eu.superhub.wp5.plannerdataimporter.util.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class EvaluatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void createEvaluationDetailsTest() throws NoSuchAuthorityCodeException, FactoryException, TransformException {
		System.out.println("***** With tags *****");
		EPSGProjection projection = new EPSGProjection(2065);

		Set<Tag> fromNodeTags = getFromNodeTags();
		CycleNode fromNode = new CycleNode(0, projection.getProjectedGPSLocation(new GPSLocation(50.102775, 14.383461,
				350d)), "", fromNodeTags);

		Set<Tag> toNodeTags = getToNodeTags();
		CycleNode toNode = new CycleNode(1, projection.getProjectedGPSLocation(new GPSLocation(50.091928, 14.380028,
				300d)), "", toNodeTags);

		double lengthInMetres = EdgeUtil.computeDirectDistanceInM(fromNode.getGpsLocation(), toNode.getGpsLocation());

		Set<Tag> wayTags = getEdgeWayTags();
		Set<Tag> relationTags = getEdgeRelationTags();

		CycleEdge edge = new CycleEdge(fromNode, toNode, lengthInMetres, wayTags, relationTags);

		EvaluationDetails details = Evaluator.createEvaluationDetails(edge);

		double expectedPrecomputation = (lengthInMetres + Evaluator.UPHILL_MULTIPLIER * edge.getRises())
				/ ((((edge.getDrops() / lengthInMetres) / Evaluator.CRITICAL_DOWNHILL_GRADE)
						* (Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER - 1) + 1) * 0.6);
		System.out.println("Edge elevation " + edge.getDrops() + " " + edge.getRises());
		System.out.println("Expected precomputed value: " + expectedPrecomputation);
		System.out.println("Travel time: " + details.getTravelTimePrecomputation() + " "
				+ details.getTravelTimeSlowdownConstant());
		System.out.println("Multipliers: " + details.getComfortMultiplier() + " " + details.getQuietnessMultiplier()
				+ " " + details.getFlatnessMultiplier());

		assertEquals(expectedPrecomputation, details.getTravelTimePrecomputation(), 0d);
		assertEquals(details.getComfortMultiplier(), 4, 0);
		assertEquals(details.getQuietnessMultiplier(), 3, 0);
		assertEquals(details.getTravelTimeSlowdownConstant(), 30, 0);
		assertEquals(details.getFlatnessMultiplier(), 0, 0);
	}

	@Test
	public void createEvaluationDetailsFromEdgeWithoutTagsTest() throws NoSuchAuthorityCodeException, FactoryException,
			TransformException {
		System.out.println("***** Without tags *****");
		EPSGProjection projection = new EPSGProjection(2065);

		CycleNode fromNode = new CycleNode(0, projection.getProjectedGPSLocation(new GPSLocation(50.102775, 14.383461,
				300d)), "", new HashSet<Tag>());

		CycleNode toNode = new CycleNode(1, projection.getProjectedGPSLocation(new GPSLocation(50.091928, 14.380028,
				350d)), "", new HashSet<Tag>());

		double lengthInMetres = EdgeUtil.computeDirectDistanceInM(fromNode.getGpsLocation(), toNode.getGpsLocation());

		CycleEdge edge = new CycleEdge(fromNode, toNode, lengthInMetres, new HashSet<Tag>(), new HashSet<Tag>());

		EvaluationDetails details = Evaluator.createEvaluationDetails(edge);

		double expectedPrecomputation = (lengthInMetres + Evaluator.UPHILL_MULTIPLIER * edge.getRises())
				/ (((Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER - 1) * (edge.getDrops() / lengthInMetres)
						/ Evaluator.CRITICAL_DOWNHILL_GRADE + 1));

		System.out.println("Edge elevation " + edge.getDrops() + " " + edge.getRises());

		System.out.println("Expected precomputed value: " + expectedPrecomputation);
		System.out.println("Travel time: " + details.getTravelTimePrecomputation() + " "
				+ details.getTravelTimeSlowdownConstant());
		System.out.println("Multipliers: " + details.getComfortMultiplier() + " " + details.getQuietnessMultiplier()
				+ " " + details.getFlatnessMultiplier());
		assertEquals(expectedPrecomputation, details.getTravelTimePrecomputation(), 0d);
		assertEquals(details.getComfortMultiplier(), 1.0, 0);
		assertEquals(details.getQuietnessMultiplier(), 1.0, 0);
		assertEquals(details.getTravelTimeSlowdownConstant(), 0, 0);
		assertEquals(details.getFlatnessMultiplier(), 650, 0);
	}

	@Test
	public void notConsideringOffroadGroupTest() throws NoSuchAuthorityCodeException, FactoryException,
			TransformException {
		System.out.println("***** Offroad *****");
		EPSGProjection projection = new EPSGProjection(2065);

		CycleNode fromNode = new CycleNode(0, projection.getProjectedGPSLocation(new GPSLocation(50.102775, 14.383461,
				300d)), "", new HashSet<Tag>());

		CycleNode toNode = new CycleNode(1, projection.getProjectedGPSLocation(new GPSLocation(50.091928, 14.380028,
				350d)), "", new HashSet<Tag>());

		double lengthInMetres = EdgeUtil.computeDirectDistanceInM(fromNode.getGpsLocation(), toNode.getGpsLocation());

		CycleEdge edge = new CycleEdge(fromNode, toNode, lengthInMetres, getEdgeWayTagsOnlyOffRoad(),
				new HashSet<Tag>());

		EvaluationDetails details = Evaluator.createEvaluationDetails(edge);

		double expectedPrecomputation = (lengthInMetres + Evaluator.UPHILL_MULTIPLIER * edge.getRises())
				/ (((Evaluator.MAXIMUM_DOWNHILL_SPEED_MULTIPLIER - 1) * (edge.getDrops() / lengthInMetres)
						/ Evaluator.CRITICAL_DOWNHILL_GRADE + 1) * 0.9);
		System.out.println("Edge elevation " + edge.getDrops() + " " + edge.getRises());
		System.out.println("Expected precomputed value: " + expectedPrecomputation);
		System.out.println("Travel time: " + details.getTravelTimePrecomputation() + " "
				+ details.getTravelTimeSlowdownConstant());
		System.out.println("Multipliers: " + details.getComfortMultiplier() + " " + details.getQuietnessMultiplier()
				+ " " + details.getFlatnessMultiplier());
		assertEquals(expectedPrecomputation, details.getTravelTimePrecomputation(), 0d);
		assertEquals(details.getComfortMultiplier(), 1.5, 0);
		assertEquals(details.getQuietnessMultiplier(), 1.0, 0);
		assertEquals(details.getTravelTimeSlowdownConstant(), 0, 0);
		assertEquals(details.getFlatnessMultiplier(), 650, 0);
	}

	private Set<Tag> getFromNodeTags() {
		Set<Tag> tags = new HashSet<Tag>();

		tags.add(new Tag("highway", "traffic_signals")); // 1 30 1 3
		tags.add(new Tag("crossing", "traffic_signals"));// 1 30 1 1
		tags.add(new Tag("crossing", "zebra")); // 1 15 1 1

		return tags;
	}

	private Set<Tag> getToNodeTags() {
		Set<Tag> tags = new HashSet<Tag>();

		tags.add(new Tag("highway", "elevator")); // 1 75 7 1

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

	private Set<Tag> getEdgeWayTagsOnlyOffRoad() {
		Set<Tag> tags = new HashSet<Tag>();

		tags.add(new Tag("access", "forestry")); // 0.8 0 2 1
		tags.add(new Tag("access", "agricultural")); // 0.8 0 2 1
		tags.add(new Tag("surface", "compacted")); // 0.9 0 1.5 1
		return tags;
	}

	private Set<Tag> getEdgeRelationTags() {
		Set<Tag> tags = new HashSet<Tag>();
		tags.add(new Tag("route", "bicycle")); // 1 0 1 0.9
		return tags;
	}
}
