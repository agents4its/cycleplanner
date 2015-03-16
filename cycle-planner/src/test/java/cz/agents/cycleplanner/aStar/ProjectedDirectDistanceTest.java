package cz.agents.cycleplanner.aStar;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math.util.FastMath;
import org.openstreetmap.osm.data.coordinates.LatLon;

import cz.agents.cycleplanner.util.GeoCalculationsHelper;
import eu.superhub.wp5.plannerdataimporter.util.EPSGProjection;
import eu.superhub.wp5.wp5common.location.GPSLocation;

public class ProjectedDirectDistanceTest {
	private static ArrayList<GPSLocation> starts = new ArrayList<GPSLocation>();
	private static ArrayList<GPSLocation> ends = new ArrayList<GPSLocation>();
	private static EPSGProjection projection;

	final static double LEFT = 14.323425;
	final static double RIGHT = 14.567871;
	final static double TOP = 50.146546;
	final static double BOTTOM = 50.020094;

	/**
	 * Measure performance time for computing direct distance between two points.
	 */
	public static void main(String[] args) throws Exception {
		setUp();
		
		long time = System.currentTimeMillis();
		double endLonM = ends.get(0).getLongitude();
		double endLatM = ends.get(0).getLatitude();
		double latLength = GeoCalculationsHelper.lengthOfLatitudeDegree(endLatM);
		double lonLength = GeoCalculationsHelper.lengthOfLongitudeDegree(endLonM);
		
		// During the computation of heuristic function, we do not change end state
		for (int i = 0; i < 5500000; i++) {
			double startLon = starts.get(i).getLongitude();
			double startLat = starts.get(i).getLatitude();

			GeoCalculationsHelper.distanceE2(startLon * lonLength, startLat * latLength, endLonM * lonLength, endLatM
					* latLength);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Nemet's formula time: " + time);

		time = System.currentTimeMillis();
		for (int i = 0; i < 5500000; i++) {
			double startLon = starts.get(i).getProjectedLongitude();
			double startLat = starts.get(i).getProjectedLatitude();
			double endLon = ends.get(i).getProjectedLongitude();
			double endLat = ends.get(i).getProjectedLatitude();

			Math.hypot(startLat - endLat, startLon - endLon);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Math.hypot time: " + time);

		time = System.currentTimeMillis();
		for (int i = 0; i < 5500000; i++) {
			double x = starts.get(i).getProjectedLongitude() - ends.get(i).getProjectedLongitude();
			double y = starts.get(i).getProjectedLatitude() - ends.get(i).getProjectedLatitude();

			Math.sqrt(x * x + y * y);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("Math.sqrt(a*a + b*b) time: " + time);

		time = System.currentTimeMillis();
		for (int i = 0; i < 5500000; i++) {
			double startLon = starts.get(i).getProjectedLongitude();
			double startLat = starts.get(i).getProjectedLatitude();
			double endLon = ends.get(i).getProjectedLongitude();
			double endLat = ends.get(i).getProjectedLatitude();

			FastMath.hypot(startLat - endLat, startLon - endLon);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("FastMath time: " + time);

		time = System.currentTimeMillis();
		for (int i = 0; i < 5500000; i++) {
			double startLon = starts.get(i).getLongitude();
			double startLat = starts.get(i).getLatitude();
			double endLon = ends.get(i).getLongitude();
			double endLat = ends.get(i).getLatitude();

			LatLon.distanceInMeters(startLat, startLon, endLat, endLon);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("LatLon distance time: " + time);

	}
	
	public static void setUp() throws Exception {
		long time = System.currentTimeMillis();
		// Initialization takes a lot of time
		projection = new EPSGProjection(2065);

		starts = new ArrayList<GPSLocation>();
		ends = new ArrayList<GPSLocation>();

		Random random = new Random(103L);
		for (int i = 1; i <= 5500000; i++) {
			double startLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
			double startLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
			double endLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
			double endLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();

			starts.add(projection.getProjectedGPSLocation(new GPSLocation(startLat, startLon)));
			ends.add(projection.getProjectedGPSLocation(new GPSLocation(endLat, endLon)));
		}

		System.out.println("Set up time: " + (System.currentTimeMillis() - time));
	}

}
