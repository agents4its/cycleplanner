package cz.agents.cycleplanner.mongodb;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;

import cz.agents.cycleplanner.api.datamodel.feedback.CycleplannerFeedback;

/**
 * Implementation of bicycle journey plan storage that is using mongo db at CVUT
 * 
 * @author Jan Hrncir (CVUT)
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 */
public class BicycleJourneyPlanStorage {

	private static final Logger log = Logger.getLogger(BicycleJourneyPlanStorage.class);
	private static String connectionDetails = "";
	private static BicycleJourneyPlanStorage storage = null;

	private static final String FEEDBACK_COLLECTION = "feedback";
	
//	private static final String RESPONSES_COLLECTION = "responses";
//	private static final String LASTID_COLLECTION = "lastid";

	private DB db = null;
	private ObjectMapper mapper = null;

	private BicycleJourneyPlanStorage() {

		try {
			// initialize connection to Mongo DB
			db = StorageMongoDbConnectionProvider.getConnection();

			// remember connection details
			connectionDetails = StorageMongoDbConnectionProvider.getDbConnectionDetails();

			// TODO create indexes, when we start frequently reading from
			// mongoDB
			// example (in mongoDB v3 is ensureIndex deprecated):
			// db.getCollection(RESPONSES_COLLECTION).ensureIndex(new
			// BasicDBObject("journeyResponse.responseID", 1),
			// "PK_responseID", true);


			// initialize mapper
			mapper = new ObjectMapper();
			mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			
			log.info(String.format("JourneyPlanStorage based on %s successfully initialized.",
					StorageMongoDbConnectionProvider.getDbConnectionDetails()));

		} catch (Exception e) {

			log.error(String.format("JourneyPlanStorage initialization based on %s failed.",
					StorageMongoDbConnectionProvider.getDbConnectionDetails()), e);
		}
	}

	/**
	 * Get a singleton instance of a storage
	 * 
	 * @return Journey plan storage
	 */
	public static BicycleJourneyPlanStorage getStorage() {

		if ((storage == null) || (!connectionDetails.equals(StorageMongoDbConnectionProvider.getDbConnectionDetails()))) {

			BicycleJourneyPlanStorage newStorage = new BicycleJourneyPlanStorage();
			if (newStorage.isConnected()) {
				storage = newStorage;
			} else {
				storage = null;
			}
		}

		return storage;
	}

	/**
	 * 
	 */
	public boolean isConnected() {

		if (db != null) {
			return db.collectionExists(FEEDBACK_COLLECTION);
		}

		return false;
	}
	
	/**
	 * 
	 * TODO javadoc
	 * 
	 * @param feedback
	 * @return
	 */
	public boolean storeCyclePlannerFeedback(CycleplannerFeedback feedback) {

		return MongoUtils.storeObjectInCollection(feedback, db, FEEDBACK_COLLECTION);
	}

//	/**
//	 * 
//	 */
//	public boolean storeJourneyRequestAndResponse(JourneyRequestAndResponse journeyRequestAndResponse) {
//		boolean success = false;
//		String json;
//
//		try {
//			json = mapper.writeValueAsString(journeyRequestAndResponse);
//			DBObject dbobject = (DBObject) JSON.parse(json);
//			WriteResult wr = db.getCollection(RESPONSES_COLLECTION).insert(dbobject);
//			success = ((double) wr.getField("ok") == 1.0);
//
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//
//		return success;
//	}
//
//	/**
//	 * 
//	 */
//	public List<JourneyRequestAndResponse> retrieveAllManagedJourneys() {
//		List<JourneyRequestAndResponse> toReturn = new ArrayList<>();
//		try {
//			for (DBObject obj : db.getCollection(MANAGED_PLANS_COLLECTION).find()) {
//				toReturn.add(retrieveJourneyRequestAndResponse((int) obj.get("responseID")));
//			}
//		} catch (Exception e) {
//			log.error("An error has occurred during retrieving details from the DB!", e);
//			toReturn.clear();
//		}
//		return toReturn;
//	}
//
//	/**
//	 * 
//	 */
//	public boolean storeManagedJourneyPlan(JourneyPlanID id) {
//
//		return MongoUtils.storeObjectInCollection(id, db, MANAGED_PLANS_COLLECTION);
//	}
//
//	/**
//	 * 
//	 */
//	public boolean removeManagedJourneyPlan(JourneyPlanID id) {
//		if (id == null) {
//			return false;
//		}
//		try {
//			DBCollection managedPlans = db.getCollection(MANAGED_PLANS_COLLECTION);
//			WriteResult result = managedPlans.remove(new BasicDBObject("responseID", id.getResponseID()));
//			return ((double) result.getField("ok") == 1.0);
//		} catch (Exception e) {
//			log.error("An error has occurred during writing to the DB!", e);
//			return false;
//		}
//	}
//
//	/**
//	 * 
//	 */
//	public JourneyLeg retrieveJourneyLeg(long responseID, int journeyPlanID, int journeyLegID) {
//
//		JourneyLeg leg = null;
//		JourneyPlan plan = this.retrieveJourneyPlan(responseID, journeyPlanID);
//
//		if (plan != null) {
//			leg = plan.getJourneyLegByID(journeyLegID);
//		}
//
//		return leg;
//	}
//
//	/**
//	 * 
//	 */
//	public JourneyPlan retrieveJourneyPlan(long responseID, int journeyPlanID) {
//
//		JourneyPlan plan = null;
//		JourneyResponse response = this.retrieveJourneyResponse(responseID);
//
//		if (response != null) {
//			plan = response.getJourneyPlanByID(journeyPlanID);
//		}
//
//		return plan;
//	}
//
//	/**
//	 * 
//	 */
//	public JourneyResponse retrieveJourneyResponse(long responseID) {
//
//		JourneyResponse response = null;
//		JourneyRequestAndResponse requestAndResponse = this.retrieveJourneyRequestAndResponse(responseID);
//
//		if (requestAndResponse != null) {
//			response = requestAndResponse.getJourneyResponse();
//		}
//
//		return response;
//	}
//
//	/**
//	 * 
//	 */
//	public JourneyRequestAndResponse retrieveJourneyRequestAndResponse(long responseID) {
//
//		BasicDBObject query = new BasicDBObject("journeyResponse.responseID", responseID);
//		DBCursor cursor = db.getCollection(RESPONSES_COLLECTION).find(query);
//		DBObject response = null;
//		JourneyRequestAndResponse requestAndResponse = null;
//
//		try {
//			if (cursor.hasNext()) {
//				response = cursor.next();
//			}
//		} finally {
//			cursor.close();
//		}
//
//		if (response != null) {
//			requestAndResponse = MongoUtils.convertMongoDbObjectToJava(response, JourneyRequestAndResponse.class);
//		}
//
//		return requestAndResponse;
//	}

	/**
	 * 
	 */
	// TODO retrieve last responseID
	// Method below is only example taken from SUPERHUB
//	public long newJourneyResponseID() {
//
//		long newID = 1;
//
//		// Approach using a special collection to store last id & largest stored
//		// id
//		DBObject lastIDfound = db.getCollection(LASTID_COLLECTION).findOne();
//		WriteResult wr = null;
//		// boolean success = false;
//		Integer largestResponseIdInCollection = 0;
//
//		// check largest ID in the responses collection
//		DBCursor cursor = db.getCollection(RESPONSES_COLLECTION).find()
//				.sort(new BasicDBObject("journeyResponse.responseID", -1));
//		if (cursor.hasNext()) {
//			largestResponseIdInCollection = (Integer) ((BSONObject) cursor.next().get("journeyResponse"))
//					.get("responseID");
//		}
//
//		if (lastIDfound != null) {
//
//			Integer lastID = (Integer) ((BSONObject) lastIDfound).get("lastid");
//			if (largestResponseIdInCollection != null) {
//				lastID = Math.max(largestResponseIdInCollection, lastID);
//			}
//
//			newID = lastID + 1;
//			DBObject newLastID = (DBObject) JSON.parse("{'lastid':" + newID + "}");
//			wr = db.getCollection(LASTID_COLLECTION).update(lastIDfound, newLastID);
//			// success = ((double) wr.getField("ok") == 1.0);
//			// log.debug("last id = " + lastID + ", new id = " + newID +
//			// ", successfully written = " + success);
//
//		} else {
//
//			if (largestResponseIdInCollection != null) {
//				newID = largestResponseIdInCollection + 1;
//			}
//			wr = db.getCollection(LASTID_COLLECTION).insert((DBObject) JSON.parse("{'lastid':" + newID + "}"));
//			// success = ((double) wr.getField("ok") == 1.0);
//			// log.debug("last id initialized to " + newID +
//			// ", successfully written = " + success);
//		}
//
//		return newID;
//	}

//	/**
//	 * 
//	 */
//	public JourneyResponse retrieveJourneyResponse(JourneyPlanStorageRequest journeyPlanStorageRequest) {
//
//		long responseID = journeyPlanStorageRequest.getResponseID();
//		Integer journeyPlanID = journeyPlanStorageRequest.getJourneyPlanID();
//		boolean planFilteredSuccessfully = false;
//		Integer journeyLegID = journeyPlanStorageRequest.getJourneyLegID();
//		JourneyResponse response = retrieveJourneyResponse(responseID);
//
//		if (response != null) {
//
//			// filter journey plan
//			if ((journeyPlanID != null) && (response.getJourneyPlanByID(journeyPlanID) != null)) {
//				response = JourneyResponse.createResponseWithOnePlan(response, journeyPlanID);
//				planFilteredSuccessfully = true;
//			}
//
//			// filter journey leg
//			if ((journeyLegID != null) && (planFilteredSuccessfully)
//					&& (response.getFirstJourneyPlan().getJourneyLegByID(journeyLegID) != null)) {
//				response = JourneyResponse.createOnePlanResponseWithOneLeg(response, journeyLegID);
//			}
//
//			// filter POIs
//			if (!journeyPlanStorageRequest.isIncludePOIs()) {
//				response = JourneyResponse.removePOIsFromResponse(response);
//			}
//
//			// filter Atomic travel actions
//			if (!journeyPlanStorageRequest.isIncludeAtomicTravelActions()) {
//				response = JourneyResponse.removeATAsFromResponse(response);
//			}
//
//			// return response
//			return response;
//		}
//
//		return null;
//	}
//
//	/**
//	 * 
//	 */
//	public long countStoredResponses() {
//		return db.getCollection(RESPONSES_COLLECTION).count();
//	}
//
//	/**
//	 * Drop all data from testing db, i.e., reset testing db
//	 */
//	public void dropAllDataFromTestDb() {
//
//		if (db.getName().equals("wp5test")) {
//
//			// responses
//			db.getCollection(RESPONSES_COLLECTION).drop();
//			db.createCollection(RESPONSES_COLLECTION, null);
//
//			// managed
//			db.getCollection(MANAGED_PLANS_COLLECTION).drop();
//			db.createCollection(MANAGED_PLANS_COLLECTION, null);
//
//			// lastid
//			db.getCollection(LASTID_COLLECTION).drop();
//			db.createCollection(LASTID_COLLECTION, null);
//		}
//	}
//
//	public long countAllManagedJourneys() {
//
//		return db.getCollection(MANAGED_PLANS_COLLECTION).count();
//	}
}
