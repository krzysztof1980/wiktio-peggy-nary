package wiktiopeggynary.model;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.meaning.Meaning;
import wiktiopeggynary.model.substantiv.Flexion;
import wiktiopeggynary.model.substantiv.FlexionForm;
import wiktiopeggynary.model.substantiv.Substantiv;
import wiktiopeggynary.model.translation.TranslationMeaning;
import wiktiopeggynary.parser.ParserSpecBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Krzysztof Witukiewicz
 */
class WiktionaryEntrySerializerTest extends ParserSpecBase {

    private final WiktionaryEntrySerializer serializer = new WiktionaryEntrySerializer();

    @Test
    void genericEntry() {
        Substantiv entry = getWiktionaryEntry("Kartoffel", 0, Substantiv.class);

        // "lemma is set"
        assertFalse(StringUtils.isBlank(entry.getLemma()));

        // "translations are set"
        assertFalse(entry.getTranslations().isEmpty());
        TranslationMeaning enTransMeaning0 = entry.getTranslations().get("en").get(0);
        assertEquals(List.of(ItemNumber.singleNumber("1")), enTransMeaning0.getMeaningNumbers());
        assertEquals("potato", enTransMeaning0.getTranslations().get(0).getInternalLink());

        // "meanings are set"
        assertFalse(entry.getMeanings().isEmpty());
        Meaning meaning = entry.getMeanings().get(4);
        assertEquals(List.of(ItemNumber.singleNumber("5")), meaning.getNumbers());
        assertEquals(5, meaning.getText().getComponents().size());
    }

    @Test
    void complexSubstantiv() {
        Substantiv entry = getWiktionaryEntry("Boot", 0, Substantiv.class);

        // "gender is set"
        assertTrue(entry.getGender().getGenders().length > 0);

        // "Singular flexion forms are set"
        List<FlexionForm> sgFlexionForms = entry.getFlexionForms(Numerus.Singular);
        assertFalse(sgFlexionForms.isEmpty());
        Flexion sgDativ = sgFlexionForms.get(0).getFlexions().stream()
                                        .filter(f -> f.getKasus() == Kasus.Dativ)
                                        .findFirst()
                                        .orElseThrow();
        assertEquals(2, sgDativ.getVariants().size());
        assertEquals("Boote", sgDativ.getVariants().get(1));

        // "Plural flexion forms are set"
        List<FlexionForm> plFlexionForms = entry.getFlexionForms(Numerus.Plural);
        assertFalse(plFlexionForms.isEmpty());
        assertEquals(2, plFlexionForms.size());
        Flexion plDativ = plFlexionForms.get(1).getFlexions().stream()
                                        .filter(f -> f.getKasus() == Kasus.Dativ)
                                        .findFirst()
                                        .orElseThrow();
        assertEquals(1, plDativ.getVariants().size());
        assertEquals("Böten", plDativ.getVariants().get(0));
    }

    @Test
    void substantivWithAttributes() {
        Substantiv entry = getWiktionaryEntry("Kim", 0, Substantiv.class);

        assertFalse(entry.getAttributes().isEmpty());
        assertEquals("Vorname", entry.getAttributes().iterator().next());
    }

    private <T extends WiktionaryEntry> T getWiktionaryEntry(String lemma, int idx, Class<T> valueType) {
        @SuppressWarnings("unchecked")
        T entry = (T) parseWiktionaryEntryPage(lemma).getWiktionaryEntries().get(idx);
        String jsonString = serializer.serializeWiktionaryEntry(entry);
        return serializer.deserializeWiktionaryEntry(jsonString, valueType);
    }
}
