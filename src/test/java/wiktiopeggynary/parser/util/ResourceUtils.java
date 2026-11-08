package wiktiopeggynary.parser.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Krzysztof Witukiewicz
 */
public final class ResourceUtils {

    public static String readArticleFromResources(String lemma) {
        return readResource("/articles-de/" + filenameFromLemma(lemma));
    }

    public static boolean hasResourceForArticle(String lemma) {
        return ResourceUtils.class.getResource("/articles-de/" + filenameFromLemma(lemma)) != null;
    }

    public static String readTemplateDefinitionFromResources(String lemma) {
        return readResource("/templates/" + filenameFromLemma(lemma));
    }

    private static String readResource(String path) {
        URL url = ResourceUtils.class.getResource(path);
        try (InputStream in = url.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String filenameFromLemma(String lemma) {
        return lemma.replaceAll(" ", "_") + ".txt";
    }
}
