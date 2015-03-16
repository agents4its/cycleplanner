package cz.agents.cycleplanner.aStar.JGraphT;

import java.util.Iterator;

import cz.agents.cycleplanner.aStar.Profile;
import cz.agents.cycleplanner.criteria.Criterion;
import cz.agents.cycleplanner.criteria.FlatnessCriterion;
import cz.agents.cycleplanner.criteria.TravelTimeCriterion;
import cz.agents.cycleplanner.dataStructures.CycleEdge;
import eu.superhub.wp5.graphcommon.graph.elements.Node;
import eu.superhub.wp5.plannercore.structures.base.TimeDependentEdge;
import eu.superhub.wp5.plannercore.structures.evaluators.additionalkeysevaluators.AdditionalKeyEvaluator;
import eu.superhub.wp5.plannercore.structures.timedstructures.TimedNode;

/**
 * Created by Jan Nykl(CVUT) on 17.03.14.
 */
public class CriterionAKE extends AdditionalKeyEvaluator<TimedNode>{

    private double oneOverAverageSpeedMetersPerSecond;
    private Profile profile;

    public CriterionAKE(Profile profile, double averageSpeedKMpH) {

        this.oneOverAverageSpeedMetersPerSecond = 3.6/averageSpeedKMpH;
        this.profile = profile;
    }


    @Override
    public double computeAdditionalKey(TimedNode current, Node successor, TimeDependentEdge timeDependentEdge) {
        CycleEdge edge = (CycleEdge) timeDependentEdge;
        double currentCost = 0;
        double base = TravelTimeCriterion.evaluateWithSpeed(edge, oneOverAverageSpeedMetersPerSecond);

        for (Iterator<Criterion> it = profile.getCriteria().iterator(); it.hasNext();) {
            Criterion criterion = it.next();

            if (criterion instanceof FlatnessCriterion) {
                currentCost += criterion.evaluateWithWeight(edge, oneOverAverageSpeedMetersPerSecond);
            } else {
                currentCost += criterion.evaluateWithWeight(edge, base);
            }

        }
        return current.getAdditionalKey()+currentCost;
    }
}
