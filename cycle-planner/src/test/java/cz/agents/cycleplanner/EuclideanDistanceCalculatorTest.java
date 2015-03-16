package cz.agents.cycleplanner;

import static org.junit.Assert.*;

import org.junit.Test;

import cz.agents.cycleplanner.util.EuclideanDistanceCalculator;

public class EuclideanDistanceCalculatorTest {

	@Test
	public void test() {
		double[] a = new double[] { 12, 13, 8.5, 4.9 };
		double[] b = new double[] { 0.5, 3.1, 4.9, 5.6 };
		double expected = 15.611214;
		double actual = EuclideanDistanceCalculator.calculate(a, b);

		assertEquals(expected, actual, 0.0000001);
	}

}
