package cz.agents.cycleplanner.arguments;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Command line parser for arguments
 * 
 * @author Pavol Zilecky (pavol.zilecky@agents.fel.cvut.cz)
 *
 */
public abstract class CommandLineArgumentsParser {

	protected CommandLine cmd;

	public CommandLineArgumentsParser(String[] args) {

		Options options = getOptions();
		CommandLineParser parser = new BasicParser();

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {

			HelpFormatter formatter = new HelpFormatter();
			System.out.println("Wrong arguments!");
			formatter.printHelp("Please, use following arguments", options);
			System.exit(-1);
		}
	}

	/**
	 * Returns specified options for command line
	 * 
	 * @return object {@link Options} represents a collection of {@link Option}
	 *         objects
	 */
	protected abstract Options getOptions();

}
