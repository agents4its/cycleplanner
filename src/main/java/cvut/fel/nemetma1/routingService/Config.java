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
package cvut.fel.nemetma1.routingService;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Marcel Német <marcel.nemet@gmail.com>
 */
public class Config {

	private final static String PATH_TO_OSM_FILE = "/praha_medium_completeways_srtm-2013-20-04.osm";
	private final static String JAVAOBJECT_FILE_APPENDIX = ".oneways.tags.javaobject";

    private Properties prop;

    public Config() {

    	prop = new Properties();
		try {
			File osm = ResourceToFile.getFileFromResource(PATH_TO_OSM_FILE);
			File g = ResourceToFile.getFileFromResource(PATH_TO_OSM_FILE
					+ JAVAOBJECT_FILE_APPENDIX);
			
			// set the properties value
			prop.setProperty("osm_file", osm.getAbsolutePath());
			prop.setProperty("osm_graphobject_file", g.getAbsolutePath());
			prop.setProperty("recreate_graph_on_startup", "false");
			prop.setProperty("strip_osm_tags", "false");

			System.out.println(prop.getProperty("osm_file"));
			System.out.println(prop.getProperty("osm_graphobject_file"));
			System.out.println(prop.getProperty("recreate_graph_on_startup"));
			System.out.println(prop.getProperty("strip_osm_tags"));

		} catch (IOException ex) {
			ex.printStackTrace();
		}
    }

    public String getOSMFilePath() {
        return prop.getProperty("osm_file");
    }

    public String getGraphObjectFilePath() {
        return prop.getProperty("osm_graphobject_file");
    }

    public boolean isRecreateGraph() {
        if (prop.getProperty("recreate_graph_on_startup").equals("true")) {
            return true;
        }
        return false;
    }

    public boolean isdestroyTags() {
        if (prop.getProperty("strip_osm_tags").equals("true")) {
            return true;
        }
        return false;
    }
}
