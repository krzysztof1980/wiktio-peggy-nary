package wiktiopeggynary.parser.dumpparser;

/**
 * @author Krzysztof Witukiewicz
 */
public final class WiktionaryAutocorrectionService {

    private static final WiktionaryAutocorrectionService instance = new WiktionaryAutocorrectionService();

    private static final String CURSIVE_TEXT = "(''.+?)([,:]\\s*)('')";

    private WiktionaryAutocorrectionService() {
    }

    public static WiktionaryAutocorrectionService getInstance() {
        return instance;
    }

    public String correctPage(String page) {
        // add any other automatic corrections here
        return correctTrailingDelimiterCatchedInCursiveText(page);
    }

    private String correctTrailingDelimiterCatchedInCursiveText(String page) {
        return page.replaceAll(CURSIVE_TEXT, "$1$3$2");
    }
}
