package wiktiopeggynary.parser.util

/**
 * @author Krzysztof Witukiewicz
 */
final class ResourceUtils {

    static String readArticleFromResources(String title) {
        return this.getClass().getResource("/articles-de/${title}.txt").text
    }

    static boolean hasResourceForArticle(String title) {
        return this.getClass().getResource("/articles-de/${title}.txt") != null
    }
}
