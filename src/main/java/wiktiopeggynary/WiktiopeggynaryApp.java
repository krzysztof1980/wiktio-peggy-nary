package wiktiopeggynary;

import org.apache.commons.cli.*;
import wiktiopeggynary.parser.ConcurrentParserTaskExecutorFactory;
import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.SequentialParserTaskExecutorFactory;
import wiktiopeggynary.persistence.ElasticsearchNativeClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktiopeggynaryApp {

	private final String dumpFile;
	private final boolean parallel;

	public WiktiopeggynaryApp(String dumpFile, boolean parallel) {
		this.dumpFile = dumpFile;
		this.parallel = parallel;
	}

	public static void main(String[] args) throws Exception {
		Options options = new Options();
		Option file = Option.builder("f")
		                    .longOpt("file")
		                    .desc("filename of Wiktionary's dump")
		                    .hasArg()
		                    .argName("FILE")
		                    .build();
		Option parallel = Option.builder("p")
		                        .longOpt("parallel")
		                        .desc("run parser in multiple threads")
		                        .build();
		options.addOption(file);
		options.addOption(parallel);

		CommandLineParser parser = new DefaultParser();
		CommandLine line;
		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			printHelp(options);
			return;
		}
		if (!line.hasOption(file.getOpt())) {
			printHelp(options);
			return;
		}
		WiktiopeggynaryApp app = new WiktiopeggynaryApp(line.getOptionValue(file.getOpt()),
		                                                line.hasOption(parallel.getOpt()));
		app.parseDump();
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java wiktiopeggynary.WiktiopeggynaryApp [OPTION]", options);
	}

	public void parseDump() throws IOException {
		Path dumpFilePath = Paths.get(dumpFile);
		if (!Files.exists(dumpFilePath)) {
			System.out.println("Provided file does not exist");
			return;
		}
		ParserService parserService = new ParserService(parallel
		                                                ? new ConcurrentParserTaskExecutorFactory()
		                                                : new SequentialParserTaskExecutorFactory());
		WiktionaryDumpParseManager parseManager = new WiktionaryDumpParseManager(parserService, new ElasticsearchNativeClient());
		parseManager.parse(dumpFilePath);
	}
}
