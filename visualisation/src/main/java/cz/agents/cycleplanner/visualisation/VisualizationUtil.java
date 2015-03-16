package cz.agents.cycleplanner.visualisation;

import java.net.MalformedURLException;
import java.sql.SQLException;

import javax.xml.transform.TransformerException;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import cz.agents.agentpolis.tools.geovisio.layer.BoundingBox;
import cz.agents.agentpolis.tools.geovisio.visualisation.VisualisationSettings;
import cz.agents.cycleplanner.data.CityCycleData;

public class VisualizationUtil {

	/**
	 * Visualisation settings
	 */
	public static final VisualisationSettings HONZA_NTB = new VisualisationSettings("localhost", 54321, "postgres", "",
			"medford", "http://localhost:8080/geoserver", "admin", "geovisio");
	public final static VisualisationSettings AGENT4ITS = new VisualisationSettings("its.felk.cvut.cz", 5432, "visio",
			"geovisio", "visio", "http://its.felk.cvut.cz:8080/geoserver", "admin", "geovisio");

	public static CycleGraphVisualisation initVisualisation(CityCycleData data, String datastoreNameSuffix) {

		final String datastoreName = createDatastoreName(data, datastoreNameSuffix);
		final BoundingBox boundingBox = createBoundingBox(data);

		try {
			CycleGraphVisualisation vis = new CycleGraphVisualisation(datastoreName, 4326, 900913, AGENT4ITS,
					boundingBox, new CycleGraphVisParameterMapper(), true);
			return vis;
		} catch (MalformedURLException | ClassNotFoundException | IllegalArgumentException | SQLException
				| FactoryException | TransformException | TransformerException e) {
			System.err.println("Visualization wasn't inited. " + e.getLocalizedMessage());
			e.printStackTrace(System.err);
			return null;
		}

	}

	private static BoundingBox createBoundingBox(CityCycleData data) {

		return new BoundingBox(data.getBoundingBox().getLeftE6() / 1E6, data.getBoundingBox().getBottomE6() / 1E6, data
				.getBoundingBox().getRightE6() / 1E6, data.getBoundingBox().getTopE6() / 1E6, 4326);
	}

	private static String createDatastoreName(CityCycleData data, String datastoreNameSuffix) {
		return data.getCity().toString().concat(datastoreNameSuffix);
	}

}
