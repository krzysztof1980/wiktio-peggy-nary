package wiktiopeggynary;

import wiktiopeggynary.client.IndexInElasticsearchClient;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.parser.ParserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Scanner;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktiopeggynaryApp {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printHelp();
            return;
        }

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

    private static void parseSingleResource(InputStream resourceStream) {
        String resourceText = new Scanner(resourceStream, "UTF-8").useDelimiter("\\A").next();
        Collection<WiktionaryEntry> wiktionaryEntries = ParserService.getInstance().parseWiktionaryPage(resourceText);
        wiktionaryEntries.stream().forEach(System.out::println);
    }

    private static void parseDump(String dumpFile) throws IOException {
        Path dumpFilePath = Paths.get(dumpFile);
        if (!Files.exists(dumpFilePath)) {
            System.out.println("Provided file does not exist");
            return;
        }
        IndexInElasticsearchClient client = new IndexInElasticsearchClient();
        ParserService.getInstance().parseWiktionaryDump(
                dumpFilePath,
                client::indexWiktionaryPage,
                client::cleanup);
    }

    private static InputStream getResourceURLFromUserInput() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Provide word from articles-de: ");
            String line = in.readLine();
            if (line.length() == 0) return null;
            String resourceName = "articles-de/" + line + ".txt";
            InputStream is = WiktiopeggynaryApp.class.getClassLoader().getResourceAsStream(resourceName);
            if (is == null)
                System.out.println("Resource with provided name does not exist!");
            else
                return is;
        }
    }

    private static void printHelp() {
        System.out.println("Command line options:");
        System.out.println("\tParse dump of Wiktionary: -f dump-file-name");
    }
}
