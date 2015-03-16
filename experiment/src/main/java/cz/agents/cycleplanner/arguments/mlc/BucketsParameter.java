package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Parameter of MLC algorithms with buckets speedup technique (e.g. <code>MLCBuckets</code>)
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class BucketsParameter implements MLCAlgorithmParameter {

	private final int[] buckets;

	public BucketsParameter(int[] buckets) {
		this.buckets = buckets;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		f.set(obj, buckets);

	}

}
