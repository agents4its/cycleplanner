package cz.agents.cycleplanner;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.routingService.City;
import cz.agents.cycleplanner.util.CityBoundingBoxDiagonalDistanceCalculator;

public class CityBoundingBoxDiagonalDistanceCalculatorTest {
	
	private static Logger log = Logger.getLogger(CityBoundingBoxDiagonalDistanceCalculatorTest.class);

	public static void main(String[] args) {

		double diagonalDist = CityBoundingBoxDiagonalDistanceCalculator.calculate(City.PRAGUE);

		log.info("Bounding box diagonal distance " + diagonalDist + "m");
	}
}
