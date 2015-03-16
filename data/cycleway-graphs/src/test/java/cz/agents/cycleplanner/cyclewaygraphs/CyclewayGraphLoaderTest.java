package cz.agents.cycleplanner.cyclewaygraphs;

import org.junit.Assert;
import org.junit.Test;

public class CyclewayGraphLoaderTest {

	@Test
	public void test() {
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("prague"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("prague-medium-A"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("prague-medium-B"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("prague-medium-C"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("prague-small"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("brno"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("plzen"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("ceske-budejovice"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("hradec-kralove"));
		Assert.assertNotNull(CyclewayGraphLoader.getCyclewayGraphStream("pardubice"));
	}
}
