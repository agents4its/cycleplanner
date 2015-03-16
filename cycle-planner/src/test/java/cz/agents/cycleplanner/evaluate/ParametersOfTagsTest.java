package cz.agents.cycleplanner.evaluate;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class ParametersOfTagsTest {

	@Test
	public void correctLoadingTest() {
		ParametersOfTags parameters = ParametersOfTags.INSTANCE;

		Set<String> tags = parameters.getParametersKeySet();
		System.out.println("Number of loaded tags: " + tags.size());
		System.out.println("***** Tags *****");
		for (Iterator<String> it = tags.iterator(); it.hasNext();) {
			String tag = it.next();
			System.out.println(tag + ": " + parameters.getTravelTimeMultiplier(tag) + ", "
					+ parameters.getTravelTimeSlowdownConstant(tag) + ", " + parameters.getComfortMultiplier(tag)
					+ ", " + parameters.getQuietnessMultiplier(tag) + ", ");
			// + parameters.getJunctionProlongationConstant(tag));
		}
		assertEquals(119, tags.size());
	}

}
