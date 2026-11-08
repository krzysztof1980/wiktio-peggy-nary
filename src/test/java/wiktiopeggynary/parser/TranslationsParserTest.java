package wiktiopeggynary.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.markup.CursiveBlock;
import wiktiopeggynary.model.markup.InternalLink;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.markup.LanguageVariant;
import wiktiopeggynary.model.markup.PlainText;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.markup.RichTextComponent;
import wiktiopeggynary.model.substantiv.Gender;
import wiktiopeggynary.model.substantiv.MultiGender;
import wiktiopeggynary.model.translation.Translation;
import wiktiopeggynary.model.translation.TranslationMeaning;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Krzysztof Witukiewicz
 */
class TranslationsParserTest extends ParserSpecBase {

    @Test
    void translationMeaningNumbers() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Bauch").getWiktionaryEntries().get(0);

        // "the meaning number is an integer"
        assertEquals(List.of(ItemNumber.singleNumber("1")),
                     entry.getTranslations().get("en").get(0).getMeaningNumbers());

        // "the meaning number is a list of integers"
        assertEquals(List.of(ItemNumber.singleNumber("1"), ItemNumber.singleNumber("3")),
                     entry.getTranslations().get("en").get(2).getMeaningNumbers());

        // "the meaning number is a list of integers and range"
        assertEquals(List.of(ItemNumber.singleNumber("1"),
                             ItemNumber.range("3", "4b"),
                             ItemNumber.singleNumber("5"),
                             ItemNumber.singleNumber("6")),
                     entry.getTranslations().get("en").get(1).getMeaningNumbers());
    }

    @ParameterizedTest(name = "entry[''{1}''] has {2} meanings{0}")
    @MethodSource("entryHasMeanings")
    void entryHasMeanings(String notes, String lang, int meaningCount) {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Staat").getWiktionaryEntries().get(0);

        assertEquals(meaningCount, entry.getTranslations().get(lang).size());
    }

    private static Stream<Arguments> entryHasMeanings() {
        return Stream.of(
                Arguments.of("", "it", 1),
                Arguments.of(" (separated by semicolon)", "ru", 4),
                Arguments.of(" (separated by space)", "pl", 5)
        );
    }

    @ParameterizedTest(name = "Translations with details in cursive: meaning ''{2}''{0} has {3} translations")
    @MethodSource("translationsWithDetailsInCursive")
    void translationsWithDetailsInCursive(String notes, int meaningIdx, String meaningTxt, int translationsCount) {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Staat").getWiktionaryEntries().get(0);
        List<TranslationMeaning> langTranslations = entry.getTranslations().get("pl");

        assertEquals(5, langTranslations.size());
        TranslationMeaning meaning = langTranslations.get(meaningIdx);

        assertEquals(translationsCount, meaning.getTranslations().size());
    }

    // @formatter:off
    private static Stream<Arguments> translationsWithDetailsInCursive() {
        return Stream.of(
                Arguments.of("", 0, "[1, 2] {{Ü|pl|państwo}} {{n}}", 1),
                Arguments.of(" (space separated)", 1, "[3] {{Ü|pl|Stany}} ''Pl.,'' {{Ü|pl|Stany Zjednoczone}} ''Pl.''", 2),
                Arguments.of("", 2, "[4] {{Ü|pl|kanton}} {{m}};", 1),
                Arguments.of(" (comma separated)", 3, "[5] {{Ü|pl|społeczeństwo}} {{n}}, ''Bienen, Wespen, Hummeln:'' {{Ü|pl|rój}} {{m}};", 2),
                Arguments.of("", 4, "[7] {{Ü|pl|dwór}} {{m}}", 1)
        );
    }
    // @formatter:on

    @ParameterizedTest(name = "Translations with details as links: meaning ''{2}''{0} has {3} translations")
    @MethodSource("translationsWithDetailsAsLinks")
    void translationsWithDetailsAsLinks(String notes, int meaningIdx, String meaningTxt, int translationsCount) {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Staat").getWiktionaryEntries().get(0);
        List<TranslationMeaning> langTranslations = entry.getTranslations().get("en");

        assertEquals(6, langTranslations.size());
        TranslationMeaning meaning = langTranslations.get(meaningIdx);

        assertEquals(translationsCount, meaning.getTranslations().size());
    }

    // @formatter:off
    private static Stream<Arguments> translationsWithDetailsAsLinks() {
        return Stream.of(
                Arguments.of("", 0, "[1] {{Ü|en|state}};", 1),
                Arguments.of(" (comma separated)", 1, "[2] [[national]] {{Ü|en|territory}}, {{Ü|en|country}};", 2),
                Arguments.of(" (semicolon separated)", 2, "[3] [[the]] {{Ü|en|States}}; [[the]] {{Ü|en|US}};", 2),
                Arguments.of("", 3, "[4] {{Ü|en|canton}};", 1),
                Arguments.of(" (comma before template)", 4, "[5] [[insect]] [[society]], {{Ü|en|colony}};", 1),
                Arguments.of("", 5, "[6] {{Ü|en|finery}}", 1)
        );
    }
    // @formatter:on

    @ParameterizedTest(name = "translation ''{2}'' has gender: {3}")
    @MethodSource("translationHasGender")
    void translationHasGender(int meaningIdx, int translationIdx, String translationTxt, MultiGender gender) {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Staat").getWiktionaryEntries().get(0);
        List<TranslationMeaning> langTranslations = entry.getTranslations().get("pl");

        assertEquals(gender, langTranslations.get(meaningIdx).getTranslations().get(translationIdx).getGender());
    }

    // @formatter:off
    private static Stream<Arguments> translationHasGender() {
        return Stream.of(
                Arguments.of(0, 0, "[1, 2] {{Ü|pl|państwo}} {{n}}", new MultiGender(Gender.NEUTRUM)),
                Arguments.of(1, 0, "[3] {{Ü|pl|Stany}} ''Pl.,''", null),
                Arguments.of(1, 1, "{{Ü|pl|Stany Zjednoczone}} ''Pl.''", null),
                Arguments.of(2, 0, "[4] {{Ü|pl|kanton}} {{m}};", new MultiGender(Gender.MASKULINUM)),
                Arguments.of(3, 0, "[5] {{Ü|pl|społeczeństwo}} {{n}},", new MultiGender(Gender.NEUTRUM)),
                Arguments.of(3, 1, "''Bienen, Wespen, Hummeln:'' {{Ü|pl|rój}} {{m}};", new MultiGender(Gender.MASKULINUM)),
                Arguments.of(4, 0, "[7] {{Ü|pl|dwór}} {{m}}", new MultiGender(Gender.MASKULINUM))
        );
    }
    // @formatter:on

    @ParameterizedTest(name = "content of translation in entry[''{0}''] meaning[{1}] translation[{2}]")
    @MethodSource("contentOfTranslation")
    void contentOfTranslation(String lang, int meaningIdx, int translationIdx, String translationTxt,
                              String internalLink, String transcription, String label) {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Staat").getWiktionaryEntries().get(0);
        List<TranslationMeaning> langTranslations = entry.getTranslations().get(lang);
        Translation translation = langTranslations.get(meaningIdx).getTranslations().get(translationIdx);

        assertEquals(internalLink, translation.getInternalLink());
        assertEquals(transcription, translation.getTranscription());
        assertEquals(label, translation.getLabel());
    }

    // @formatter:off
    private static Stream<Arguments> contentOfTranslation() {
        return Stream.of(
                Arguments.of("pl", 0, 0, "{{Ü|pl|państwo}}", "państwo", null, null),
                Arguments.of("ru", 0, 0, "{{Üt|ru|государство|gosudarstwo}}", "государство", "gosudarstwo", null)
        );
    }
    // @formatter:on

    @Test
    void ignoreMeaningsWithEmptyTranslation() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Tiefgang").getWiktionaryEntries().get(0);

        assertEquals(1, entry.getTranslations().size());
        assertTrue(entry.getTranslations().containsKey("en"));
    }

    @Test
    void meaningWithNonEmptyTextWithoutTranslationTemplate() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Spiel").getWiktionaryEntries().get(0);

        List<TranslationMeaning> itMeanings = entry.getTranslations().get("it");
        assertEquals(3, itMeanings.size());
        TranslationMeaning meaning3 = itMeanings.get(2);
        assertTrue(meaning3.getTranslations().isEmpty());
        assertEquals(new RichText(new InternalLink.Builder().withPageTitle("partita").build(),
                                  new PlainText(" "),
                                  new MultiGender(Gender.FEMININUM)),
                     meaning3.getText());
    }

    @Test
    void translationInsideCursiveText() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Erziehungsziel").getWiktionaryEntries().get(0);

        List<TranslationMeaning> enMeanings = entry.getTranslations().get("en");
        assertEquals(5, enMeanings.get(0).getTranslations().size());
    }

    @Test
    void complexMeaningText() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Erziehungsziel").getWiktionaryEntries().get(0);

        TranslationMeaning enMeaning1 = entry.getTranslations().get("en").get(0);
        List<RichTextComponent> components = enMeaning1.getText().getComponents();
        assertEquals(7, components.size());
        assertEquals(translation("child-rearing goal"), components.get(0));
        assertEquals(new PlainText(", "), components.get(1));
        assertEquals(translation("parenting objective"), components.get(2));
        assertEquals(new PlainText(", "), components.get(3));
        assertEquals(translation("parenting goal"), components.get(4));
        assertEquals(new PlainText("; <br />"), components.get(5));
        assertEquals(new CursiveBlock(new RichText(new PlainText("Anmerkung: "),
                                                   translation("educational objective"),
                                                   new PlainText(" und "),
                                                   translation("educational goal"),
                                                   new PlainText(" bedeuten eher „"),
                                                   new InternalLink.Builder().withPageTitle("Lernziel").build(),
                                                   new PlainText("“ als „Erziehungsziel“"))),
                     components.get(6));
    }

    @Test
    void languageVariants() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Billion").getWiktionaryEntries().get(0);

        TranslationMeaning enMeaning1 = entry.getTranslations().get("en").get(0);
        List<RichTextComponent> langVariants = enMeaning1.getText().getComponents().stream()
                                                         .filter(c -> c instanceof LanguageVariant)
                                                         .toList();
        assertEquals(2, langVariants.size());

        LanguageVariant beVariant = (LanguageVariant) langVariants.get(0);
        assertEquals(LanguageVariant.Variant.British, beVariant.variant());
        assertEquals(":", beVariant.suffix());

        LanguageVariant aeVariant = (LanguageVariant) langVariants.get(1);
        assertEquals(LanguageVariant.Variant.American, aeVariant.variant());
        assertEquals(":", aeVariant.suffix());
    }

    private static Translation translation(String internalLink) {
        Translation translation = new Translation();
        translation.setInternalLink(internalLink);
        return translation;
    }
}
