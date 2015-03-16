package cz.agents.cycleplanner.dataStructure;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class CycleEdgeTest {
	CycleNode a, b, c, d, e;
	
	@Before
	public void setUp() throws Exception {
		a = new CycleNode(1, new GPSLocation(3d, 3d, 3d, 3d, 0d), "");
		b = new CycleNode(2, new GPSLocation(3d, 1d, 3d, 1d, 0d), "");
		c = new CycleNode(3, new GPSLocation(5d, 3d, 5d, 3d, 0d), "");
		d = new CycleNode(4, new GPSLocation(3d, 5d, 3d, 5d, 0d), "");
		e = new CycleNode(5, new GPSLocation(1d, 3d, 1d, 3d, 0d), "");
	}

	@Test
	public void testIntersectingEdges() {
		CycleEdge edge1 = new CycleEdge(e, c, 4);
		CycleEdge edge2 = new CycleEdge(b, d, 4);
		boolean actual = edge1.intersectsEdge(edge2);
		
		assertEquals(true, actual);
	}
	
	@Test
	public void testParallelEdges() {
		CycleEdge edge1 = new CycleEdge(e, d, 2);
		CycleEdge edge2 = new CycleEdge(b, c, 2);
		boolean actual = edge1.intersectsEdge(edge2);
		
		assertEquals(false, actual);
	}
}
