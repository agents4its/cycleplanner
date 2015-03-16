package cz.agents.cycleplanner.util;

// TODO oprav exception
// TODO popis
// TODO zjednot do interfacu use Java 8 - to make this method static and let Euclidean and Jaccard distance calculator implement it
public class EuclideanDistanceCalculator {

	/**
	 * TODO javadoc
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int calculate(int[] a, int[] b) {
		return (Integer) Math.round(EuclideanDistanceCalculator.calculate(a, b));
	}

	/**
	 * TODO javadoc
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double calculate(double[] a, double[] b) {
		double base = (a.length <= b.length) ? calculateBase(a, b) : calculateBase(b, a);
		return Math.sqrt(base);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private static double calculateBase(double[] a, double[] b) {
		double base = 0;

		for (int i = 0; i < a.length; i++) {
			base += (a[i] - b[i]) * (a[i] - b[i]);
		}

		return base;
	}

}
