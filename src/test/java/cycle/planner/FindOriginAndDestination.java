package cycle.planner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.openstreetmap.osm.data.coordinates.LatLon;

import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cvut.fel.nemetma1.routingService.RoutingService;
import cycle.planner.aStar.Profile;

public class FindOriginAndDestination {
	final static int ITERATION = 1;
	final static double LEFT = 14.323425;
	final static double RIGHT = 14.567871;
	final static double TOP = 50.146546;
	final static double BOTTOM = 50.020094;
	final static double AVERAGE_SPEED_METERS_PER_SECOND = 3.8;

	public static void main(String[] args) throws FileNotFoundException {

		RoutingService rs = RoutingService.INSTANCE;
		List<Collection<CycleEdge>> profiles;
		List<List<String>> results = new ArrayList<List<String>>();

		Random random = new Random(103L);
		boolean end = false;
		double startLon, startLat, endLon, endLat;
		startLat = startLon = endLon = endLat = 0;
		for (int i = 0; i < 10; i++) {

			while (!end) {
				end = true;
				System.out
						.println("********************************************************************************");

				startLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
				startLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
				endLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
				endLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();

				double dist = LatLon.distanceInMeters(startLat, startLon,
						endLat, endLon);
				if (dist < 7000 && dist > 10000)
					continue;

				profiles = new ArrayList<Collection<CycleEdge>>();
				// Commuting (2; 1; 1; 1)
				// Bike friendly (1; 3; 5; 2)
				// Flat (1; 1; 1; 5)
				// Travel time (1; 0; 0; 0)
				profiles.add(rs.findRoute(startLat, startLon, endLat, endLon,
						AVERAGE_SPEED_METERS_PER_SECOND,
						new Profile(2, 1, 1, 1)));
				profiles.add(rs.findRoute(startLat, startLon, endLat, endLon,
						AVERAGE_SPEED_METERS_PER_SECOND,
						new Profile(1, 3, 5, 2)));
				profiles.add(rs.findRoute(startLat, startLon, endLat, endLon,
						AVERAGE_SPEED_METERS_PER_SECOND,
						new Profile(1, 1, 1, 5)));
				profiles.add(rs.findRoute(startLat, startLon, endLat, endLon,
						AVERAGE_SPEED_METERS_PER_SECOND,
						new Profile(1, 0, 0, 0)));

				for (int j = 0; j < profiles.size(); j++) {
					Collection<CycleEdge> a = profiles.get(j);
					for (int k = 0; k < profiles.size(); k++) {
						if (k == j)
							continue;

						Collection<CycleEdge> b = profiles.get(k);
						int diff = 0;
						for (Iterator<CycleEdge> it = a.iterator(); it
								.hasNext();) {
							CycleEdge edge = it.next();
							if (!b.contains(edge)) {
								diff++;
							}
						}

						System.out.println("**********");
						System.out.println(j + " : " + k);
						double rate = (double) diff / (double) a.size();
						System.out.println(diff + "/" + a.size() + "=" + rate);

						if (rate < 0.3)
							end = false;
						// System.out.println(k+" : "+j);
						// System.out.println(diffOposite+"/"+b.size()+"="+((double)diffOposite/(double)b.size()));
					}

				}
			}
			System.out.println("OUTPUT: ");
			System.out.println(startLon + " " + startLat + " " + endLon + " "
					+ endLat);
			results.add(getJSON(startLon, startLat, endLon, endLat));
			end = false;
		}

		for (int i = 0; i < results.size(); i++) {
			printToFile(results.get(i), i);
		}
	}

	public static List<String> getJSON(double startLon, double startLat,
			double endLon, double endLat) {
		List<String> jsons = new ArrayList<String>();
		RoutingService rs = RoutingService.INSTANCE;

		jsons.add(rs.findRouteAsJSON(startLat, startLon, endLat, endLon,
				AVERAGE_SPEED_METERS_PER_SECOND, new Profile(2, 1, 1, 1)));
		jsons.add(rs.findRouteAsJSON(startLat, startLon, endLat, endLon,
				AVERAGE_SPEED_METERS_PER_SECOND, new Profile(1, 3, 5, 2)));
		jsons.add(rs.findRouteAsJSON(startLat, startLon, endLat, endLon,
				AVERAGE_SPEED_METERS_PER_SECOND, new Profile(1, 1, 1, 5)));
		jsons.add(rs.findRouteAsJSON(startLat, startLon, endLat, endLon,
				AVERAGE_SPEED_METERS_PER_SECOND, new Profile(1, 0, 0, 0)));
		return jsons;
	}

	public static void print(List<String> jsons) {
		for (String string : jsons) {
			System.out.println("**************************************");
			System.out.println(string);
		}
	}

	public static void printToFile(List<String> jsons, int i)
			throws FileNotFoundException {
		PrintStream c;
		for (int j = 0; j < jsons.size(); j++) {
			c = new PrintStream(new File("test_diff_" + i + "_" + j + ".json"));
			c.print(jsons.get(j));
			c.flush();
			c.close();
		}

	}
}
