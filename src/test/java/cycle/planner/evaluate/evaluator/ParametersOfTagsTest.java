package cycle.planner.evaluate.evaluator;

import static org.junit.Assert.*;

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
			System.out.println(tag+" "+parameters.getComfortMultiplier(tag)+" "+parameters.getQuietnessMultiplier(tag));
		}
		assertEquals(63, tags.size());
	}
	
}
