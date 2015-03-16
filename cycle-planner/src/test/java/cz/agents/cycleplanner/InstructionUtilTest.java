package cz.agents.cycleplanner;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.api.datamodel.Instruction;
import cz.agents.cycleplanner.api.datamodel.Manoeuvre;
import cz.agents.cycleplanner.api.datamodel.RoadType;
import cz.agents.cycleplanner.api.datamodel.Surface;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.util.InstructionUtil;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class InstructionUtilTest {

	CycleNode prevFrom, prevTo;
	CycleNode actualFrom, actualTo;
	CycleNode nextFrom, nextTo;

	@Before
	public void setUp() throws Exception {
		prevFrom = new CycleNode(1, new GPSLocation(1, 1), "Instruction test");
		prevTo = actualFrom = new CycleNode(2, new GPSLocation(2, 2), "Instruction test");
		actualTo = nextFrom = new CycleNode(3, new GPSLocation(3, 3), "Instruction test");
		nextTo = new CycleNode(4, new GPSLocation(4, 4), "Instruction test");
	}

	@Test
	public void testSurface() {
		Set<String> tags = new HashSet<>();

		tags.add("way::surface::gravel");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);

		assertEquals(Surface.GRAVEL, ins.getSurface());
	}

	@Test
	public void testStreetName() {
		Set<String> tags = new HashSet<>();

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals("Bělehradská", ins.getStreetName());
	}

	@Test
	public void testRoadTypeHighWay() {
		Set<String> tags = new HashSet<>();

		tags.add("way::highway::primary");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals(RoadType.PRIMARY, ins.getRoadType());
	}

	@Test
	public void testRoadTypeHighWayResultCycleway() {
		Set<String> tags = new HashSet<>();

		tags.add("way::highway::cycleway");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals(RoadType.CYCLEWAY, ins.getRoadType());
	}

	@Test
	public void testRoadTypeHighWayResultFootway() {
		Set<String> tags = new HashSet<>();

		tags.add("way::highway::footway;path");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals(RoadType.FOOTWAY, ins.getRoadType());
	}

	@Test
	public void testRoadTypePedestrian() {
		Set<String> tags = new HashSet<>();

		tags.add("way::highway::pedestrian");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals(RoadType.FOOTWAY, ins.getRoadType());
	}

	@Test
	public void testRoadTypeCycleWay() {
		Set<String> tags = new HashSet<>();
		tags.add("way::cycleway::share_busway");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals(RoadType.CYCLEWAY, ins.getRoadType());
	}

	@Test
	public void testRoadTypeFootway() {
		Set<String> tags = new HashSet<>();

		tags.add("way::footway::sidewalk");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 0l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		assertEquals(RoadType.FOOTWAY, ins.getRoadType());
	}

	@Test
	public void testContinue() {
		Set<String> tags = new HashSet<>();
		tags.add("way::footway::sidewalk");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 170l);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.CONTINUE, ins.getManoeuvre());
	}

	@Test
	public void testContinue2() {
		Set<String> tags = new HashSet<>();
		tags.add("way::footway::sidewalk");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 190l);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.CONTINUE, ins.getManoeuvre());
	}

	@Test
	public void testKeepRight() {
		Set<String> tags = new HashSet<>();
		tags.add("way::highway::primary");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 160l);

		Set<String> tagsNext = new HashSet<>();
		tagsNext.add("way::footway::sidewalk");
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tagsNext, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.KEEP_RIGHT, ins.getManoeuvre());
	}

	@Test
	public void testKeepLeft() {
		Set<String> tags = new HashSet<>();
		tags.add("way::highway::primary");

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 200l);

		Set<String> tagsNext = new HashSet<>();
		tagsNext.add("way::footway::sidewalk");
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tagsNext, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.KEEP_LEFT, ins.getManoeuvre());
	}

	@Test
	public void testTurnLeft() {
		Set<String> tags = new HashSet<>();

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 300l);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.TURN_LEFT, ins.getManoeuvre());
	}

	@Test
	public void testTurnRight() {
		Set<String> tags = new HashSet<>();

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 100l);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.TURN_RIGHT, ins.getManoeuvre());
	}

	@Test
	public void testUTurn() {
		Set<String> tags = new HashSet<>();

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, 0l);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(Manoeuvre.U_TURN, ins.getManoeuvre());
	}

	@Test
	public void testNoTurn() {
		Set<String> tags = new HashSet<>();

		CycleEdge previous = new CycleEdge(prevFrom, prevTo, 0, tags, new HashSet(), 131565891l, 0);
		CycleEdge actual = new CycleEdge(actualFrom, actualTo, 0, tags, new HashSet(), 0l, Double.POSITIVE_INFINITY);
		CycleEdge next = new CycleEdge(nextFrom, nextTo, 0, tags, new HashSet(), 131565891l, 0);

		Instruction ins = InstructionUtil.getInstruction(previous, actual, next);
		System.out.println("MANOEUVRE: " + ins.getManoeuvre());
		assertEquals(null, ins.getManoeuvre());
	}
}
