package cycle.planner.aStar;

import java.util.List;

import aima.core.agent.Action;
import aima.core.search.framework.HeuristicFunction;
import aima.core.search.framework.StepCostFunction;
import cvut.fel.nemetma1.aStar.search.AbstractAStarSearch;
import cvut.fel.nemetma1.dataStructures.CycleNode;
import cvut.fel.nemetma1.graphWrapper.GraphWrapper;

public class PseudoMultiCriteriaAStarSearch extends AbstractAStarSearch {

	private Profile profile;
    private double averageSpeedMetersPerSecond;

    public PseudoMultiCriteriaAStarSearch(Profile profile, double averageSpeedMetersPerSecond) {
    	this.profile = profile;
    	this.averageSpeedMetersPerSecond = averageSpeedMetersPerSecond;
    }
    
	@Override
	protected List<Action> findActions(CycleNode startNode, CycleNode endNode,
			GraphWrapper graphWrapper) throws Exception {
		StepCostFunction costWithElevation = new Cost(profile, averageSpeedMetersPerSecond);
		HeuristicFunction heuristicWithElevation = new Heuristic(endNode,
				profile, averageSpeedMetersPerSecond);
		List<Action> actions = findPathAStar(startNode, graphWrapper, endNode,
				costWithElevation, heuristicWithElevation);
		return actions;
	}
}
