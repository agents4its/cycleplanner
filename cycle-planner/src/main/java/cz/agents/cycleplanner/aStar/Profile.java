package cz.agents.cycleplanner.aStar;

import java.util.ArrayList;
import java.util.List;

import cz.agents.cycleplanner.criteria.ComfortCriterion;
import cz.agents.cycleplanner.criteria.Criterion;
import cz.agents.cycleplanner.criteria.FlatnessCriterion;
import cz.agents.cycleplanner.criteria.QuietnessCriterion;
import cz.agents.cycleplanner.criteria.TravelTimeCriterion;

public class Profile {
	
    private final List<Criterion> criteria;
    
    public Profile(double travelTimeWeight, double comfortWeight, double quietnessWeight, double flatnessWeight) {

    	double sum = travelTimeWeight + comfortWeight + quietnessWeight + flatnessWeight;
    	criteria = new ArrayList<Criterion>();
    	
    	if (travelTimeWeight > 0.001) {
    		criteria.add(new TravelTimeCriterion(travelTimeWeight/sum));
    	}
    	
    	if (comfortWeight > 0.001) {
    		criteria.add(new ComfortCriterion(comfortWeight/sum));
    	}
    	
    	if (quietnessWeight > 0.001) {
    		criteria.add(new QuietnessCriterion(quietnessWeight/sum));
    	}
    	
    	if (flatnessWeight > 0.001) {
    		criteria.add(new FlatnessCriterion(flatnessWeight/sum));
    	}
    }

	public List<Criterion> getCriteria() {
		return criteria;
	}
}
