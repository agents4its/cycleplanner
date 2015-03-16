package cz.agents.cycleplanner.evaluate;

import java.util.Set;
import java.util.regex.Pattern;

import cz.agents.cycleplanner.dataStructures.CycleEdge;
import cz.agents.cycleplanner.dataStructures.CycleNode;
import eu.superhub.wp5.graphcommon.graph.Graph;

public class Evaluator {
	private static final Pattern OFFROAD_PATTERN = Pattern
			.compile("way::access::forestry|way::access::agricultural|way::highway::track|way::highway::path|way::highway::bridleway");
	
	
	public static double MAXIMUM_DOWNHILL_SPEED_MULTIPLIER = 2.5; // s_dmax
	public static double CRITICAL_DOWNHILL_GRADE = 0.1; // d'c
	public static double UPHILL_MULTIPLIER = 8; // a_l
	public static double PERCEPTION_UPHILL_MULTIPLIER = 13; // a_p
	
	/**
	 * TODO javadoc
	 * 
	 * @param graph
	 */
	public static void evaluateGraph(Graph<CycleNode, CycleEdge> graph) {
		for (CycleEdge edge : graph.getAllEdges()) {
			edge.setEvaluationDetails(Evaluator.createEvaluationDetails(edge));
		}
	}

	public static EvaluationDetails createEvaluationDetails(CycleEdge edge) {
		ParametersOfTags parameters = ParametersOfTags.INSTANCE;
		
		double preComputedTime = 1d;
		double maxTraveTimeConstant = 0d;
		double minTravelTimeMultiplier = 1d;
		double maxComfortMultiplier = -1d;
		double maxQuietnessMultiplier = -1d;
		double flatnessMultiplier;
		
		Set<String> edgeTags = edge.getOSMtags();
        if (edgeTags != null) {
            for (String edgeTag : edge.getOSMtags()) {
                                
                if (parameters.contains(edgeTag) && !OFFROAD_PATTERN.matcher(edgeTag).matches()) {
                	
                	double multiplier = parameters.getTravelTimeMultiplier(edgeTag);
                    if (minTravelTimeMultiplier > multiplier) minTravelTimeMultiplier = multiplier;
                    
                    double constant = parameters.getTravelTimeSlowdownConstant(edgeTag);
                    if (constant > maxTraveTimeConstant) maxTraveTimeConstant = constant;
                	
                	multiplier = parameters.getComfortMultiplier(edgeTag);
                	if (multiplier > maxComfortMultiplier) maxComfortMultiplier = multiplier;
                	
                	multiplier = parameters.getQuietnessMultiplier(edgeTag);
                	if (multiplier > maxQuietnessMultiplier) maxQuietnessMultiplier = multiplier;
                }
            }
        }
        // TODO check all from node tags, im going to separate tags form node and from edge
        double numerator = edge.getLengthInMetres() + UPHILL_MULTIPLIER*edge.getRises();        
        double denominator = getDownhillSpeedMultiplier(edge.getDrops(), edge.getLengthInMetres())*minTravelTimeMultiplier;

        preComputedTime = numerator/denominator;

        if (maxComfortMultiplier == -1) maxComfortMultiplier = 1;
        if (maxQuietnessMultiplier == -1) maxQuietnessMultiplier = 1;
        
        flatnessMultiplier = edge.getRises()*PERCEPTION_UPHILL_MULTIPLIER;
        
		return new EvaluationDetails(preComputedTime, maxTraveTimeConstant, maxComfortMultiplier, maxQuietnessMultiplier, flatnessMultiplier);
	}
	
	private static double getDownhillSpeedMultiplier(double drop, double length) {
		double downhillGrade = (length == 0d) ? 0 : drop/length;
		if (downhillGrade > CRITICAL_DOWNHILL_GRADE)
			return MAXIMUM_DOWNHILL_SPEED_MULTIPLIER;
		else 
			return ( ( (downhillGrade / CRITICAL_DOWNHILL_GRADE) * (MAXIMUM_DOWNHILL_SPEED_MULTIPLIER - 1) ) + 1);			
	}

}
