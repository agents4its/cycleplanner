package cycle.planner.aStar;

import java.util.Iterator;

import cvut.fel.nemetma1.aStar.search.CycleAction;
import cvut.fel.nemetma1.dataStructures.CycleEdge;
import cycle.planner.evaluate.criteria.Criterion;
import cycle.planner.evaluate.criteria.FlatnessCriterion;
import cycle.planner.evaluate.criteria.TravelTimeCriterion;
import aima.core.agent.Action;
import aima.core.search.framework.StepCostFunction;

public class Cost implements StepCostFunction {

	private double oneOverAverageSpeedMetersPerSecond;
	private Profile profile;
	
	public Cost(Profile profile, double averageSpeedMetersPerSecond) {
		        
		this.oneOverAverageSpeedMetersPerSecond = 1/averageSpeedMetersPerSecond;
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
