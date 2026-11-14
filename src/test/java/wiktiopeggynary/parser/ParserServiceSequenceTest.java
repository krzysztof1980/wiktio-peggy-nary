package wiktiopeggynary.parser;

import org.junit.jupiter.api.Test;
import wiktiopeggynary.persistence.WiktionaryEntryEsRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources;

/**
 * @author Krzysztof Witukiewicz
 */
class ParserServiceSequenceTest extends ParserSpecBase {

    @Test
    void parseResultCarriesZeroSequenceWhenParsedStandalone() {
        var page = readArticleFromResources("Boot");
        var result = parserService.parseWiktionaryEntryPage(page).orElseThrow();
        assertEquals(0L, result.getSequence());
        assertEquals(3, result.getWiktionaryEntries().size()); // Boot: n, m, surname
    }

    @Test
    void documentIdIsLexicographicallySortable() {
        assertEquals("000000000007-02", WiktionaryEntryEsRepository.documentId(7L, 2));
        assertTrue(WiktionaryEntryEsRepository.documentId(6L, 9)
                .compareTo(WiktionaryEntryEsRepository.documentId(7L, 0)) < 0);
    }
}
