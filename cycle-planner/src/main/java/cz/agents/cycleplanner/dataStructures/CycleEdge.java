package cz.agents.cycleplanner.dataStructures;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

import cz.agents.cycleplanner.criteria.TravelTimeCriterion;
import cz.agents.cycleplanner.evaluate.EvaluationDetails;
import eu.superhub.wp5.plannercore.structures.base.TimeDependentEdge;
import eu.superhub.wp5.wp5common.modes.JourneyPlanTemplate;
import eu.superhub.wp5.wp5common.modes.ModeOfTransport;

/**
 * Represents an edge in a cycle route network graph.
 * 
 * @author Marcel Német <marcel.nemet@gmail.com>
 * @author Pavol Zilecky <pavol.zilecky@agents.fel.cvut.cz>
 */
public class CycleEdge extends TimeDependentEdge {

	private double elevation;
	private CycleNode fromNode;
	private CycleNode toNode;
	private Set<String> tagsJoinedEntityKeyAndValue;
	private EvaluationDetails evaluationDetails;

	private final Long wayId;
	private final double junctionAngle;

	public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres, Long wayId, double junctionAngle) {
		super(fromNode.getId(), toNode.getId(), lengthInMetres);
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.wayId = wayId;
		this.junctionAngle = junctionAngle;

		if (toNode.hasElevation() && !fromNode.hasElevation()) {
			this.elevation = toNode.getElevation();
		} else if (!toNode.hasElevation() && fromNode.hasElevation()) {
			this.elevation = -fromNode.getElevation();
		} else if (!toNode.hasElevation() && !fromNode.hasElevation()) {
			this.elevation = 0;
		} else {
			this.elevation = toNode.getElevation() - fromNode.getElevation();
		}
	}

	public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres) {
		this(fromNode, toNode, lengthInMetres, null, Double.POSITIVE_INFINITY);
		this.tagsJoinedEntityKeyAndValue = new HashSet<>();
	}

	public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres, Long wayId) {
		this(fromNode, toNode, lengthInMetres, wayId, Double.POSITIVE_INFINITY);
		this.tagsJoinedEntityKeyAndValue = new HashSet<>();
	}

	public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres, Set wayTags, Set relationTags,
			Long wayId, double junctionAngle) {
		this(fromNode, toNode, lengthInMetres, wayId, junctionAngle);

		if (this.tagsJoinedEntityKeyAndValue == null) {
			this.tagsJoinedEntityKeyAndValue = new HashSet<>();
		}

		for (Iterator it = wayTags.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof String) {
				String tag = (String) o;
				this.tagsJoinedEntityKeyAndValue.add(tag);
			} else if (o instanceof Tag) {
				Tag tag = (Tag) o;
				this.tagsJoinedEntityKeyAndValue.add("way::" + tag.getKey().concat("::").concat(tag.getValue()));
			}
		}

		for (Iterator it = relationTags.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof String) {
				String tag = (String) o;
				this.tagsJoinedEntityKeyAndValue.add(tag);
			} else if (o instanceof Tag) {
				Tag tag = (Tag) o;
				this.tagsJoinedEntityKeyAndValue.add("relation::" + tag.getKey().concat("::").concat(tag.getValue()));
			}

		}

	}

	public CycleEdge(CycleNode fromNode, CycleNode toNode, double lengthInMetres, Set wayTags, Set relationTags) {
		this(fromNode, toNode, lengthInMetres, wayTags, relationTags, null, Double.POSITIVE_INFINITY);
	}

	/**
	 * removes OSM tags in this edge and replaces the collection by null
	 */
	public void destroyTags() {
		if (this.tagsJoinedEntityKeyAndValue != null) {
			this.tagsJoinedEntityKeyAndValue.clear();
			this.tagsJoinedEntityKeyAndValue = null;
		}
	};

	/**
	 * @return the start node of this edge
	 */
	public CycleNode getFromNode() {
		return fromNode;
	}

	/**
	 * @return OSM tags of the start node of this edge
	 */
	public Set<String> getFromNodeTags() {
		return fromNode.getTagsJoinedKeyAndValue();
	}

	/**
	 * @return OSM tags of the end node of this edge
	 */
	public Set<String> getToNodeTags() {
		return toNode.getTagsJoinedKeyAndValue();
	}

	/**
	 * @return the end node of this edge
	 */
	public CycleNode getToNode() {
		return toNode;
	}

	/**
	 * returns associated OSM tags as Strings in format entity::key::value,
	 * where entity can be relation, way or node
	 * 
	 * @return Set of associated OSM tags
	 */
	public Set<String> getOSMtags() {
		return tagsJoinedEntityKeyAndValue;
	}

	/**
	 * @return cumulative elevation gain of this edge
	 */
	public double getRises() {
		return ((elevation < 0) ? 0 : elevation);
	}

	/**
	 * 
	 * @return cumulative elevation loss of this edge
	 */
	public double getDrops() {
		return ((elevation > 0) ? 0 : -elevation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkFeasibility(ModeOfTransport modeOfTransport) {
		return modeOfTransport.equals(ModeOfTransport.BIKE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkFeasibility(JourneyPlanTemplate journeyPlanTemplate) {
		return JourneyPlanTemplate.getModesOfTransport(journeyPlanTemplate).contains(ModeOfTransport.BIKE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkFeasibility(Set<ModeOfTransport> set) {
		return set.contains(ModeOfTransport.BIKE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadableDuration computeAverageTravelTime(double averageSpeedKMPerHour) {

		double travelTime = TravelTimeCriterion.evaluateWithSpeed(this, 3.6 / averageSpeedKMPerHour);

		return new Duration(Math.round(travelTime * 1000));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReadableInstant findNearestArrivalTimeAtTerminalNode(ReadableInstant instant, double averageSpeedKMPerHour) {
		double travelTime = TravelTimeCriterion.evaluateWithSpeed(this, 3.6 / averageSpeedKMPerHour);

		return new DateTime(instant).plusMillis(Math.round((float) travelTime * 1000));
	}

	/**
	 * returns evaluation details for the specified aspect, if they have been
	 * pre-calculated for this edge and saved. If not returns null.
	 * 
	 * @param aspect
	 * @return EvaluationDetails or null
	 */
	public EvaluationDetails getEvaluationDetails() {
		return evaluationDetails;
	}

	/**
	 * sets evaluation details for this edge and aspect
	 * 
	 * @param aspect
	 * @param evaluationDetails
	 */

	public void setEvaluationDetails(EvaluationDetails evaluationDetails) {

		this.evaluationDetails = evaluationDetails;
	}

	public Long getWayId() {
		return wayId;
	}

	public double getJunctionAngle() {
		return junctionAngle;
	}

	/**
	 * Check if this edge intersects with other edge define in method parameter.
	 * 
	 * @param edge
	 * @return true if this edge and the specified edge intersect each other;
	 *         false otherwise
	 */
	public boolean intersectsEdge(CycleEdge edge) {

		Line2D line1 = createLineSegment(this);
		Line2D line2 = createLineSegment(edge);

		return line1.intersectsLine(line2);
	}

	/**
	 * Converts CycleEdge to Line2D object
	 */
	private Line2D createLineSegment(CycleEdge edge) {
		Point2D from = new Point2D.Double(edge.getFromNode().getProjectedLongitude(), edge.getFromNode()
				.getProjectedLatitude());
		Point2D to = new Point2D.Double(edge.getToNode().getProjectedLongitude(), edge.getToNode()
				.getProjectedLatitude());

		return new Line2D.Double(from, to);
	}
}
