package cz.agents.cycleplanner.experiment;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * A results' visualization in web browser using front-end of
 * {@link cykloplanovac.cz}
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
// TODO finish ---> takes results and open it in browser
public class ExperimentVisualisation {
	private static Logger log = Logger.getLogger(ExperimentVisualisation.class);

	private static void openChromeBrowser() {
		try {
			Runtime.getRuntime().exec(
					"C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome --disable-web-security");
		} catch (IOException e) {
			log.error("Could not open chrome!", e);
		}
	}

	// Automatic render code, not yet fully finished
	// openChromeBrowser();
	// PrintStream c;
	// File jsonFile = new File("response_" + i + ".json");
	// c = new PrintStream(jsonFile);
	// c.print(JSONUtils.javaObjectToJSON(response));
	// c.close();
	//
	// File f = new File("template.html");
	// log.info(f.exists());
	//
	// BufferedReader reader = new BufferedReader(new
	// FileReader(f));
	// StringBuffer sb = new StringBuffer();
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// sb.append(line);
	// sb.append("\n");
	// }
	//
	// reader.close();
	//
	// String html = sb.toString();
	// log.info(html);
	//
	// Template tmpl =
	// Mustache.compiler().escapeHTML(false).compile(html);
	// Map<String, String> data = new HashMap<String, String>();
	// data.put("title", "Reponse " + i);
	// data.put("json", "\"response_" + i + ".json\"");
	//
	// File htmlFile = new File("response_" + i + ".html");
	// c = new PrintStream(htmlFile);
	// c.print(tmpl.execute(data));
	// c.close();
	//
	// Desktop.getDesktop().browse(htmlFile.toURI());
}
