package cz.agents.cycleplanner.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

/**
 * JSON schema utils
 * 
 * @author Jan Hrncir (CVUT)
 */
public class JSONSchemaUtils {

	public static void generateSchemaAndSaveToFile(Class classForSchema, String fileName)
			throws JsonProcessingException, FileNotFoundException {

		/**
		 * Jackson 1.x works only without required fields annotations
		 */
		// ObjectMapper mapper = new ObjectMapper();
		// mapper.setSerializationInclusion(Inclusion.NON_NULL);
		// JsonSchema jsonSchema = mapper.generateJsonSchema(JourneyPlan.class);
		// String schemaStr = jsonSchema.toString();
		// System.out.println(schemaStr);

		/**
		 * Jackson 2.x is able to produce JSON schema draft 3 (it can work with required fields)
		 * http://tools.ietf.org/id/draft-zyp-json-schema-03.html
		 */

		// generate schema
		ObjectMapper schemaMapper = new ObjectMapper();
		JsonSchemaGenerator generator = new JsonSchemaGenerator(schemaMapper);
		JsonSchema jsonSchema = generator.generateSchema(classForSchema);

		// serialize schema to string
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		String jsonSchemaString = mapper.writeValueAsString(jsonSchema);
		String schemaName = "\"$schema\": \"http://json-schema.org/draft-03/schema#\",";
		String jsonSchemaStringWithSchemaName = "{\n  " + schemaName + jsonSchemaString.substring(1);

		// save to a file
		PrintWriter out = new PrintWriter(fileName);
		out.println(jsonSchemaStringWithSchemaName);
		out.close();

		// print to stdout
		System.out.println(jsonSchemaStringWithSchemaName);
	}
}
