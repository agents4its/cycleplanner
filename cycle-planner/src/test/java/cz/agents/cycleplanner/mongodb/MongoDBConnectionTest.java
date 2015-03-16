package cz.agents.cycleplanner.mongodb;

import static org.junit.Assert.*;

import org.junit.Test;

public class MongoDBConnectionTest {

	@Test
	public void test() {
		StorageMongoDbConnectionProvider.setCvutProductionDbConnectionDetails();

		BicycleJourneyPlanStorage bicycleJourneyPlanStorage = BicycleJourneyPlanStorage.getStorage();

		assertTrue(bicycleJourneyPlanStorage.isConnected());
	}

}
