package cz.agents.cycleplanner.util;

import java.util.Set;

import com.google.common.collect.Sets;

// TODO zjednot do interfacu use Java 8 - to make this method static and let Euclidean and Jaccard distance calculator implement it
/**
 * TODO documentation
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class JaccardDistanceCalculator {

	/**
	 * TODO documentation
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static <E> double calculate(Set<E> a, Set<E> b) {

		Set<E> union;
		// Google Guava
		union = Sets.union(a, b);

		double unionSize = union.size();
		double intersectionSize = a.size() + b.size() - unionSize;

		return (unionSize - intersectionSize) / unionSize;
	}
}
