package wiktiopeggynary.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wiktiopeggynary.model.markup.CursiveBlock;
import wiktiopeggynary.model.markup.InternalLink;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.markup.KTemplate;
import wiktiopeggynary.model.markup.PlainText;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.markup.WikipediaLink;
import wiktiopeggynary.model.meaning.Meaning;
import wiktiopeggynary.model.WiktionaryEntry;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Krzysztof Witukiewicz
 */
class MeaningsParserTest extends ParserSpecBase {

    @Test
    void testText() {
        RichText text = new RichText(new PlainText("nach Einfluss oder Aufgabe gestaffeltes "),
                                     new InternalLink.Builder().withPageTitle("System").build(),
                                     new PlainText(" aus "),
                                     new InternalLink.Builder().withPageTitle("Individuum").withLinkText("Individuen")
                                                               .build());

        Meaning meaning = getMeaningFromWiktionaryEntry("Staat", 0);

        assertEquals(text, meaning.getText());
    }

    @Test
    void plainTextKTemplate() {
        KTemplate kTemplate = new KTemplate(List.of(new KTemplate.Part(new RichText("Politik"), null)),
                                            new RichText(":"));

        assertEquals(kTemplate, getMeaningFromWiktionaryEntry("Staat", 1).getText().getComponents().get(0));
    }

    @Test
    void richTextKTemplate() {
        KTemplate kTemplate = new KTemplate(
                List.of(new KTemplate.Part(
                        new RichText(new InternalLink.Builder().withPageTitle("umgangssprachlich").build(),
                                     new PlainText(", nur "),
                                     new InternalLink.Builder().withPageTitle("Plural").build()), null)),
                new RichText(":"));

        assertEquals(kTemplate, getMeaningFromWiktionaryEntry("Staat", 2).getText().getComponents().get(0));
    }

    @Test
    void kTemplateWithMultipleParts() {
        KTemplate kTemplate = (KTemplate) getMeaningFromWiktionaryEntry("Bauch", 5).getText().getComponents().get(0);

        assertEquals(3, kTemplate.parts().size());
        assertEquals(new RichText("ugs."), kTemplate.parts().get(0).text());
        assertEquals(new RichText(), kTemplate.parts().get(0).separator());
        assertEquals(new RichText("übertr."), kTemplate.parts().get(1).text());
        assertEquals(new RichText(), kTemplate.parts().get(1).separator());
        assertEquals(new RichText("zu [5]"), kTemplate.parts().get(2).text());
        assertNull(kTemplate.parts().get(2).separator());
    }

    @Test
    void kTemplateWithLooseOrderingOfParameters() {
        KTemplate kTemplate = (KTemplate) getMeaningFromWiktionaryEntry("Verpflegung", 0).getText().getComponents()
                                                                                          .get(0);

        assertEquals(new RichText(), kTemplate.suffix());
        assertEquals(1, kTemplate.parts().size());
        KTemplate.Part kontextPart = kTemplate.parts().get(0);
        assertEquals(new RichText("ohne Plural"), kontextPart.text());
        assertNull(kontextPart.separator());
    }

    @Test
    void kTemplateWithKontextAsTemplateName() {
        KTemplate kTemplate = (KTemplate) getMeaningFromWiktionaryEntry("Distribution", 0).getText().getComponents()
                                                                                          .get(0);

        assertEquals(1, kTemplate.parts().size());
        assertEquals(new RichText("Wirtschaft"), kTemplate.parts().get(0).text());
    }

    @Test
    void abkTemplate() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Steppke").getWiktionaryEntries().get(0);

        assertEquals(2, entry.getMeanings().size());
        assertEquals(new KTemplate(List.of(new KTemplate.Part(new RichText("ugs."), null)), new RichText(",")),
                     entry.getMeanings().get(0).getText().getComponents().get(0));
        assertEquals(new KTemplate(List.of(new KTemplate.Part(new RichText("ugs."), null)), new RichText(":")),
                     entry.getMeanings().get(1).getText().getComponents().get(0));
    }

    @Test
    void citationAsDetails() {
        RichText text = new RichText(
                new CursiveBlock(
                        new RichText(new InternalLink.Builder().withPageTitle("Militär").build(),
                                     new PlainText(":"))),
                new PlainText(" seegehende Einheiten einer bestimmten Größenordnung bei der Marine"));

        assertEquals(text, getMeaningFromWiktionaryEntry("Boot", 1).getText());
    }

    @ParameterizedTest(name = "Wikipedia-link in details: {0} {1} -> pageTitle=''{2}'', linkText=''{3}''")
    @MethodSource("wikiPediaLinkInDetails")
    void wikiPediaLinkInDetails(String lemma, int meaningIdx, String pageTitle, String linkText) {
        Meaning meaning = getMeaningFromWiktionaryEntry(lemma, meaningIdx);

        WikipediaLink link = (WikipediaLink) meaning.getText().getComponents().stream()
                                                    .filter(c -> c instanceof WikipediaLink)
                                                    .findFirst()
                                                    .orElseThrow();
        assertNotNull(link);
        assertEquals(pageTitle, link.pageTitle());
        assertEquals(linkText, link.linkText());
    }

    private static Stream<Arguments> wikiPediaLinkInDetails() {
        return Stream.of(
                Arguments.of("Dreieck", 0, "geometrische Figur", null),
                Arguments.of("Höhlenbär", 0, "Letzte_Kaltzeit", "letzten Kaltzeit")
        );
    }

    @Test
    void subMeaning() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Dirham").getWiktionaryEntries().get(0);

        assertEquals(2, entry.getMeanings().size());
        assertEquals(6, entry.getMeanings().get(0).getSubMeanings().size());
        assertTrue(entry.getMeanings().get(1).getSubMeanings().isEmpty());
        assertEquals(List.of(ItemNumber.singleNumber("1")), entry.getMeanings().get(0).getNumbers());
        assertEquals(List.of(ItemNumber.singleNumber("1f")),
                     entry.getMeanings().get(0).getSubMeanings().get(5).getNumbers());
        RichText text = new RichText(new PlainText("früher in "),
                                     new InternalLink.Builder().withPageTitle("Jordanien").build(),
                                     new PlainText(" (kleinere Einheit des "),
                                     new InternalLink.Builder().withPageTitle("jordanischer Dinar")
                                                               .withLinkText("jordanischen Dinars")
                                                               .build(),
                                     new PlainText(")"));
        assertEquals(text, entry.getMeanings().get(0).getSubMeanings().get(5).getText());
    }

    @Test
    void subMeaningWithAsteriskInFront() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Aberration").getWiktionaryEntries().get(0);

        assertEquals(5, entry.getMeanings().size());
        assertEquals(2, entry.getMeanings().get(0).getSubMeanings().size());
        assertEquals(List.of(ItemNumber.singleNumber("*")),
                     entry.getMeanings().get(0).getSubMeanings().get(1).getNumbers());
        RichText text = new RichText(new InternalLink.Builder().withPageTitle("täglich").build(),
                                     new PlainText("e "),
                                     new CursiveBlock(new RichText("Aberration:")),
                                     new PlainText(" aufgrund der "),
                                     new InternalLink.Builder().withPageTitle("Erdrotation").build());
        assertEquals(text, entry.getMeanings().get(0).getSubMeanings().get(1).getText());
    }

    @Test
    void meaningWithoutText() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Skizze").getWiktionaryEntries().get(0);

        assertEquals(2, entry.getMeanings().size());
        assertEquals(List.of(ItemNumber.singleNumber("1")), entry.getMeanings().get(0).getNumbers());
        assertEquals(new RichText(), entry.getMeanings().get(0).getText());
    }

    @Test
    void qsBedeutungenAtTheBeginningOfMeaningText() {
        Meaning meaning = getMeaningFromWiktionaryEntry("Lichtwoche", 0);

        assertEquals(List.of(ItemNumber.singleNumber("1")), meaning.getNumbers());
        assertEquals(new RichText("Entfernung, die das Licht in einer Woche im Vakuum zurücklegt"), meaning.getText());
    }

    @Test
    void qsBedeutungenAtTheEndOfMeaningText() {
        Meaning meaning = getMeaningFromWiktionaryEntry("Bekanntwerdung", 2);

        assertEquals(1, meaning.getText().getComponents().size());
        assertEquals(new PlainText("das Kennenlernen"), meaning.getText().getComponents().get(0));
    }

    @Test
    void refElementsShouldBeIgnoredElementWithoutAttributes() {
        WiktionaryEntry entry = parseWiktionaryEntryPage("Müller").getWiktionaryEntries().get(1);
        Meaning meaning = entry.getMeanings().get(0);

        PlainText mergedText = (PlainText) meaning.getText().getComponents().get(7);
        assertTrue(mergedText.getText().startsWith(
                " mit etwa 10,6% Anteil und über 600.000 Namenträgern häufigster Familienname in Deutschland"));
    }

    @Test
    void refElementsShouldBeIgnoredElementWithAttribute() {
        Meaning meaning = getMeaningFromWiktionaryEntry("Weltchronik", 0);

        List<?> components = meaning.getText().getComponents();
        PlainText lastComp = (PlainText) components.get(components.size() - 1);
        assertTrue(lastComp.getText().endsWith("historisch wird "));
    }

    private Meaning getMeaningFromWiktionaryEntry(String lemma, int meaningIdx) {
        WiktionaryEntry entry = parseWiktionaryEntryPage(lemma).getWiktionaryEntries().get(0);
        return entry.getMeanings().get(meaningIdx);
    }
}
