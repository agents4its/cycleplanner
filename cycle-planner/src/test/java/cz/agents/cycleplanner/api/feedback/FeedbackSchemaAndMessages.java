package cz.agents.cycleplanner.api.feedback;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import cz.agents.cycleplanner.api.datamodel.feedback.CycleplannerFeedback;
import cz.agents.cycleplanner.api.datamodel.feedback.TimedCoordinate;
import cz.agents.cycleplanner.util.JSONSchemaUtils;
import eu.superhub.wp5.journeyplandatamodel.JSONUtils;

public class FeedbackSchemaAndMessages {

	public static void main(String[] args) throws JsonProcessingException, FileNotFoundException {

		JSONSchemaUtils.generateSchemaAndSaveToFile(CycleplannerFeedback.class, "FeedbackSchema.json");

		TimedCoordinate tc1 = new TimedCoordinate(14245100, 50180400, "2012-11-16T11:00:00.000+01:00");
		TimedCoordinate tc2 = new TimedCoordinate(14712100, 49937100, "2012-11-16T12:00:00.000+01:00");
		List<TimedCoordinate> tcList = new ArrayList<>();
		tcList.add(tc1);
		tcList.add(tc2);
		CycleplannerFeedback feedback = new CycleplannerFeedback(1234, 3, false, true, false, false, true, tcList);
		JSONUtils.saveAsJSONToFile(feedback, "FeedbackExample.json");
	}
}
