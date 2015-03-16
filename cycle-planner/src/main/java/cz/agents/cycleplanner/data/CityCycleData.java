package cz.agents.cycleplanner.data;

import java.io.File;

import cz.agents.cycleplanner.api.datamodel.BoundingBox;
import cz.agents.cycleplanner.routingService.City;


public class CityCycleData {
	
	protected File osmFile;
	
	protected String graphFileName;

	protected String streetNamesFileName;

	protected int top = -1;
	protected int left = -1;
	protected int right = -1;
	protected int bottom = -1;
	
	protected City city = City.OTHER;

	public File getOsmFile() {
		return osmFile;
	}

	public String getGraphFileName() {
		return graphFileName;
	}

	public String getStreetNamesFileName() {
		return streetNamesFileName;
	}

	public City getCity() {
		return city;
	}

	public BoundingBox getBoundingBox() {
		return new BoundingBox(left, top, right, bottom);
	}

	public boolean locationInside(double lat, double lon) {
		int latE6 = (int) (lat * 1E6);
		int lonE6 = (int) (lon * 1E6);

		return (left <= lonE6) && (lonE6 <= right) && (bottom <= latE6) && (latE6 <= top);
	}

	public static CityCycleData getDataForCity(City city) {
		CityCycleData data;

		switch (city) {
		case PRAGUE:
			data = new PragueCycleData();
			break;
		case PRAGUE_MEDIUM_A:
			data = new PragueMediumACycleData();
			break;
		case PRAGUE_MEDIUM_B:
			data = new PragueMediumBCycleData();
			break;
		case PRAGUE_MEDIUM_C:
			data = new PragueMediumCCycleData();
			break;
		case PRAGUE_SMALL:
			data = new PragueSmallCycleData();
			break;
		case BRNO:
			data = new BrnoCycleData();
			break;
		case CESKE_BUDEJOVICE:
			data = new CeskeBudejoviceCycleData();
			break;
		case HRADEC_KRALOVE:
			data = new HradecKraloveCycleData();
			break;
		case PARDUBICE:
			data = new PardubiceCycleData();
			break;
		case PLZEN:
			data = new PlzenCycleData();
			break;
		default:
			data = new CityCycleData();
		}

		return data;
	}

}
