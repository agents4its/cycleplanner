package cz.agents.cycleplanner.ebike;

import java.util.Set;
import java.util.regex.Pattern;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.util.EnergyConsumption;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.structures.base.TimeDependentEdge;
import eu.superhub.wp5.plannercore.structures.evaluators.additionalkeysevaluators.AdditionalKeyEvaluator;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;

/**
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class ElectricBicycleCriterion extends AdditionalKeyEvaluator<TimedNode> {

	private static final Pattern STEPS_PATTERN = Pattern
			.compile("way::highway::steps|node::highway::elevator|node::highway::steps");

	private double averageSpeedMetersPerSecond;

	public ElectricBicycleCriterion(double averageSpeedKMpH) {

		this.averageSpeedMetersPerSecond = averageSpeedKMpH / 3.6;
	}

	@Override
	public double computeAdditionalKey(TimedNode current, Node successor, TimeDependentEdge timeDependentEdge) {
		CycleEdge edge = (CycleEdge) timeDependentEdge;
		        
        double isEdgeStep = areSteps(edge.getOSMtags());
        double isNodeStep = areSteps(edge.getFromNodeTags());

		return current.getAdditionalKey() + isEdgeStep + isNodeStep + EnergyConsumption.compute(edge, averageSpeedMetersPerSecond);
	}
	
	private double areSteps(Set<String> tags) {

		if (tags != null) {
			for (String edgeTag : tags) {

				if (STEPS_PATTERN.matcher(edgeTag).matches()) {
					return Double.POSITIVE_INFINITY;
				}
			}
		}

		return 0d;
	}

}
