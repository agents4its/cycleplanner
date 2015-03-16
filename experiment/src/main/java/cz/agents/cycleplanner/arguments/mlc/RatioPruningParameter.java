package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Parameter of MLC algorithms with ratio pruning speedup technique (e.g.
 * <code>MLCRatioPruning</code>)
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class RatioPruningParameter implements MLCAlgorithmParameter {

	private final double alpha;

	public RatioPruningParameter(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		f.setDouble(obj, alpha);
	}

}
