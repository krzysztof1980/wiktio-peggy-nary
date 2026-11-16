package wiktiopeggynary.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import wiktiopeggynary.model.Numerus;
import wiktiopeggynary.model.ReferenceWiktionaryEntry;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.meaning.Meaning;
import wiktiopeggynary.model.substantiv.Gender;
import wiktiopeggynary.model.substantiv.Substantiv;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void parsePluraTantumHeadlessOfGenderMarker() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Calisthenics").getWiktionaryEntries();

        assertEquals(1, entries.size());
        Substantiv calisthenics = assertInstanceOf(Substantiv.class, entries.get(0));
        assertTrue(calisthenics.getGender().isSameAs(Gender.PLURAL));
        assertEquals(1, calisthenics.getMeanings().size());
    }

    @Test
    void parsesGenusRowWithParenthesizedSuffix() {
        Substantiv laufshorts = assertInstanceOf(Substantiv.class,
                parseWiktionaryEntryPage("Laufshorts").getWiktionaryEntries().get(0));

        assertEquals(Gender.FEMININUM, laufshorts.getFlexionForms(Numerus.Singular).get(0).getGender());
    }

    @Test
    void parsesPageWithoutAnyGenderInfo() {
        Substantiv matsuInseln = assertInstanceOf(Substantiv.class,
                parseWiktionaryEntryPage("Matsu-Inseln").getWiktionaryEntries().get(0));

        assertNull(matsuInseln.getGender());
        assertEquals(1, matsuInseln.getMeanings().size());
        assertEquals(0, matsuInseln.getFlexionForms(Numerus.Singular).size());
    }

    @Test
    void parsesColonGroupedMeanings() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Quatsch").getWiktionaryEntries();

        assertEquals(1, entries.size());
        Substantiv quatsch = assertInstanceOf(Substantiv.class, entries.get(0));
        assertEquals(1, quatsch.getMeanings().size());

        Meaning colloquialLabel = quatsch.getMeanings().get(0);
        assertNull(colloquialLabel.getNumbers());
        assertEquals(5, colloquialLabel.getSubMeanings().size());
        assertEquals(List.of(ItemNumber.singleNumber("1")), colloquialLabel.getSubMeanings().get(0).getNumbers());
        assertEquals(List.of(ItemNumber.singleNumber("5")), colloquialLabel.getSubMeanings().get(4).getNumbers());
    }

    @Test
    void parsesGroupedMeanings() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Baden").getWiktionaryEntries();

        assertEquals(2, entries.size());
        Substantiv bathing = assertInstanceOf(Substantiv.class, entries.get(0));
        assertEquals(1, bathing.getMeanings().size());

        Substantiv toponym = assertInstanceOf(Substantiv.class, entries.get(1));
        List<Meaning> meanings = toponym.getMeanings();
        assertEquals(11, meanings.size());

        Meaning badenRegionGroup = meanings.get(0);
        assertNull(badenRegionGroup.getNumbers());
        assertTrue(badenRegionGroup.getText().toString().contains("Südwesten"));
        assertEquals(3, badenRegionGroup.getSubMeanings().size());
        assertEquals(List.of(ItemNumber.singleNumber("1")), badenRegionGroup.getSubMeanings().get(0).getNumbers());
        assertEquals(List.of(ItemNumber.singleNumber("3")), badenRegionGroup.getSubMeanings().get(2).getNumbers());

        assertEquals(List.of(ItemNumber.singleNumber("4")), meanings.get(1).getNumbers());
        assertEquals(0, meanings.get(4).getSubMeanings().size());

        Meaning aargauGroup = meanings.get(5);
        assertEquals(3, aargauGroup.getSubMeanings().size());
        assertEquals(List.of(ItemNumber.singleNumber("8")), aargauGroup.getSubMeanings().get(0).getNumbers());

        assertEquals(List.of(ItemNumber.singleNumber("11")), meanings.get(6).getNumbers());
        assertEquals(List.of(ItemNumber.singleNumber("15")), meanings.get(10).getNumbers());
    }

    @Test
    void parseTeilPageFromDump() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Teil").getWiktionaryEntries();

        assertEquals(2, entries.size());
        Substantiv stueckTeil = assertInstanceOf(Substantiv.class, entries.get(0));
        assertTrue(stueckTeil.getGender().isSameAs(Gender.NEUTRUM));
        assertEquals("Stück", stueckTeil.getMeaningAnnotation());
        assertEquals(3, stueckTeil.getMeanings().size());
        Substantiv mathTeil = assertInstanceOf(Substantiv.class, entries.get(1));
        assertTrue(mathTeil.getGender().isSameAs(Gender.MASKULINUM));
        assertEquals("Untermenge", mathTeil.getMeaningAnnotation());
        assertEquals(3, mathTeil.getMeanings().size());
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
