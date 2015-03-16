package cz.agents.cycleplanner.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CycleDataTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLocationInside() {
		Assert.assertTrue(new BrnoCycleData().locationInside(49.200500, 16.607744));
		Assert.assertTrue(new CeskeBudejoviceCycleData().locationInside(48.983659, 14.473057));
		Assert.assertTrue(new HradecKraloveCycleData().locationInside(50.212591, 15.842427));
		Assert.assertTrue(new PardubiceCycleData().locationInside(50.034102, 15.774985));
		Assert.assertTrue(new PlzenCycleData().locationInside(49.738138, 13.357039));
		Assert.assertTrue(new PragueCycleData().locationInside(50.078029, 14.440526));
	}

}
