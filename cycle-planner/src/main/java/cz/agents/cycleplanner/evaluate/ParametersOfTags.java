package cz.agents.cycleplanner.evaluate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;
import cz.agents.cycleplanner.routingService.ResourceToFile;

//TODO javadoc
/**
 * Load all parameters for each tag from external csv file.
 * 
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 *
 */
public enum ParametersOfTags {
	
	INSTANCE;
	public int ENTITY_COL = 1;
	public int KEY_COL = 2;
	public int VALUE_COL = 3;
	
	public int TRAVEL_TIME_MULTIPLIER_CSV_COL = 4;
	public int TRAVEL_TIME_SLOWDOWN_CONSTANT_CSV_COL = 5;
	public int COMFORT_MULTIPLIER_CSV_COL = 6;
	public int QUIETNESS_MULTIPLIER_CSV_COL = 7;
	
	private int TRAVEL_TIME_MULTIPLIER_ARRAY_INDEX = 0;
	private int TRAVEL_TIME_SLOWDOWN_CONSTANT_ARRAY_INDEX = 1;
	private int COMFORT_MULTIPLIER_ARRAY_INDEX = 2;
	private int QUIETNESS_MULTIPLIER_ARRAY_INDEX = 3;
	
	private final String PATH_TO_CSV_FILE = "/feature_values.csv";
	private final char SEPARATOR = ';';
	private final char QUOTECHAR = '"';
		
	private HashMap<String, double[]> parameters;

    private ParametersOfTags() {
        parameters = new HashMap<>();
        try {

            File f = ResourceToFile.getFileFromResource(PATH_TO_CSV_FILE);
            
            CSVReader reader = new CSVReader(new FileReader(f), SEPARATOR, QUOTECHAR);
            String[] nextLine;
            boolean first = true;
            
            while ((nextLine = reader.readNext()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                
                String entityKeyValueJoined = nextLine[ENTITY_COL] + "::" + nextLine[KEY_COL] + "::" + nextLine[VALUE_COL];
                
                double[] d = new double[4];
                d[TRAVEL_TIME_MULTIPLIER_ARRAY_INDEX] = Double.parseDouble(nextLine[TRAVEL_TIME_MULTIPLIER_CSV_COL]);
                d[TRAVEL_TIME_SLOWDOWN_CONSTANT_ARRAY_INDEX] = Double.parseDouble(nextLine[TRAVEL_TIME_SLOWDOWN_CONSTANT_CSV_COL]);
                d[COMFORT_MULTIPLIER_ARRAY_INDEX] = Double.parseDouble(nextLine[COMFORT_MULTIPLIER_CSV_COL]);
                d[QUIETNESS_MULTIPLIER_ARRAY_INDEX] = Double.parseDouble(nextLine[QUIETNESS_MULTIPLIER_CSV_COL]);
                
                parameters.put(entityKeyValueJoined, d);
            }
            
            reader.close();
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParametersOfTags.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParametersOfTags.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }
    
    public double getTravelTimeMultiplier(String entity, String key, String value) {
    	String entityKeyValue = joinTags(entity, key, value);
    	return getTravelTimeMultiplier(entityKeyValue);
    }
    
    public double getTravelTimeMultiplier(String entityKeyValue) {
    	return parameters.get(entityKeyValue)[TRAVEL_TIME_MULTIPLIER_ARRAY_INDEX];
    }
    
    public double getTravelTimeSlowdownConstant(String entity, String key, String value) {
    	String entityKeyValue = joinTags(entity, key, value);
    	return getTravelTimeSlowdownConstant(entityKeyValue);
    }
    
    public double getTravelTimeSlowdownConstant(String entityKeyValue) {
    	return parameters.get(entityKeyValue)[TRAVEL_TIME_SLOWDOWN_CONSTANT_ARRAY_INDEX];
    }
    
    public double getComfortMultiplier(String entity, String key, String value) {
    	String entityKeyValue = joinTags(entity, key, value);
    	return getComfortMultiplier(entityKeyValue);
    }
    
    public double getComfortMultiplier(String entityKeyValue) {
    	return parameters.get(entityKeyValue)[COMFORT_MULTIPLIER_ARRAY_INDEX];
    }
    
    public double getQuietnessMultiplier(String entity, String key, String value) {
    	String entityKeyValue = joinTags(entity, key, value);
    	return getQuietnessMultiplier(entityKeyValue);
    }
    
    public double getQuietnessMultiplier(String entityKeyValue) {
    	return parameters.get(entityKeyValue)[QUIETNESS_MULTIPLIER_ARRAY_INDEX];
    }

    
    /**
     * returns true if parameters contain an OSM tag. Where entity is
     * "relation", "way" or "node", key is an OSM tag key, value is an OSM tag value.
     *
     * @param entityKeyValue string representing an OSM tag
     * @return true if parameters contain an OSM tag, otherwise flase
     */
    public boolean contains(String entity, String key, String value) {
    	String entityKeyValue = joinTags(entity, key, value);
    	return contains(entityKeyValue);
    }
    
    /**
     * returns true if parameters contain an OSM tag. entityKeyValue argument must be a string in format "entity::key::value" where entity is
     * "relation", "way" or "node", key is an OSM tag key, value is an OSM tag value.
     *
     * @param entityKeyValue string representing an OSM tag
     * @return true if parameters contain an OSM tag, otherwise flase
     */
    public boolean contains(String entityKeyValue) {
        return parameters.containsKey(entityKeyValue);
    }
    
    /**
     * returns set of all OSM tags as Strings in format "entity::key::value" where entity is "relation", "way" or "node", key is an OSM tag key, value
     * is an OSM tag value.
     *
     * @return list of OSM tags as strings
     */
    public Set<String> getParametersKeySet() {
        return parameters.keySet();
    }
    
    private String joinTags(String entity, String key, String value) {
    	StringBuilder sb = new StringBuilder();
    	sb.append(entity);
    	sb.append("::");
    	sb.append(key);
    	sb.append("::");
    	sb.append(value);
    	
    	return sb.toString();
    }
}
