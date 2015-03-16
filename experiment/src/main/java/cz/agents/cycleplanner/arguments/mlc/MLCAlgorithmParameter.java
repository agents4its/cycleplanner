package cz.agents.cycleplanner.arguments.mlc;

import java.lang.reflect.Field;

/**
 * Represents parameter of MLC algorithm
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 */
public interface MLCAlgorithmParameter {

	/**
	 * Modify field f of object obj with parameter value.
	 * 
	 * @param f
	 *            the field which should be modified
	 * @param obj
	 *            the object whose field should be modified
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public void set(Field f, Object obj) throws IllegalArgumentException, IllegalAccessException;

}