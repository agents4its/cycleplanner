package cz.agents.cycleplanner.api.datamodel;

public class Instruction {

	private final Manoeuvre manoeuvre;
	private final String streetName;
	private final RoadType roadType;
	private final Surface surface;

	@SuppressWarnings("unused")
	private Instruction() {
		this.manoeuvre = null;
		this.streetName = null;
		this.roadType = null;
		this.surface = null;
	}

	public Instruction(Manoeuvre manoeuvre, String streetName, RoadType roadType, Surface surface) {
		super();
		this.manoeuvre = manoeuvre;
		this.streetName = streetName;
		this.roadType = roadType;
		this.surface = surface;
	}

	public Manoeuvre getManoeuvre() {
		return manoeuvre;
	}

	public String getStreetName() {
		return streetName;
	}

	public RoadType getRoadType() {
		return roadType;
	}

	public Surface getSurface() {
		return surface;
	}

	@Override
	public String toString() {
		return "Instruction [manoeuvre=" + manoeuvre + ", streetName=" + streetName + ", roadType=" + roadType
				+ ", surface=" + surface + "]";
	}
}
