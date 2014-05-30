/*
Copyright 2013 Marcel Német

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package cvut.fel.nemetma1.evaluate.evaluator;

import au.com.bytecode.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class loads and holds parameters of OSM tags (multipliers and slowdown constants)
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class ParametersOfRelevantTags {

    private HashMap<String, double[]> parameters;

    /**
     * loads multipliers and slowdown constants from a CSV file
     *
     * @param parametersCSV CSV file from which the values are loaded
     * @param entityRow a number of row in CSV which contains names of entities ("relation" or "way" or "node")
     * @param keyRow a number of row in CSV which contains OSM key (e.g. "highway" or "access")
     * @param valueRow a number of row in CSV which contains OSM value
     * @param coefficientRow a number of row in CSV which contains tag multiplier value for given tag
     * @param constantRow a number of row in CSV which contains tag slowdown constant value for given tag
     */
    public ParametersOfRelevantTags(File parametersCSV, int entityRow, int keyRow, int valueRow, int coefficientRow, int constantRow) {
        this(parametersCSV, entityRow, keyRow, valueRow, coefficientRow, constantRow, ';', '"', true);
    }

    /**
     * loads multipliers and slowdown constants from a CSV file
     *
     * @param parametersCSV CSV file from which the values are loaded
     * @param entityRow a number of row in CSV which contains names of entities ("relation" or "way" or "node")
     * @param keyRow a number of row in CSV which contains OSM key (e.g. "highway" or "access")
     * @param valueRow a number of row in CSV which contains OSM value
     * @param coefficientRow a number of row in CSV which contains tag multiplier value for given tag
     * @param constantRow a number of row in CSV which contains tag slowdown constant value for given tag
     * @param separator column separator character
     * @param quotechar quoting character
     * @param skipFirstRow if true, first row is skipped (use if table has header)
     */
    public ParametersOfRelevantTags(File parametersCSV, int entityRow, int keyRow, int valueRow, int coefficientRow, int constantRow, char separator, char quotechar, boolean skipFirstRow) {
        parameters = new HashMap<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(parametersCSV), separator, quotechar);
            String[] nextLine;
            boolean first = true;
            while ((nextLine = reader.readNext()) != null) {
                if (first && skipFirstRow) {
                    first = false;
                    continue;
                }
                String entityKeyValueJoined = nextLine[entityRow] + "::" + nextLine[keyRow] + "::" + nextLine[valueRow];
                double coef = Double.parseDouble(nextLine[coefficientRow]);
                double constant = Double.parseDouble(nextLine[constantRow]);

                double[] d = {coef, constant};
                parameters.put(entityKeyValueJoined, d);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParametersOfRelevantTags.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParametersOfRelevantTags.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * returns a tag multiplier value for an OSM tag
     *
     * @param entity "relation", "way" or "node"
     * @param key OSM tag key
     * @param value OSM tag value
     * @return multiplier value
     */
    public double getMultiplierForEntityKeyValue(String entity, String key, String value) {
        String keyValueJoined = entity + "::" + key + "::" + value;
        return parameters.get(keyValueJoined)[0];
    }

    /**
     * returns a tag multiplier value for an OSM tag entityKeyValue argument must be a string in format "entity::key::value" where entity is
     * "relation", "way" or "node", key is an OSM tag key, value is an OSM tag value.
     *
     * @param entityKeyValue
     * @return a tag multiplier value
     */
    public double getMultiplierForEntityKeyValue(String entityKeyValue) {
        String keyValueJoined = entityKeyValue;
        return parameters.get(keyValueJoined)[0];
    }

    /**
     * returns a tag slowdown constant value for an OSM tag
     *
     * @param entity "relation", "way" or "node"
     * @param key OSM tag key
     * @param value OSM tag value
     * @return multiplier value
     */
    public double getConstantForEntityKeyValue(String entity, String key, String value) {
        String keyValueJoined = entity + "::" + key + "::" + value;
        return parameters.get(keyValueJoined)[1];
    }

    /**
     * returns a tag slowdown constant value for an OSM tag entityKeyValue argument must be a string in format "entity::key::value" where entity is
     * "relation", "way" or "node", key is an OSM tag key, value is an OSM tag value.
     *
     * @param entityKeyValue
     * @return a tag slowdown constant value
     */
    public double getConstantForEntityKeyValue(String entityKeyValue) {
        String keyValueJoined = entityKeyValue;
        return parameters.get(keyValueJoined)[1];
    }

    /**
     * returns true if parameters contain an OSM tag. entityKeyValue argument must be a string in format "entity::key::value" where entity is
     * "relation", "way" or "node", key is an OSM tag key, value is an OSM tag value.
     *
     * @param entityKeyValue string representing an OSM tag
     * @return true if parameters contain an OSM tag, otherwise flase
     */
    public boolean containsEntityKeyValue(String entityKeyValue) {
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
}
