package wiktiopeggynary.parser;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources;

/**
 * @author Krzysztof Witukiewicz
 */
public class ParserSpecBase {

    ParserService parserService;

    @BeforeEach
    void setUp() {
        parserService = new ParserService(new ConcurrentParserTaskExecutorFactory(1));
    }

    public WiktionaryEntryPageParseResult parseWiktionaryEntryPage(String lemma) {
        var optResult = parserService.parseWiktionaryEntryPage(readArticleFromResources(lemma));
        assertTrue(optResult.isPresent());
        return optResult.get();
    }
}
