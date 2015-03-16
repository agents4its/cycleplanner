package cz.agents.cycleplanner;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.util.Ellipse;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class EllipseTest {
	private static final double F = 6000d;
	private CycleNode focus1, focus2;
	private Ellipse testEllipse; 

	@Before
	public void setUp() throws Exception {
		// latitude <=> y
		// longitude <=> x
		focus1 = new CycleNode(1, new GPSLocation(0d, -F, 0d, -F, 0d), "test");
		focus2 = new CycleNode(2, new GPSLocation(0d, F, 0d, F, 0d), "test");
		// a = 1.6666, b=1.3333
		testEllipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 1.25);
	}

	@Test
	public void createDifferentEllipses() {

		Ellipse ellipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 1.1);
		assertEquals(2.400396793 * F, ellipse.getA(), 0.001d);
		assertEquals(2.182178902 * F, ellipse.getB(), 0.001d);

		ellipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 1.25);
		assertEquals(1.666666667d * F, ellipse.getA(), 0.001d);
		assertEquals(1.333333333d * F, ellipse.getB(), 0.001d);

		ellipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 1.5);
		assertEquals(1.341640786d * F, ellipse.getA(), 0.001d);
		assertEquals(0.894427191d * F, ellipse.getB(), 0.001d);

		ellipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 1.75);

		assertEquals(1.218543592d * F, ellipse.getA(), 0.001d);
		assertEquals(0.6963106238d * F, ellipse.getB(), 0.001d);

		ellipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 2);
		assertEquals(1.154700538 * F, ellipse.getA(), 0.001d);
		assertEquals(0.5773502692 * F, ellipse.getB(), 0.001d);

		ellipse = new Ellipse(focus1.getGpsLocation(), focus2.getGpsLocation(), 2.25);
		assertEquals(1.116312611 * F, ellipse.getA(), 0.001d);
		assertEquals(0.4961389384 * F, ellipse.getB(), 0.001d);
	}

	@Test
	public void testInsideMethod() {

		// latitude <=> y
		// longitude <=> x
		
		// right most edge and top most edge points
		CycleNode node = new CycleNode(3, new GPSLocation(0d, 1.7d * F, 0d, 1.7d * F, 0d), "");
		assertFalse(testEllipse.isInside(node.getGpsLocation()));
		node = new CycleNode(3, new GPSLocation(0d, 1.6d * F, 0d, 1.6d * F, 0d), "");
		assertTrue(testEllipse.isInside(node.getGpsLocation()));		
		node = new CycleNode(3, new GPSLocation(1.4d * F, 0d, 1.4d * F, 0d, 0d), "");
		assertFalse(testEllipse.isInside(node.getGpsLocation()));
		node = new CycleNode(3, new GPSLocation(1.3d * F, 0d, 1.3d * F, 0d, 0d), "");
		assertTrue(testEllipse.isInside(node.getGpsLocation()));
		
		// point outside ellipse
		node = new CycleNode(3, new GPSLocation(2d * F, 0d, 2d * F, 0d, 0d), "");
		assertFalse(testEllipse.isInside(node.getGpsLocation()));
		// point inside ellipse
		node = new CycleNode(3, new GPSLocation(1d * F, 0d, 1d * F, 0d, 0d), "");
		assertTrue(testEllipse.isInside(node.getGpsLocation()));
	}
	
	@Test
	public void testCenter() {

		// latitude <=> y
		// longitude <=> x
		CycleNode center = new CycleNode(3, new GPSLocation(0d, 0d, 0d, 0d, 0d), "");
		assertTrue(testEllipse.isInside(center.getGpsLocation()));
	}

}
