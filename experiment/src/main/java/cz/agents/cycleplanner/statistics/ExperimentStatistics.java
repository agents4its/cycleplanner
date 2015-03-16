package cz.agents.cycleplanner.statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Statistics of experiment.
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public class ExperimentStatistics {

	private static Logger log = Logger.getLogger(ExperimentStatistics.class);

	private List<String[]> statistics;

	public ExperimentStatistics(HeaderStatistics header) {
		statistics = new ArrayList<>();
		statistics.add(header.getHeader());
	}

	/**
	 * Add specified line to the statistics.
	 * 
	 * @param line
	 *            line to be appended to statistics
	 */
	public void add(LineStatistic line) {
		statistics.add(line.getLine());
	}

	/**
	 * Write statistics to <code>csv</code> file.
	 * 
	 * @param file
	 *            output file for writing statistics
	 */
	public void write(File file) {
		try {
			CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
			csvWriter.writeAll(statistics);
			csvWriter.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
