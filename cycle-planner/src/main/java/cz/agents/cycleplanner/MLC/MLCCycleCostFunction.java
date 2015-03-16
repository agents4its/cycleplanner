package cz.agents.cycleplanner.MLC;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.criteria.Criterion;
import cz.agents.cycleplanner.criteria.FlatnessCriterion;
import cz.agents.cycleplanner.criteria.TravelTimeCriterion;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.EdgeId;

public class MLCCycleCostFunction implements MLCCostFunction<CycleNode, CycleEdge> {

	private static Logger log = Logger.getLogger(MLCCycleCostFunction.class);

	/**
	 * 
	 */
	private final double oneOverAverageSpeedMetersPerSecond;

	/**
	 * Cache costs for edges, because in MLC we can traverse one edge multiple
	 * times.
	 */
	private Map<EdgeId, int[]> costsCache;

	public MLCCycleCostFunction(double averageSpeedKMpH) {
		this.oneOverAverageSpeedMetersPerSecond = 3.6 / averageSpeedKMpH;
		this.costsCache = new HashMap<EdgeId, int[]>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] getCostVector(CycleNode current, CycleNode next, CycleEdge edge) {

		if (costsCache.containsKey(edge.getEdgeId())) {
			return costsCache.get(edge.getEdgeId());
		}

		double travelTime = TravelTimeCriterion.evaluateWithSpeed(edge, oneOverAverageSpeedMetersPerSecond);

		double quietComfort = edge.getLengthInMetres() * getCriterionMaxQuietComfort(edge);

		Criterion criterion = new FlatnessCriterion(1);
		double flatness = criterion.evaluate(edge, oneOverAverageSpeedMetersPerSecond);

		int[] costs = new int[] { (int) Math.round(travelTime), (int) Math.round(quietComfort),
				(int) Math.round(flatness) };

		costsCache.put(edge.getEdgeId(), costs);

		return costs;
	}

	/**
	 * 
	 * TODO javadoc
	 * 
	 * @param edge
	 * @return
	 */
	private double getCriterionMaxQuietComfort(CycleEdge edge) {
		double comfort = Math.round(edge.getEvaluationDetails().getComfortMultiplier() * 10d);
		double quietness = Math.round(edge.getEvaluationDetails().getQuietnessMultiplier() * 10d);
		
		if (comfort != 10 && quietness != 10) {
			if (comfort >= quietness) {
				return comfort;
			} else {
				return quietness;
			}
		} else if (comfort == 10 && quietness != 10) {
			return quietness;
		}

		return comfort;
	}

	/**
	 * 
	 * TODO javadoc
	 * 
	 * @param edge
	 * @return
	 */
	@SuppressWarnings("unused")
	private int getCriterionQuietPlusComfort(CycleEdge edge) {
		int comfort = (int) Math.round(edge.getEvaluationDetails().getComfortMultiplier() * 10);
		int quietness = (int) Math.round(edge.getEvaluationDetails().getQuietnessMultiplier() * 10);

		if (comfort == 10 && quietness == 10) {
			return comfort;
		} else if (comfort != 10 && quietness == 10) {
			return comfort;
		} else if (comfort == 10 && quietness != 10) {
			return quietness;
		}

		return comfort + quietness;
	}

}
