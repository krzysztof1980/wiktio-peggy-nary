package wiktiopeggynary.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wiktiopeggynary.model.Kasus;
import wiktiopeggynary.model.Numerus;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.substantiv.Flexion;
import wiktiopeggynary.model.substantiv.FlexionForm;
import wiktiopeggynary.model.substantiv.Gender;
import wiktiopeggynary.model.substantiv.Substantiv;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Krzysztof Witukiewicz
 */
class SubstantivParserTest extends ParserSpecBase {

    @Test
    void genderAndOtherAttributes() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Boot").getWiktionaryEntries();

        // "there are 3 entries"
        assertEquals(3, entries.size());

        // "first entry is Neutrum"
        Substantiv entry1 = (Substantiv) entries.get(0);
        assertTrue(entry1.getGender().isSameAs(Gender.NEUTRUM));
        assertTrue(entry1.getAttributes().isEmpty());

        // "second entry is Maskulinum"
        Substantiv entry2 = (Substantiv) entries.get(1);
        assertTrue(entry2.getGender().isSameAs(Gender.MASKULINUM));
        assertTrue(entry2.getAttributes().isEmpty());

        // "third entry can be Maskulinum or Femininum and is a name"
        Substantiv entry3 = (Substantiv) entries.get(2);
        assertEquals(2, entry3.getGender().getGenders().length);
        assertEquals(Gender.MASKULINUM, entry3.getGender().getGenders()[0]);
        assertEquals(Gender.FEMININUM, entry3.getGender().getGenders()[1]);
        List<String> attributes3 = List.copyOf(entry3.getAttributes());
        assertEquals(1, attributes3.size());
        assertEquals("Nachname", attributes3.get(0));
    }

    @Test
    void adjektivischeDeklination() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Bahnangestellter").getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);

        // "it is Maskulinum"
        assertTrue(entry.getGender().isSameAs(Gender.MASKULINUM));

        // "there is attribute for adjektivische Deklination"
        List<String> attributes = List.copyOf(entry.getAttributes());
        assertEquals(1, attributes.size());
        assertEquals(Substantiv.ATTR_ADJ_DEKLINATION, attributes.get(0));
    }

    @Test
    void adjektivischeDeklinationWithError() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Suchende").getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);

        // "it is Femininum"
        assertTrue(entry.getGender().isSameAs(Gender.FEMININUM));

        // "there is attribute for adjektivische Deklination"
        List<String> attributes = List.copyOf(entry.getAttributes());
        assertEquals(1, attributes.size());
        assertEquals(Substantiv.ATTR_ADJ_DEKLINATION, attributes.get(0));
    }

    @Test
    void flexionTableWithMultipleVariantsOfSingleForm() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Zug").getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);
        List<FlexionForm> flexionForms = entry.getFlexionForms(Numerus.Singular);

        assertEquals(1, flexionForms.size());
        FlexionForm flexionForm1 = flexionForms.get(0);
        assertEquals(Gender.MASKULINUM, flexionForm1.getGender());
        Flexion genitiv = flexionForms.get(0).getFlexions().get(1);
        assertEquals(Kasus.Genitiv, genitiv.getKasus());
        assertEquals(2, genitiv.getVariants().size());
        assertEquals("Zuges", genitiv.getVariants().get(0));
        assertEquals("Zugs", genitiv.getVariants().get(1));
    }

    @Test
    void flexionTableWithMultipleFlexionForms() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Boot").getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);
        List<FlexionForm> flexionForms = entry.getFlexionForms(Numerus.Plural);

        assertEquals(2, flexionForms.size());

        // first form of Nominativ Plural
        FlexionForm flexionForm1 = flexionForms.get(0);
        assertEquals(Gender.PLURAL, flexionForm1.getGender());
        Flexion nominativ1 = flexionForm1.getFlexions().get(0);
        assertEquals(Kasus.Nominativ, nominativ1.getKasus());
        assertEquals(1, nominativ1.getVariants().size());
        assertEquals("Boote", nominativ1.getVariants().get(0));

        // second form of Nominativ Plural
        FlexionForm flexionForm2 = flexionForms.get(1);
        assertEquals(Gender.PLURAL, flexionForm2.getGender());
        Flexion nominativ2 = flexionForm2.getFlexions().get(0);
        assertEquals(Kasus.Nominativ, nominativ2.getKasus());
        assertEquals(1, nominativ2.getVariants().size());
        assertEquals("Böte", nominativ2.getVariants().get(0));
    }

    @Test
    void flexionTableWithMultipleVariantsInFirstOf2SingularForms() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Monat").getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);
        List<FlexionForm> flexionForms = entry.getFlexionForms(Numerus.Singular);

        assertEquals(2, flexionForms.size());

        // first form of Genitiv Singular
        FlexionForm flexionForm1 = flexionForms.get(0);
        assertEquals(Gender.MASKULINUM, flexionForm1.getGender());
        Flexion genitiv1 = flexionForm1.getFlexions().get(1);
        assertEquals(Kasus.Genitiv, genitiv1.getKasus());
        assertEquals(2, genitiv1.getVariants().size());
        assertEquals("Monates", genitiv1.getVariants().get(0));
        assertEquals("Monats", genitiv1.getVariants().get(1));

        // second form of Genitiv Singular
        FlexionForm flexionForm2 = flexionForms.get(1);
        assertEquals(Gender.NEUTRUM, flexionForm2.getGender());
        Flexion genitiv2 = flexionForm2.getFlexions().get(1);
        assertEquals(Kasus.Genitiv, genitiv2.getKasus());
        assertEquals(1, genitiv2.getVariants().size());
        assertEquals("Monats", genitiv2.getVariants().get(0));
    }

    @Test
    void noGenderDefinedInTheFlexionTable() {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage("Oachkatzlschwoaf").getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);
        List<FlexionForm> flexionForms = entry.getFlexionForms(Numerus.Singular);

        // "there is one flexion form for Singular"
        assertEquals(1, flexionForms.size());
        FlexionForm flexionForm1 = flexionForms.get(0);

        // "its gender is set to the gender of Substantiv"
        assertTrue(entry.getGender().isSameAs(Gender.MASKULINUM));
        assertEquals(Gender.MASKULINUM, flexionForm1.getGender());
    }

    @ParameterizedTest(name = "{0} has no singular form - gender not specified in header and {1}")
    @CsvSource({
            "Eltern,\"Genus=0 in the flexion table\"",
            "Everglades,\"Genus=x in the flexion table\"",
            "Seschellen,\"Genus=pl in the flexion table\""
    })
    void noSingularFormGenderNotSpecifiedInHeader(String lemma, String testcase) {
        List<WiktionaryEntry> entries = parseWiktionaryEntryPage(lemma).getWiktionaryEntries();
        Substantiv entry = (Substantiv) entries.get(0);

        assertTrue(entry.getGender().isSameAs(Gender.PLURAL));
    }
}
