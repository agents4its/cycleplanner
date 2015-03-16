package cz.agents.cycleplanner.api.datamodel;

public enum Surface {
	PAVED, ASPHALT, COBBLESTONE, SETT, CONCRETE, PAVING_STONES, UNPAVED, COMPACTED, DIRT, EARTH, FINE_GRAVEL, GRASS, GRASS_PAVER, GRAVEL, GROUND, ICE, METAL, MUD, PEBBLESTONE, SALT, SAND, SNOW, WOOD;

	public static Surface getSurface(String type) {
		if (type.startsWith(PAVED.toString())) {
			return PAVED;
		} else if (type.startsWith(ASPHALT.toString())) {
			return ASPHALT;
		} else if (type.startsWith(COBBLESTONE.toString())) {
			return COBBLESTONE;
		} else if (type.startsWith(SETT.toString())) {
			return SETT;
		} else if (type.startsWith(CONCRETE.toString())) {
			return CONCRETE;
		} else if (type.startsWith(PAVING_STONES.toString())) {
			return PAVING_STONES;
		} else if (type.startsWith(UNPAVED.toString())) {
			return UNPAVED;
		} else if (type.startsWith(COMPACTED.toString())) {
			return COMPACTED;
		} else if (type.startsWith(DIRT.toString())) {
			return DIRT;
		} else if (type.startsWith(EARTH.toString())) {
			return EARTH;
		} else if (type.startsWith(FINE_GRAVEL.toString())) {
			return FINE_GRAVEL;
		} else if (type.startsWith(GRASS.toString())) {
			return GRASS;
		} else if (type.startsWith(GRASS_PAVER.toString())) {
			return GRASS_PAVER;
		} else if (type.startsWith(GRAVEL.toString())) {
			return GRAVEL;
		} else if (type.startsWith(GROUND.toString())) {
			return GROUND;
		} else if (type.startsWith(ICE.toString())) {
			return ICE;
		} else if (type.startsWith(METAL.toString())) {
			return METAL;
		} else if (type.startsWith(MUD.toString())) {
			return MUD;
		} else if (type.startsWith(PEBBLESTONE.toString())) {
			return PEBBLESTONE;
		} else if (type.startsWith(SALT.toString())) {
			return SALT;
		} else if (type.startsWith(SAND.toString())) {
			return SAND;
		} else if (type.startsWith(SNOW.toString())) {
			return SNOW;
		} else if (type.startsWith(WOOD.toString())) {
			return WOOD;
		}

		return null;
	}
}
