package cz.agents.cycleplanner.statistics;

/**
 * Line of statistics.
 * 
 * Contains value for each statistic.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public interface LineStatistic {

	public String blank = "    ";

	/**
	 * Returns statistic values.
	 * 
	 * @return statistic values as array of <code>String</code>
	 */
	public String[] getLine();
}
