package cz.agents.cycleplanner.experiment;

import java.util.Scanner;

import org.apache.log4j.Logger;

import cz.agents.cycleplanner.arguments.mlc.MLCCommandLineArgumentsParser;

public class RunExperiment {

	private static Logger log = Logger.getLogger(RunExperiment.class);

	public static void main(String[] args) throws Exception {
		
		
		log.info("Write something to run an experiment...");
		Scanner scanner = new Scanner(System.in);
		scanner.next();
		scanner.close();
		log.info("Running experiment!");
		
		MLCCommandLineArgumentsParser cliParser = new MLCCommandLineArgumentsParser(args);
		Experiment experiment = new MLCExperiment(cliParser);
		experiment.run();

	}

}
