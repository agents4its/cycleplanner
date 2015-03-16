package cz.agents.cycleplanner.aStar.aima;

import java.util.Iterator;

import aima.core.agent.Action;
import aima.core.search.framework.StepCostFunction;
import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.criteria.Criterion;
import cz.agents.cycleplanner.criteria.FlatnessCriterion;
import cz.agents.cycleplanner.criteria.TravelTimeCriterion;
import cz.agents.cycleplanner.dataStructures.CycleEdge;

public class Cost implements StepCostFunction {

	private double oneOverAverageSpeedMetersPerSecond;
	private Profile profile;
	
	public Cost(Profile profile, double averageSpeedKMpH) {
		        
		this.oneOverAverageSpeedMetersPerSecond = 3.6/averageSpeedKMpH;
		this.profile = profile;
	}
	
	@Override
	public double c(Object s, Action a, Object sDelta) {
		if (a instanceof CycleAction) {
            CycleAction aa = (CycleAction) a;
            CycleEdge edge = aa.getEdgeToTake();
            double cost = 0;
            double base = TravelTimeCriterion.evaluateWithSpeed(edge, oneOverAverageSpeedMetersPerSecond);
            
            for (Iterator<Criterion> it = profile.getCriteria().iterator(); it.hasNext();) {
				Criterion criterion = it.next();
				
				if (criterion instanceof FlatnessCriterion) {
					cost += criterion.evaluateWithWeight(edge, oneOverAverageSpeedMetersPerSecond);
				} else {
					cost += criterion.evaluateWithWeight(edge, base);
				}
				
			}
            return cost;
            
        } else {
            System.out.println("Wrong Action");
            return 0;
        }
	}
}
