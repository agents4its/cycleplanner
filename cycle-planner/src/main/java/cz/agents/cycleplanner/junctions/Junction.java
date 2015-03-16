package cz.agents.cycleplanner.junctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import cz.agents.cycleplanner.util.AngleUtil;
import eu.superhub.wp5.graphcommon.graph.EdgeId;
import eu.superhub.wp5.graphcommon.graph.utils.EdgeUtil;

public class Junction {

	private static long nextId = -1l;

	private static final Pattern MOTORWAY = Pattern.compile("way::highway::motroway|way::highway::motroway_link");
	private static final Pattern TRUNK = Pattern.compile("way::highway::trunk|way::highway::trunk_link");
	private static final Pattern PRIMARY = Pattern.compile("way::highway::primary|way::highway::primary_link");
	private static final Pattern SECONDARY = Pattern.compile("way::highway::secondary|way::highway::secondary_link");
	private static final Pattern TERTIARY = Pattern.compile("way::highway::tertiary|way::highway::tertiary_link");
	private static final Pattern ROAD = Pattern.compile("way::highway::road");
	private static final Pattern UNCLASSIFIED = Pattern.compile("way::highway::unclassified");
	private static final Pattern RESIDENTIAL = Pattern.compile("way::highway::residential");
	private static final Pattern LIVING_STREET = Pattern.compile("way::highway::living_street");
	private static final Pattern SERVICE = Pattern.compile("way::highway::service");


	private static final Pattern TRAFFIC_SIGNALS = Pattern.compile("node::highway::traffic_signals|"
			+ "node::crossing::traffic_signals");


	private CycleNode junctionNode;

	private List<CycleEdge> incomingEdges;
	private List<CycleEdge> outcomingEdges;

	private List<JunctionInnerEdge> innerEdges;

	private Map<EdgeId, CycleEdge> newIncomingEdgesByOldEdgeId;
	private Map<EdgeId, CycleEdge> newOutcomingEdgesByOldEdgeId;

	private boolean trafficLightsPresent;
	

	private FlowIntensity mainTrafficFlow = FlowIntensity.SMALL;
	private boolean moreThanOneMainTrafficFlow = false;

	public Junction(CycleNode junctionNode, List<CycleEdge> incomingEdges, List<CycleEdge> outcomingEdges) {

		this.junctionNode = junctionNode;

		this.incomingEdges = incomingEdges;
		this.outcomingEdges = outcomingEdges;

		newIncomingEdgesByOldEdgeId = new HashMap<>();
		newOutcomingEdgesByOldEdgeId = new HashMap<>();

		innerEdges = new ArrayList<>();

		trafficLightsPresent = trafficLightPresent();

		createJunction();

	}

	public long getJunctionId() {
		return junctionNode.getId();
	}

	public Map<EdgeId, CycleEdge> getNewIncomingEdgesByOldEdgeId() {
		return newIncomingEdgesByOldEdgeId;
	}

	public Map<EdgeId, CycleEdge> getNewOutcomingEdgesByOldEdgeId() {
		return newOutcomingEdgesByOldEdgeId;
	}

	public List<JunctionInnerEdge> getInnerEdges() {
		return innerEdges;
	}

	private void createJunction() {
		handleIncomingEdges();
		handleOutcomingEdges();

		moreThanOneMainTrafficFlow = isMoreThanOneMainTrafficFlow();

		modelInnerEdges();
	}

	private Set<String> copyJunctionTags() {
		Set<String> tags = new HashSet<String>();
		for (String tag : junctionNode.getTagsJoinedKeyAndValue()) {
			tags.add(tag);
		}
		return tags;
	}

	private void handleIncomingEdges() {

		for (CycleEdge cycleEdge : incomingEdges) {

			setMainTrafficFlow(cycleEdge);

			CycleNode newCycleNode = createInNode(cycleEdge.getFromNode());

			CycleEdge newCycleEdge;

			newCycleEdge = createEdge(cycleEdge.getFromNode(), newCycleNode, cycleEdge.getOSMtags(),
					cycleEdge.getWayId());

			newIncomingEdgesByOldEdgeId.put(cycleEdge.getEdgeId(), newCycleEdge);
		}
	}

	private CycleNode createInNode(CycleNode from) {
		Set<String> tags = copyJunctionTags();
		tags.add("node::junction::in");

		return new CycleNode(getNewId(), JunctionCoordinatesUtil.deriveCoordinatesJunctionIncomingNode(junctionNode,
				from), junctionNode.getDescription(), tags);
	}

	private void handleOutcomingEdges() {
		for (CycleEdge cycleEdge : outcomingEdges) {

			setMainTrafficFlow(cycleEdge);

			CycleNode newCycleNode = createOutNode(cycleEdge.getToNode());

			CycleEdge newCycleEdge;

			newCycleEdge = createEdge(newCycleNode, cycleEdge.getToNode(), cycleEdge.getOSMtags(), cycleEdge.getWayId());

			newOutcomingEdgesByOldEdgeId.put(cycleEdge.getEdgeId(), newCycleEdge);
		}
	}

	private CycleNode createOutNode(CycleNode to) {
		Set<String> tags = copyJunctionTags();
		tags.add("node::junction::out");

		return new CycleNode(getNewId(), JunctionCoordinatesUtil.deriveCoordinatesJunctionOutcomingNode(junctionNode,
				to), junctionNode.getDescription(), tags);
	}

	private CycleEdge createEdge(CycleNode from, CycleNode to, Set<String> tags, Long wayId) {

		double lengthInMetres = EdgeUtil.computeDirectDistanceInM(from.getGpsLocation(), to.getGpsLocation());

		return new CycleEdge(from, to, lengthInMetres, tags, new HashSet<>(), wayId, Double.POSITIVE_INFINITY);
	}

	/**
	 * Returns negative IDs for additional elements
	 * 
	 * @return (long) ID
	 */
	private long getNewId() {
		return nextId--;
	}

	/**
	 * Compare current traffic flow with specified edge flow and store the
	 * highest value
	 * 
	 */
	private void setMainTrafficFlow(CycleEdge edge) {
		FlowIntensity edgeTrafficFlow = assignFlowIntensity(edge.getOSMtags());

		if (edgeTrafficFlow.getValue() > mainTrafficFlow.getValue()) {
			mainTrafficFlow = edgeTrafficFlow;
		}
	}
	
	private boolean isMoreThanOneMainTrafficFlow() {
		
		Set<CycleNode> nodes = new HashSet<>();
		
		for (CycleEdge edge : incomingEdges) {
			if (assignFlowIntensity(edge.getOSMtags()) == mainTrafficFlow) {
				nodes.add(edge.getFromNode());
			}
		}
		for (CycleEdge edge : outcomingEdges) {
			if (assignFlowIntensity(edge.getOSMtags()) == mainTrafficFlow) {
				nodes.add(edge.getToNode());
			}
		}
		
		return (nodes.size() <= 2) ? false : true;
	}

	/**
	 * connect the new junction (which has incoming edge) with those that has
	 * outgoing edge
	 */
	private void modelInnerEdges() {

		this.innerEdges = new ArrayList<>();

		for (CycleEdge incomingEdge : newIncomingEdgesByOldEdgeId.values()) {
			for (CycleEdge outcomingEdge : newOutcomingEdgesByOldEdgeId.values()) {
				this.innerEdges.add(createInnerEdge(incomingEdge, outcomingEdge));
			}

		}
	}

	// private List<JunctionInnerEdge> checkRightOfWay(List<JunctionInnerEdge>
	// innerEdges) {
	//
	// boolean equalRightOfWay = true;
	// for (JunctionInnerEdge junctionInnerEdge : innerEdges) {
	// if (junctionInnerEdge.getRightOfWay() ==
	// JunctionInnerEdgeRightOfWay.MAIN_SIDE
	// || junctionInnerEdge.getRightOfWay() ==
	// JunctionInnerEdgeRightOfWay.SIDE_MAIN) {
	// equalRightOfWay = false;
	// }
	// }
	//
	// if (equalRightOfWay) {
	// for (JunctionInnerEdge junctionInnerEdge : innerEdges) {
	// junctionInnerEdge
	// .setRightOfWay(JunctionInnerEdgeRightOfWay.MAIN_ROAD_NOT_SPECIFIED);
	// }
	// }
	//
	// return innerEdges;
	// }

	private JunctionInnerEdge createInnerEdge(CycleEdge incomingEdge, CycleEdge outcomingEdge) {

		// Uhol pocitam medzi zaciatocnym vrcholom vstupnej hrany, krizovatkovym
		// vrcholom a koncovym vrcholov vystupnej hrany
		double angle = AngleUtil.getAngle(incomingEdge.getFromNode(), junctionNode, outcomingEdge.getToNode());

		FlowIntensity flowIncoming = assignFlowIntensity(incomingEdge.getOSMtags());
		FlowIntensity flowOutcoming = assignFlowIntensity(outcomingEdge.getOSMtags());

		// Parallel - ked idem z vacsej intenzity na nizsiu
		// Cross - ked idem z nizsej intenzity na vyssiu
		// Ak sa incoming a outcoming rovnaju a rovnaju sa aj najvysiej hodnote
		// trafficflow v krizovatke tak

		Traffic traffic;
		if (flowIncoming == mainTrafficFlow && mainTrafficFlow == flowOutcoming) {
			traffic = moreThanOneMainTrafficFlow ? Traffic.CROSS : Traffic.PARALLEL;
		} else if (flowIncoming.getValue() > flowOutcoming.getValue()) {
			traffic = Traffic.PARALLEL;
		} else {
			traffic = Traffic.CROSS;
		}
		return new JunctionInnerEdge(incomingEdge.getToNode(), outcomingEdge.getFromNode(), angle,
				trafficLightsPresent, traffic, mainTrafficFlow);
	}

	private FlowIntensity assignFlowIntensity(Set<String> tags) {
		for (String tag : tags) {

			if (MOTORWAY.matcher(tag).matches() || TRUNK.matcher(tag).matches() || PRIMARY.matcher(tag).matches()) {

				return FlowIntensity.HUGE;
			} else if (SECONDARY.matcher(tag).matches()) {

				return FlowIntensity.BIG;
			} else if (TERTIARY.matcher(tag).matches()) {

				return FlowIntensity.MEDIUM;
			} else if (ROAD.matcher(tag).matches() || UNCLASSIFIED.matcher(tag).matches()
					|| RESIDENTIAL.matcher(tag).matches() || LIVING_STREET.matcher(tag).matches()
					|| SERVICE.matcher(tag).matches()) {

				return FlowIntensity.SMALL;
			}
		}

		return FlowIntensity.SMALL;
	}

	/**
	 * If at least one node contains traffic_signals tag, then traffic lights
	 * are present in junction.
	 */
	private boolean trafficLightPresent() {

		for (CycleEdge edge : incomingEdges) {

			for (String tag : edge.getOSMtags()) {
				if (TRAFFIC_SIGNALS.matcher(tag).matches()) {
					return true;
				}
			}
		}

		for (CycleEdge edge : outcomingEdges) {

			for (String tag : edge.getOSMtags()) {
				if (TRAFFIC_SIGNALS.matcher(tag).matches()) {
					return true;
				}
			}

			for (String tag : edge.getToNodeTags()) {
				if (TRAFFIC_SIGNALS.matcher(tag).matches()) {
					return true;
				}
			}
		}

		return false;
	}
}
