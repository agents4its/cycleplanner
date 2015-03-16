package cz.agents.cycleplanner.junctions;

import java.util.HashSet;
import java.util.Set;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;

public class JunctionInnerEdge {

	private static long wayId = -1;

	private final CycleNode fromNode;
	private final CycleNode toNode;

	private final double angle;
	private final Direction direction;
	private final boolean trafficLightsPresent;

	private final Set<String> tags;
	
	private final Traffic traffic;
	private final FlowIntensity trafficIntensity;

	// private int numberOfCrossedTrafficFlows = 0;

	// TODO rename
	// private JunctionInnerEdgeRightOfWay rightOfWay;

	// private FlowIntensity incomingTrafficFlow;
	// private FlowIntensity outcomingTrafficFlow;

	public JunctionInnerEdge(CycleNode fromNode, CycleNode toNode, double angle, boolean trafficLightsPresent,
			Traffic traffic, FlowIntensity trafficIntensity) {

		this.fromNode = fromNode;
		this.toNode = toNode;
		this.angle = angle;
		this.direction = assignDirection(angle);
		this.trafficLightsPresent = trafficLightsPresent;
		this.traffic = traffic;
		this.trafficIntensity = trafficIntensity;
		this.tags = new HashSet<String>();
		setTags();
	}

	public double getAngle() {
		return angle;
	}

	public Direction getDirection() {
		return direction;
	}

	// public JunctionInnerEdgeRightOfWay getRightOfWay() {
	// return rightOfWay;
	// }
	//
	// public void setRightOfWay(JunctionInnerEdgeRightOfWay rightOfWay) {
	// this.rightOfWay = rightOfWay;
	// }

	private void setTags() {
		StringBuilder tagBuilder = new StringBuilder();
		tagBuilder.append("way::turn::");

		if (trafficLightsPresent) {
			tagBuilder.append("traffic_lights_");
		}

		tagBuilder.append(direction.toString().toLowerCase());
		tagBuilder.append("_");


		tagBuilder.append(String.valueOf(trafficIntensity.getValue()));

		tagBuilder.append(traffic.toString().toLowerCase());

		tags.add(tagBuilder.toString());
	}

	public boolean isTrafficLightsPresent() {
		return trafficLightsPresent;
	}

	public CycleEdge getCycleEdge() {
		double lengthInMetres = EdgeUtil.computeDirectDistanceInM(fromNode.getGpsLocation(), toNode.getGpsLocation());
		return new CycleEdge(fromNode, toNode, lengthInMetres, tags, new HashSet<>(), wayId--, angle);
	}

	private Direction assignDirection(double angle) {
		if (angle <= 15 || angle >= (360 - 15)) {
			return Direction.U_TURN;
		} else if (angle > 15 && angle < (180 - 15)) {
			return Direction.RIGHT;
		} else if (angle >= (180 - 15) && angle <= (180 + 15)) {
			return Direction.STRAIGHT;
		}

		return Direction.LEFT;
	}
}
