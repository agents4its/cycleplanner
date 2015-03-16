package cz.agents.cycleplanner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cz.agents.cycleplanner.api.datamodel.BoundingBox;
import cz.agents.cycleplanner.api.datamodel.Coordinate;
import cz.agents.cycleplanner.api.datamodel.Instruction;
import cz.agents.cycleplanner.api.datamodel.Response;
import cz.agents.cycleplanner.api.datamodel.ResponseStatus;
import cz.agents.cycleplanner.criteria.TravelTimeCriterion;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.routingService.RoutingService;

public class ResponseFactory {

	private RoutingService routingService;

	private long responseID;
	private BoundingBox boundingBox;

	private List<Coordinate> coordinates;
	private List<Integer> elevationProfile;
	private List<Integer> comulativeDistance;
	private List<Instruction> instructions;

	private double length;
	private double duration;
	private double elevationGain;
	private double elevationDrop;
	private double energyTotal;

	private double averageSpeedMetersPerSecond;
	private double oneOverAverageSpeedMetersPerSecond;

	public ResponseFactory(BoundingBox boundingBox, double averageSpeedKMpH) {
		routingService = RoutingService.INSTANCE;

		responseID = routingService.getResponseID();
		this.boundingBox = boundingBox;

		length = 0;
		duration = 0;
		elevationGain = 0;
		elevationDrop = 0;
		energyTotal = 0;

		coordinates = new ArrayList<>();
		elevationProfile = new ArrayList<>();
		comulativeDistance = new ArrayList<>();
		instructions = new ArrayList<>();

		averageSpeedMetersPerSecond = averageSpeedKMpH / 3.6;
		oneOverAverageSpeedMetersPerSecond = 1 / averageSpeedMetersPerSecond;
	}

	public Response getResponse(Collection<CycleEdge> path) {

		// If path was not found, return status PLAN_NOT_FOUND
		if (path == null || path.isEmpty() || path.size() <= 2) {
			return new Response(responseID, ResponseStatus.PLAN_NOT_FOUND);
		}

		if (path.size() >= 3) {
			Iterator<CycleEdge> it = path.iterator();
			CycleEdge previous = it.next();
			CycleEdge actual = it.next();
			CycleEdge next = it.next();

			add(previous);

			while (it.hasNext()) {

				sum(previous);

				coordinates.add(new Coordinate(actual.getFromNode().getLatitudeE6(), actual.getFromNode()
						.getLongitudeE6()));
				elevationProfile.add(round(actual.getFromNode().getElevation()));
				comulativeDistance.add(round(length));

				// nemozem pouzit add(...), do instruction.add vkladam tri rozne
				// hrany
				instructions.add(InstructionUtil.getInstruction(previous, actual, next));

				previous = actual;
				actual = next;
				next = it.next();
			}

			sum(previous);

			add(actual);
			sum(actual);

			add(next);
			sum(next);

			// nemozem pouzit add(...), potrebujem edge.getToNode() a vnutri
			// metody pouzivam edge.getFromNode()
			coordinates.add(new Coordinate(next.getToNode().getLatitudeE6(), next.getToNode().getLongitudeE6()));
			elevationProfile.add(round(next.getToNode().getElevation()));
			comulativeDistance.add(round(length));
			instructions.add(InstructionUtil.getInstruction(null, next, null));

		} else {
			CycleEdge last = null;
			for (CycleEdge edge : path) {

				add(edge);
				sum(edge);

				last = edge;
			}

			coordinates.add(new Coordinate(last.getToNode().getLatitudeE6(), last.getToNode().getLongitudeE6()));
			elevationProfile.add(round(last.getToNode().getElevation()));
			comulativeDistance.add(round(length));
			instructions.add(InstructionUtil.getInstruction(null, last, null));

		}

		Response response = new Response(responseID, ResponseStatus.OK, boundingBox, round(length), round(duration),
				round(elevationGain), round(elevationDrop), round(energyTotal), coordinates, elevationProfile,
				comulativeDistance, instructions);

		return response;
	}

	public static Response getEmptyResponse(ResponseStatus status) {
		if (status.equals(ResponseStatus.OUT_OF_BOUNDS)) {
			return new Response(Long.MIN_VALUE, ResponseStatus.OUT_OF_BOUNDS);
		} else if (status.equals(ResponseStatus.PLAN_NOT_FOUND)) {
			return new Response(Long.MIN_VALUE, ResponseStatus.PLAN_NOT_FOUND);
		}

		return new Response(Long.MIN_VALUE, ResponseStatus.PLANNER_EXCEPTION);
	}

	// TODO rename
	private void add(CycleEdge edge) {
		coordinates.add(new Coordinate(edge.getFromNode().getLatitudeE6(), edge.getFromNode().getLongitudeE6()));
		elevationProfile.add(round(edge.getFromNode().getElevation()));
		comulativeDistance.add(round(length));
		instructions.add(InstructionUtil.getInstruction(null, edge, null));
	}

	// TODO rename
	private void sum(CycleEdge edge) {
		length += edge.getLengthInMetres();
		duration += TravelTimeCriterion.evaluateWithSpeed(edge, oneOverAverageSpeedMetersPerSecond);
		elevationGain += edge.getRises();
		elevationDrop += edge.getDrops();
		energyTotal += EnergyConsumption.compute(edge, averageSpeedMetersPerSecond);
	}

	private int round(double d) {
		return (int) Math.round(d);
	}
}
