package wiktiopeggynary.parser

import spock.lang.Unroll
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
        def expectedMeaning = new Meaning(text: text)

        expect:
        getMeaningFromWiktionaryEntry("Staat", 0) == expectedMeaning
    }

    def "with plain text-Kontext"() {
        given:
        def kontext = new MeaningKontext(parts: [new MeaningKontext.Part(text: new RichText(new PlainText("Politik")))])
        def text = new RichText("Gebiet, auf dem ein Staat liegt")
        def expectedMeaning = new Meaning(kontext: kontext, text: text)

        expect:
        getMeaningFromWiktionaryEntry("Staat", 1) == expectedMeaning
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

    def "citation as details"() {
        given:
        def text = new RichText(
                new CursiveBlock(
                        new RichText(new InternalLink.Builder().withPageTitle("Militär").build(), new PlainText(":"))),
                new PlainText(" seegehende Einheiten einer bestimmten Größenordnung bei der Marine"))

        expect:
        getMeaningFromWiktionaryEntry("Boot", 1).text == text
    }

    // TODO: consider sub meanings, otherwise it does not make sense
    def "sub-meaning"() {
        given:
        def text = new RichText(
                new PlainText("in "),
                new InternalLink.Builder().withPageTitle("Katar").build(),
                new PlainText(" (kleinere Einheit des "),
                new InternalLink.Builder().withPageTitle("Katar-Riyal").build(),
                new PlainText("s)"))

        expect:
        getMeaningFromWiktionaryEntry("Dirham", 3).text == text
    }

    def "sub-meaning (with '*' in front)"() {
        given:
        def text = new RichText(
                new InternalLink.Builder().withPageTitle("jährlich").build(),
                new PlainText("e "),
                new CursiveBlock(new RichText("Aberration:")),
                new PlainText(" aufgrund des "),
                new InternalLink.Builder().withPageTitle("Erdumlauf").build(),
                new PlainText("s um die "),
                new InternalLink.Builder().withPageTitle("Sonne").build())

        expect:
        getMeaningFromWiktionaryEntry("Aberration", 1).text == text
    }

    private Meaning getMeaningFromWiktionaryEntry(String lemma, int meaningIdx) {
        def entry = parseWiktionaryEntryPage(lemma).wiktionaryEntries[0]
        return entry.getMeanings().get(meaningIdx)
    }
}