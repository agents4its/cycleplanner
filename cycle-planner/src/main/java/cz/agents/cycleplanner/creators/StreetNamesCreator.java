package cz.agents.cycleplanner.creators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.data.CityCycleData;
import cz.agents.cycleplanner.plannerDataImporterExtensions.RelationAndWayOsmImporter;
import cz.agents.cycleplanner.plannerDataImporterExtensions.osmBinder.RelationAndWayBicycleGraphOsmBinder;
import cz.agents.cycleplanner.plannerDataImporterExtensions.tasks.impl.StreetNamesImportTask;
import cz.agents.cycleplanner.routingService.City;
import eu.superhub.wp5.plannerdataimporter.graphimporter.OsmDataGetter;

public class StreetNamesCreator implements Creator {

	private final static Logger log = Logger.getLogger(StreetNamesCreator.class);

	@Override
	public void create(City city) {
		CityCycleData data = CityCycleData.getDataForCity(city);
		saveStreetNamesToFile(createStreetNames(data.getOsmFile()), city.toString().toLowerCase());
	}

	private Map<Long, String> createStreetNames(File osm) {

		log.info("Importing street names....");

		OsmDataGetter osmDataGetter = OsmDataGetter.createOsmDataGetter(osm);
		RelationAndWayOsmImporter importer = new RelationAndWayOsmImporter(osmDataGetter);

		return importer.executeTaskForWayAndRelation(new StreetNamesImportTask(),
				RelationAndWayBicycleGraphOsmBinder.getSelector());
	}

	private void saveStreetNamesToFile(Map<Long, String> streetNames, String name) {
		log.info("Saving street names...");

		try {
			File f = new File(name + "_street_names.javaobject");
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(streetNames);
			oos.close();
		} catch (IOException ex) {
			log.error(ex.getMessage());
		}

		log.info("Street names saved.");
	}

	public static void main(String[] args) {
		StreetNamesCreator creator = new StreetNamesCreator();

		creator.create(City.PRAGUE);
		creator.create(City.BRNO);
		creator.create(City.CESKE_BUDEJOVICE);
		creator.create(City.HRADEC_KRALOVE);
		creator.create(City.PARDUBICE);
		creator.create(City.PLZEN);
	}
}
