package cz.agents.cycleplanner.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import eu.superhub.wp5.journeyplandatamodel.request.JourneyRequest;
import eu.superhub.wp5.journeyplandatamodel.response.JourneyResponse;

/**
 * Utils for JSON serialization and deserialization
 * 
 * @author Jan Hrncir (CVUT)
 */
public class JSONUtils {

	private static final Logger log = Logger.getLogger(JSONUtils.class);

	/**
	 * Load journey response from JSON file
	 * 
	 * @param fileName
	 *            File name
	 * @return Journey Response
	 */
	@Deprecated
	public static JourneyResponse loadJourneyResponseFromJSONFile(String fileName) {

		// read file
		String jsonString;
		try {
			jsonString = Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
		} catch (Exception e) {
			log.error(fileName + " resource not found.");
			return null;
		}

		// JSON -> JAVA
		JourneyResponse response;
		try {
			ObjectMapper mapper = new ObjectMapper();
			response = mapper.readValue(jsonString, JourneyResponse.class);
		} catch (IOException e) {
			log.error(fileName + ": exception in converting from JSON to JAVA.");
			return null;
		}

		return response;
	}

	/**
	 * Save Java object as JSON to file
	 * 
	 * @param object
	 *            Java object
	 * @param fileName
	 *            File name
	 * @throws JsonProcessingException
	 * @throws FileNotFoundException
	 */
	@Deprecated
	public static void saveAsJSONToFile(Object object, String fileName) throws JsonProcessingException,
			FileNotFoundException {

		// serialize Java object to JSON string
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String jsonString = mapper.writeValueAsString(object);

		// save to a file
		PrintWriter out = new PrintWriter(fileName);
		out.println(jsonString);
		out.close();
	}

	/**
	 * Convert Java object to JSON
	 * 
	 * @param object
	 *            Java object
	 * @return Java object as JSON
	 */
	public static String javaObjectToJson(Object object) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			log.error("Cannot convert " + object.getClass().getSimpleName() + " to JSON string.", e);
			return null;
		}
	}

	/**
	 * Convert JSON to java <code>Object</code>
	 * TODO javadoc
	 * 
	 * @param json
	 * @param javaClass
	 * @return
	 */
	public static <T> T jsonToJavaObject(String json, Class<T> javaClass) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.readValue(json, javaClass);
		} catch (IOException e) {
			log.error("Error in converting " + json + " to Java object " + javaClass.getSimpleName(), e);
			return null;
		}
	}

	/**
	 * Convert JSON to JourneyRequest
	 * 
	 * @param json
	 *            Journey request in JSON
	 * @return Journey Request
	 */
	@Deprecated
	public static JourneyRequest convertJSONToJourneyRequest(String json) {

		// JSON -> JAVA
		JourneyRequest request = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			request = mapper.readValue(json, JourneyRequest.class);
		} catch (IOException e) {
			log.error("Exception in converting journey request from JSON to JAVA.");
			return null;
		}
		return request;
	}
}
