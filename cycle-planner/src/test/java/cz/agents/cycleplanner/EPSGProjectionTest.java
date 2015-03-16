package cz.agents.cycleplanner;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import cz.agents.cycleplanner.junctions.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class EPSGProjectionTest {
	private final static Logger log = Logger.getLogger(EPSGProjectionTest.class);
	private EPSGProjection projection;
	@Before
	public void setUp() throws Exception {
		try {
			projection = new EPSGProjection(2065);
		} catch (FactoryException | TransformException e) {
			log.error(e.getMessage(), e.fillInStackTrace());
		}
	}

	@Test
	public void test() {
		GPSLocation gps = new GPSLocation(50077319, 14424638);
		GPSLocation projected = projection.getProjectedGPSLocation(gps);
		GPSLocation newGps = projection.getSphericalGPSLocation(projected);

		Assert.assertEquals(gps.getLatitudeE6(), newGps.getLatitudeE6());
		Assert.assertEquals(gps.getLongitudeE6(), newGps.getLongitudeE6());

	}

}
