package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Parameter of MLC algorithm responsible for stopping search after exceeding
 * maximal running time
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class CuttingTimeParameter implements MLCAlgorithmParameter {
	private final long cuttingTime;

	public CuttingTimeParameter(long cuttingTime) {
		this.cuttingTime = cuttingTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		f.setLong(obj, cuttingTime);
	}

}
