package wiktiopeggynary;

import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.dumpparser.WiktionaryAutocorrectionService;
import wiktiopeggynary.persistence.ElasticsearchClient;
import wiktiopeggynary.util.ServiceLocator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktiopeggynaryApp {

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			printHelp();
			return;
		}

		configure();

		switch (args[0]) {
			case "-f":
				if (args.length == 2)
					parseDump(args[1]);
				else
					printHelp();
				break;
			default:
				printHelp();
		}
	}

	private static void configure() {
		ServiceLocator.loadService(new ParserService());
		ServiceLocator.loadService(new WiktionaryAutocorrectionService());
	}

	private static void parseDump(String dumpFile) throws IOException {
		Path dumpFilePath = Paths.get(dumpFile);
		if (!Files.exists(dumpFilePath)) {
			System.out.println("Provided file does not exist");
			return;
		}
		WiktionaryDumpParseManager parseManager = new WiktionaryDumpParseManager(new ElasticsearchClient());
		parseManager.parse(dumpFilePath);
	}

	private static void printHelp() {
		System.out.println("Command line options:");
		System.out.println("\tParse dump of Wiktionary: -f dump-file-name");
	}
}
