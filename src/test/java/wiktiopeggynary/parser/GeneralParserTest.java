package wiktiopeggynary.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import wiktiopeggynary.model.ReferenceWiktionaryEntry;
import wiktiopeggynary.model.WiktionaryEntry;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserTest extends ParserSpecBase {

    @ParameterizedTest(name = "newline: {0}")
    @CsvSource({"\"empty line at the begin of file\",Unabwendbarkeit"})
    void newline(String testcase, String lemma) {
        assertFalse(parseWiktionaryEntryPage(lemma).getWiktionaryEntries().isEmpty());
    }

    @Test
    void createEntityProSection() {
        assertEquals(3, parseWiktionaryEntryPage("Boot").getWiktionaryEntries().size());
    }

    @Test
    void parseOnlyGermanSections() {
        assertEquals(2, parseWiktionaryEntryPage("Kim").getWiktionaryEntries().size());
    }

    @Test
    void noTranslations() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("SQL").getWiktionaryEntries();

        assertEquals(1, entries.size());
        assertEquals(0, entries.get(0).getTranslations().size());
        assertEquals(1, entries.get(0).getMeanings().size());
    }

    @ParameterizedTest(name = "old spellings: ''{0}'' -> ''{1}'' ({2})")
    @MethodSource("oldSpellings")
    void oldSpellings(String lemma, String referenceValue, String referenceType) {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage(lemma).getWiktionaryEntries();

        assertEquals(1, entries.size());
        assertInstanceOf(ReferenceWiktionaryEntry.class, entries.get(0));
        ReferenceWiktionaryEntry entry = (ReferenceWiktionaryEntry) entries.get(0);
        assertEquals(lemma, entry.getLemma());
        assertEquals(referenceValue, entry.getReferenceValue());
        assertEquals(referenceType, entry.getReferenceType().getReferenceName());
    }

    private static Stream<Arguments> oldSpellings() {
        return Stream.of(
                Arguments.of("Gemse", "Gämse", "Alte Schreibweise"),
                Arguments.of("Scheisskerl", "Scheißkerl", "Schweizer und Liechtensteiner Schreibweise"),
                Arguments.of("Stilleben", "Stillleben", "Alte Schreibweise"),
                Arguments.of("Weisse-Kragen-Kriminalität", "Weiße-Kragen-Kriminalität",
                             "Schweizer und Liechtensteiner Schreibweise"),
                Arguments.of("Citrusfrucht", "Zitrusfrucht", "Alternative Schreibweise"),
                Arguments.of("Egoshooter", "Ego-Shooter", "Alternative Schreibweise"),
                Arguments.of("Photo", "Foto", "Alternative Schreibweise")
        );
    }

    @ParameterizedTest(name = "lemma == ''{0}''")
    @CsvSource("Audio Designer")
    void lemmaEqualsLemma(String lemma) {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage(lemma).getWiktionaryEntries();

        assertEquals(1, entries.size());
        assertEquals(lemma, entries.get(0).getLemma());
    }
}
