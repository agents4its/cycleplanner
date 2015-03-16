package cz.agents.cycleplanner.routingService;

import java.util.concurrent.Callable;

import cz.agents.cycleplanner.api.datamodel.Request;
import cz.agents.cycleplanner.api.datamodel.Response;

public class PlannerCallback implements Callable<Response> {

	private Planner<Response, Request> planner;
	private Request request;

	public PlannerCallback(Planner<Response, Request> planner, Request request) {
		this.planner = planner;
		this.request = request;
	}

	@Override
	public Response call() {
		return planner.plan(request);
	}

}
