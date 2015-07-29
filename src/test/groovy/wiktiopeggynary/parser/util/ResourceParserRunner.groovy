package wiktiopeggynary.parser.util

import wiktiopeggynary.parser.ParserService

/**
 * @author Krzysztof Witukiewicz
 */
class ResourceParserRunner {

    static void main(String[] args) {
        String articleTitle = getArticleFromUserInput()
        def wiktionaryEntries = ParserService.getInstance().parseWiktionaryPage(ResourceUtils.readArticleFromResources(articleTitle))
        wiktionaryEntries.each {
            e -> println(e)
        }
    }

    private static String getArticleFromUserInput() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))
        while (true) {
            System.out.print("Provide word from articles-de: ");
            String line = reader.readLine();
            if (line.length() == 0)
                return null
            if (ResourceUtils.hasResourceForArticle(line)) {
                return line
            } else {
                System.out.println("Resource with provided name does not exist!");
            }
        }
    }
}
