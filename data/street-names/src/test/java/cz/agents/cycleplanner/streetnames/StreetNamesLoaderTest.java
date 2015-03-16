package cz.agents.cycleplanner.streetnames;

import org.junit.Assert;
import org.junit.Test;

public class StreetNamesLoaderTest {

	@Test
	public void test() {
		Assert.assertNotNull(StreetNamesLoader.getStreetNamesStream("prague"));
		Assert.assertNotNull(StreetNamesLoader.getStreetNamesStream("brno"));
		Assert.assertNotNull(StreetNamesLoader.getStreetNamesStream("plzen"));
		Assert.assertNotNull(StreetNamesLoader.getStreetNamesStream("ceske_budejovice"));
		Assert.assertNotNull(StreetNamesLoader.getStreetNamesStream("hradec_kralove"));
		Assert.assertNotNull(StreetNamesLoader.getStreetNamesStream("pardubice"));
	}
}
