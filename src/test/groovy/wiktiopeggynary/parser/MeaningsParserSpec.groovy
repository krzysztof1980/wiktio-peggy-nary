package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.markup.ItemNumber
import wiktiopeggynary.meaning.Meaning
import wiktiopeggynary.meaning.MeaningKontext
import wiktiopeggynary.model.markup.CursiveBlock
import wiktiopeggynary.model.markup.InternalLink
import wiktiopeggynary.model.markup.PlainText
import wiktiopeggynary.model.markup.RichText

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class MeaningsParserSpec extends ParserSpecBase {

    def "without Kontext"() {
        given:
        def text = new RichText(new PlainText("nach Einfluss oder Aufgabe gestaffeltes "),
                                new InternalLink.Builder().withPageTitle("System").build(),
                                new PlainText(" aus "),
                                new InternalLink.Builder().withPageTitle("Individuum").withLinkText("Individuen").
                                        build())

        when:
        def meaning = getMeaningFromWiktionaryEntry("Staat", 0)

        then:
        meaning.kontext == null
        meaning.text == text
    }

    def "with plain text-Kontext"() {
        given:
        def kontext = new MeaningKontext(parts: [new MeaningKontext.Part(text: new RichText("Politik"))])

        expect:
        getMeaningFromWiktionaryEntry("Staat", 1).kontext == kontext
    }

    def "rich text-Kontext"() {
        given:
        def kontext = new MeaningKontext(parts: [new MeaningKontext.Part(
                text: new RichText(new InternalLink.Builder().withPageTitle("umgangssprachlich").build(),
                                   new PlainText(", nur "),
                                   new InternalLink.Builder().withPageTitle("Plural").build()))])

        expect:
        getMeaningFromWiktionaryEntry("Staat", 2).kontext == kontext
    }

    def "Kontext with multiple parts"() {
        given:
        def kontext = new MeaningKontext(parts: [
                new MeaningKontext.Part(text: new RichText("ironisch")),
                new MeaningKontext.Part(text: new RichText("scherzhaft"))])

        expect:
        getMeaningFromWiktionaryEntry("Kartoffel", 4).kontext == kontext
    }

    def "Kontext in form of a Abk-template "() {
        when:
        def entry = parseWiktionaryEntryPage("Steppke").wiktionaryEntries[0]

        then:
        entry.meanings.size() == 2
        entry.meanings[0].kontext == new MeaningKontext(parts: [new MeaningKontext.Part(text: new RichText("ugs."))],
                                                        suffix: new RichText(","))
        entry.meanings[1].kontext == new MeaningKontext(parts: [new MeaningKontext.Part(text: new RichText("ugs."))],
                                                        suffix: new RichText(":"))
    }

    def "citation as details"() {
        given:
        def text = new RichText(
                new CursiveBlock(
                        new RichText(new InternalLink.Builder().withPageTitle("Militär").build(), new PlainText(":"))),
                new PlainText(" seegehende Einheiten einer bestimmten Größenordnung bei der Marine"))

        expect:
        getMeaningFromWiktionaryEntry("Boot", 1).text == text
    }

    def "sub-meaning"() {
        when:
        def entry = parseWiktionaryEntryPage("Dirham").wiktionaryEntries[0]

        then:
        entry.meanings.size() == 2
        entry.meanings[0].subMeanings.size() == 6
        entry.meanings[1].subMeanings.isEmpty()
        entry.meanings[0].numbers == [ItemNumber.singleNumber("1")]
        entry.meanings[0].subMeanings[5].numbers == [ItemNumber.singleNumber("1f")]
        entry.meanings[0].subMeanings[5].text ==
                new RichText(new PlainText("früher in "),
                             new InternalLink.Builder().withPageTitle("Jordanien").build(),
                             new PlainText(" (kleinere Einheit des "),
                             new InternalLink.Builder().withPageTitle("jordanischer Dinar")
                                                       .withLinkText("jordanischen Dinars")
                                                       .build(),
                             new PlainText(")"))
    }

    def "sub-meaning (with '*' in front)"() {
        when:
        def entry = parseWiktionaryEntryPage("Aberration").wiktionaryEntries[0]

        then:
        entry.meanings.size() == 5
        entry.meanings[0].subMeanings.size() == 2
        entry.meanings[0].subMeanings[1].numbers == [ItemNumber.singleNumber("*")]
        entry.meanings[0].subMeanings[1].text ==
                new RichText(new InternalLink.Builder().withPageTitle("täglich").build(),
                             new PlainText("e "),
                             new CursiveBlock(new RichText("Aberration:")),
                             new PlainText(" aufgrund der "),
                             new InternalLink.Builder().withPageTitle("Erdrotation").build())
    }

    def "meaning without text"() {
        when:
        def entry = parseWiktionaryEntryPage("Skizze").wiktionaryEntries[0]

        then:
        entry.meanings.size() == 2
        entry.meanings[0].numbers == [ItemNumber.singleNumber("1")]
        entry.meanings[0].text == new RichText()
    }

    private Meaning getMeaningFromWiktionaryEntry(String lemma, int meaningIdx) {
        def entry = parseWiktionaryEntryPage(lemma).wiktionaryEntries[0]
        return entry.getMeanings().get(meaningIdx)
    }
}