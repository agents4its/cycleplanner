package cz.agents.cycleplanner.util;

import java.util.Set;
import java.util.regex.Pattern;

import cz.agents.cycleplanner.api.datamodel.Instruction;
import cz.agents.cycleplanner.api.datamodel.Manoeuvre;
import cz.agents.cycleplanner.api.datamodel.RoadType;
import cz.agents.cycleplanner.api.datamodel.Surface;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.routingService.RoutingService;

public class InstructionUtil {

	private static final Pattern SURFACE = Pattern.compile("way::surface::.*");
	private static final Pattern HIGHWAY = Pattern.compile("way::highway::.*");
	private static final Pattern PRIMARY = Pattern.compile("way::highway::primary|way::highway::primary_link");
	private static final Pattern SECONDARY = Pattern.compile("way::highway::secondary|way::highway::secondary_link");
	private static final Pattern TERTIARY = Pattern.compile("way::highway::tertiary|way::highway::tertiary_link");
	private static final Pattern CYCLEWAY = Pattern.compile(".*cycleway.*");
	private static final Pattern FOOTWAY = Pattern.compile(".*footway.*|.*pedestrian.*");
	private static final Pattern STEPS = Pattern.compile("way::highway::steps");

	public static Instruction getInstruction(CycleEdge previous, CycleEdge actual, CycleEdge next) {

		Manoeuvre manoeuvre = setManoeuvre(previous, actual, next);
		String streetName = getStreetName(actual.getWayId());
		RoadType roadType = getRoadType(actual.getOSMtags());
		Surface surface = getSurface(actual.getOSMtags());

		return new Instruction(manoeuvre, streetName, roadType, surface);
	}

	/**
	 * bestContinuations ??
	 * 
	 * Compare angle, name of the street and road type
	 * 
	 * @param previous
	 * @param actual
	 * @param next
	 * @return
	 */
	private static Manoeuvre setManoeuvre(CycleEdge previous, CycleEdge actual, CycleEdge next) {
		double angle = actual.getJunctionAngle();

		if (angle != Double.POSITIVE_INFINITY && previous != null && next != null) {

			String prevStreet = getStreetName(previous.getWayId());
			String nextStreet = getStreetName(next.getWayId());

			RoadType prevRoadType = getRoadType(previous.getOSMtags());
			RoadType nextRoadType = getRoadType(next.getOSMtags());

			boolean isBestContinuation = (prevStreet != null && nextStreet != null && prevStreet.equals(nextStreet))
					&& (prevRoadType != null && nextRoadType != null && prevRoadType.equals(nextRoadType));

			if (angle <= 5 || angle >= (360 - 5)) {
				return Manoeuvre.U_TURN;
			} else if (angle <= 180) {

				if (angle >= 180 - 45) {
					if (isBestContinuation) {
						return Manoeuvre.CONTINUE;
					} else {
						return Manoeuvre.KEEP_RIGHT;
					}
				} else {
					return Manoeuvre.TURN_RIGHT;
				}

			} else if (angle > 180) {

				if (angle <= 180 + 45) {
					if (isBestContinuation) {
						return Manoeuvre.CONTINUE;
					} else {
						return Manoeuvre.KEEP_LEFT;
					}
				} else {
					return Manoeuvre.TURN_LEFT;
				}
			}

		}
		return null;
	}

	private static String getStreetName(long wayId) {
		return RoutingService.INSTANCE.getStreetName(wayId);
	}

	private static RoadType getRoadType(Set<String> tags) {
		for (String tag : tags) {

			if (FOOTWAY.matcher(tag).matches()) {

				return RoadType.FOOTWAY;
			} else if (CYCLEWAY.matcher(tag).matches()) {

				return RoadType.CYCLEWAY;
			} else if (HIGHWAY.matcher(tag).matches()) {

				if (STEPS.matcher(tag).matches()) {
					return RoadType.STEPS;
				} else if (PRIMARY.matcher(tag).matches()) {
					return RoadType.PRIMARY;
				} else if (SECONDARY.matcher(tag).matches()) {
					return RoadType.SECONDARY;
				} else if (TERTIARY.matcher(tag).matches()) {
					return RoadType.TERTIARY;
				}

				return RoadType.ROAD;
			}
		}
		return null;
	}

	private static Surface getSurface(Set<String> tags) {
		for (String tag : tags) {
			if (SURFACE.matcher(tag).matches()) {
				return Surface.getSurface(tag.replaceFirst("way::surface::", "").toUpperCase());
			}
		}
		return null;
	}
}
