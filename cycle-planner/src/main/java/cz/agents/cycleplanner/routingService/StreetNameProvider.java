package cz.agents.cycleplanner.routingService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

import org.apache.log4j.Logger;

public class StreetNameProvider {

	private static final Logger log = Logger.getLogger(StreetNameProvider.class);

	private Map<Long, String> streetNames;
	private File streetNamesFile;

	public StreetNameProvider(File streetNamesFile) {
		this.streetNamesFile = streetNamesFile;
	}

	public Map<Long, String> getStreetNames() {
		if (streetNames == null) {
			streetNames = loadStreetNames();
		}
		return streetNames;
	}

	@SuppressWarnings("unchecked")
	private Map<Long, String> loadStreetNames() {
		log.info("Loading street names.");

		if (streetNamesFile.exists()) {
			try {
				FileInputStream fis;
				fis = new FileInputStream(streetNamesFile);
				ObjectInputStream ois = new ObjectInputStream(fis);

				Map<Long, String> streetNames = (Map<Long, String>) ois.readObject();
				log.info("Street names loaded");
				ois.close();
				return streetNames;
			} catch (IOException | ClassNotFoundException ex) {
				log.error("Street names not loaded: \n" + ex.getMessage());
				return null;
			}
		} else {
			log.error("Street names not loaded.");
			return null;
		}
	}

}
