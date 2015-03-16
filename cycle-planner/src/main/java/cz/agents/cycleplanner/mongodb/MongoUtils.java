package cz.agents.cycleplanner.mongodb;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

public class MongoUtils {

	private static final Logger log = Logger.getLogger(MongoUtils.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	/**
	 * Store java object in Mongo DB collection
	 * 
	 * @param object
	 *            Java object
	 * @param db
	 *            Mongo database connection
	 * @param collection
	 *            Mongo DB collection name
	 * @return True if successfully stored
	 */
	public static boolean storeObjectInCollection(Object object, DB db, String collection) {
		boolean success = false;
		String json;
		try {

			json = mapper.writeValueAsString(object);
			DBObject dbObject = (DBObject) JSON.parse(json);
			WriteResult wr = db.getCollection(collection).insert(dbObject);
			success = ((double) wr.getField("ok") == 1.0);

		} catch (Exception e) {
			log.error("An error has occurred during writing object to the DB: " + object.toString(), e);
		}
		return success;
	}

	/**
	 * Convert mongo db object to Java object
	 * 
	 * @param dbObject
	 *            Mongo db object
	 * @param javaClass
	 *            Java class
	 * @return Java object
	 */
	public static <T> T convertMongoDbObjectToJava(DBObject dbObject, Class<T> javaClass) {

		dbObject.removeField("_id");
		String jsonFromMongo = JSON.serialize(dbObject);
		try {
			return mapper.readValue(jsonFromMongo, javaClass);
		} catch (IOException e) {
			log.error("Error in converting " + dbObject.toString() + " to Java object " + javaClass.getSimpleName(), e);
			return null;
		}
	}
}
