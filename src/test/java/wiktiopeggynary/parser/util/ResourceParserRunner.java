package wiktiopeggynary.parser.util;

import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.SequentialParserTaskExecutorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Krzysztof Witukiewicz
 */
public class ResourceParserRunner {

    public static void main(String[] args) throws IOException {
        String articleTitle = getArticleFromUserInput();
        ParserService parserService = new ParserService(new SequentialParserTaskExecutorFactory());
        parserService.parseWiktionaryEntryPage(ResourceUtils.readArticleFromResources(articleTitle))
                      .ifPresent(result -> result.getWiktionaryEntries().forEach(System.out::println));
    }

    private static String getArticleFromUserInput() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Provide word from articles-de: ");
            String line = reader.readLine();
            if (line.length() == 0)
                return null;
            if (ResourceUtils.hasResourceForArticle(line)) {
                return line;
            } else {
                System.out.println("Resource with provided name does not exist!");
            }
        }
    }
}
