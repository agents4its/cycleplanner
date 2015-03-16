package cz.agents.cycleplanner.statistics;

/**
 * Header of statistics.
 * 
 * Contains description of all statistics.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public interface HeaderStatistics {
	public String blankHeader = "    ";

	/**
	 * Returns header.
	 * 
	 * @return header as array of <code>String</code>
	 */
	public String[] getHeader();
}
