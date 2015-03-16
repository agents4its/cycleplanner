package cz.agents.cycleplanner.routingService;

public interface Planner<S, U> {
	public S plan(U a);
}
