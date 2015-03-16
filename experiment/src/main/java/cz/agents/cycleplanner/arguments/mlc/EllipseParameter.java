package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Parameter of MLC algorithms with ellipse speedup technique (e.g. <code>MLCEllipse</code>)
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class EllipseParameter implements MLCAlgorithmParameter {

	private final double aOverB;

	public EllipseParameter(double aOverB) {
		this.aOverB = aOverB;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException {
		f.setAccessible(true);
		f.setDouble(obj, aOverB);
	}

}
