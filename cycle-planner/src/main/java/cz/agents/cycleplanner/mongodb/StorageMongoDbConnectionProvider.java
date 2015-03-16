package cz.agents.cycleplanner.mongodb;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class StorageMongoDbConnectionProvider {

	private static final Logger log = Logger.getLogger(StorageMongoDbConnectionProvider.class);

	/**
	 * Frequently used connection details
	 */
	private static final String MONGO_CVUT_HOST = "its.felk.cvut.cz";
	private static final int MONGO_CVUT_PORT = 27017;
	private static final String MONGO_CVUT_DB = "cycleplanner";
//	private static final String MONGO_CVUT_DB_TEST = "wp5test";
	private static final String MONGO_CVUT_USER = "cycleplanner";
	private static final String MONGO_CVUT_PASSWD = "cycleJourneyPlanner";

	// Backup server for mongo db
	// private static final String MONGO_CVUT_HOST = "147.32.83.181";
	// private static final int MONGO_CVUT_PORT = 8000;

	/**
	 * DB Connection details
	 */
	private static String host = null;
	private static Integer port = null;
	private static String dbName = null;
	private static String user = null;
	private static String passwd = null;

	/**
	 * Cached connection to database
	 */
	private static MongoClient mongoClient = null;
	private static DB dbConnection = null;

	/**
	 * Get Mongo db connection. If the connection not exists, a new one is
	 * created. If the connection exists, it is returned.
	 * 
	 * @return Mongo db connection
	 */
	public static DB getConnection() {

		if (dbConnection == null) {

			MongoClient client = null;
			DB db = null;
			boolean authenticated = false;
			try {
				mongoClient = new MongoClient(host, port);
				db = mongoClient.getDB(dbName);
				authenticated = db.authenticate(user, passwd.toCharArray());
				if (authenticated) {
					log.info(String.format("Mongo connection %s OK.", getDbConnectionDetails()));
					dbConnection = db;
					mongoClient = client;
				} else {
					log.error(String.format("Mongo authentication %s FAILED.", getDbConnectionDetails()));
					dbConnection = null;
				}
			} catch (Exception e) {
				log.error(String.format("Mongo connection %s FAILED.", getDbConnectionDetails()), e);
				dbConnection = null;
			}
		}

		return dbConnection;
	}

	public static String getDbConnectionDetails() {
		return String.format("%s@%s:%d#%s", user, host, port, dbName);
	}

	public static void setDbConnection(DB db) {

		// set db connection
		dbConnection = db;

		// set connection details
		dbName = db.getName();
		host = db.getMongo().getAddress().getHost();
		port = db.getMongo().getAddress().getPort();
		try {
			CommandResult result = db.command(new BasicDBObject("connectionStatus", 1));
			user = (String) ((DBObject) ((DBObject) ((DBObject) result.get("authInfo")).get("authenticatedUsers"))
					.get("0")).get("user");
		} catch (NullPointerException e) {
			user = "noauth";
		}
	}

	public static void setDbConnectionDetails(String pHost, Integer pPort, String pDbName, String pDbUser,
			String pDbPasswd) {
		host = pHost;
		port = pPort;
		dbName = pDbName;
		user = pDbUser;
		passwd = pDbPasswd;
		dbConnection = null;
		if (mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
	}

//	public static void setCvutTestingDbConnectionDetails() {
//		setDbConnectionDetails(MONGO_CVUT_HOST, MONGO_CVUT_PORT, MONGO_CVUT_DB_TEST, MONGO_CVUT_USER, MONGO_CVUT_PASSWD);
//	}

	public static void setCvutProductionDbConnectionDetails() {
		setDbConnectionDetails(MONGO_CVUT_HOST, MONGO_CVUT_PORT, MONGO_CVUT_DB, MONGO_CVUT_USER, MONGO_CVUT_PASSWD);
	}
}
