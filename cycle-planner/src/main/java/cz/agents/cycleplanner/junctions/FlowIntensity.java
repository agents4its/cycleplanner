package cz.agents.cycleplanner.junctions;

public enum FlowIntensity {
	// TODO rename
	SMALL(1), MEDIUM(5), BIG(10), HUGE(20);
	
	private int value;
		
	private FlowIntensity(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
