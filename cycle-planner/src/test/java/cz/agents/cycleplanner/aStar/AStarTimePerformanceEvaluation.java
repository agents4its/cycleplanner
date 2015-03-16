package cz.agents.cycleplanner.aStar;

import java.util.Random;

import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.routingService.RoutingService;
import cz.agents.cycleplanner.util.JSONUtils;

public class AStarTimePerformanceEvaluation {
	
	final static double LEFT = 14.323425;
	final static double RIGHT = 14.567871;
	final static double TOP = 50.146546;
	final static double BOTTOM = 50.020094;

	public static void main(String[] args) throws Exception {
				
		RoutingService service = RoutingService.INSTANCE;
		
		Random random = new Random(103L);
		long avgNew = 0;
		
		for (int i = 0; i < 1; i++) {			
			
			double startLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
			double startLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
			double endLon = LEFT + (RIGHT - LEFT) * random.nextDouble();
			double endLat = BOTTOM + (TOP - BOTTOM) * random.nextDouble();
			
			long time1 = System.currentTimeMillis();
			String newAStar = JSONUtils.javaObjectToJson(service.planJourney(new Request(startLat, startLon, endLat,
					endLon, 13.68, 1, 0, 0, 0)));
			if (i!=0) avgNew += System.currentTimeMillis() - time1;
			
			System.out.println(newAStar);
		}
		
		System.out.println("Astar time: "+(avgNew/100));
		
	}

}
