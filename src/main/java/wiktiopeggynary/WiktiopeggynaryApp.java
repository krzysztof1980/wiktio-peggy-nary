package wiktiopeggynary;

import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.substantiv.FlexionForm;
import wiktiopeggynary.model.substantiv.Substantiv;
import wiktiopeggynary.parser.ParserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static java.lang.String.format;

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
            case "-r":
                InputStream resourceStream = getResourceURLFromUserInput();
                if (resourceStream != null)
                    parseSingleResource(resourceStream);
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
        Path outputPath = Paths.get(Paths.get(".").toAbsolutePath().normalize().toString(), "wiktiopeggynary-out.txt");
        System.out.println("Printing output to " + outputPath);
        try (PrintWriter out = new PrintWriter(Files.newOutputStream(outputPath))) {
            List<WiktionaryEntry> parsedEntries = new LinkedList<>();
            ParserService.getInstance().parseWiktionaryDump(
                    dumpFilePath,
                    parsedEntries::add,
                    () -> {
                    });
            out.println(format("Parsed %d entries", parsedEntries.size()));
            int[] count = {0};
            parsedEntries.stream()
                    .filter(e -> e instanceof Substantiv)
                    .map(e -> (Substantiv) e)
                    .filter(s -> s.getFlexionTable().hasUnparsedForms())
                    .forEach(s -> {
                        out.println("---------------------------------------------");
                        out.println(s);
                        s.getFlexionTable().getFlexionForms().stream()
                                .filter(FlexionForm::isUnparsed)
                                .forEach(f -> out.println("\t" + f));
                        count[0]++;
                    });
            out.println("=============================================");
            out.println(count[0] + " substantives contained flexion forms, that could not be parsed");
        }
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
        System.out.println("\tParse single resource:    -r");
    }
}
