package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Parameter of MLC algorithms with cost pruning speedup technique (e.g.
 * <code>MLCCostPruning</code>)
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class CostPruningParameter implements MLCAlgorithmParameter {

	private final int gamma;

	public CostPruningParameter(int gamma) {
		this.gamma = gamma;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {

		f.setAccessible(true);
		f.setInt(obj, gamma);
	}
}
