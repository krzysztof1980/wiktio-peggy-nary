package wiktiopeggynary.parser.util

/**
 * @author Krzysztof Witukiewicz
 */
final class ResourceUtils {

    static String readArticleFromResources(String lemma) {
        return this.getClass().getResource("/articles-de/${filenameFromLemma(lemma)}").text
    }

    static boolean hasResourceForArticle(String lemma) {
        return this.getClass().getResource("/articles-de/${filenameFromLemma(lemma)}") != null
    }

    static String readTemplateDefinitionFromResources(String lemma) {
        return this.getClass().getResource("/templates/${filenameFromLemma(lemma)}").text
    }

    private static String filenameFromLemma(String lemma) {
        return lemma.replaceAll(" ", "_") + ".txt"
    }
}
