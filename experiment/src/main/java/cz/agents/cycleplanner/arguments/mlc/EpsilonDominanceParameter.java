package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Parameter of MLC algorithms with epsilon dominance speedup technique (e.g.
 * <code>MLCEpsilonDominance</code>)
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class EpsilonDominanceParameter implements MLCAlgorithmParameter {

	private final double epsilon;

	public EpsilonDominanceParameter(double epsilon) {
		this.epsilon = epsilon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		f.set(obj, epsilon);
	}

}
