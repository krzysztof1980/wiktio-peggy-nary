package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.Meaning
import wiktiopeggynary.model.markup.CursiveBlock
import wiktiopeggynary.model.markup.InternalLink
import wiktiopeggynary.model.markup.PlainText
import wiktiopeggynary.model.markup.RichText

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class MeaningsParserSpec extends ParserSpecBase {

    def "simple case"() {
        given:
        def expectedMeaning = new RichText("Gebiet, auf dem ein Staat liegt")

        expect:
        getMeaningFromWiktionaryEntry("Staat", 1).text == expectedMeaning
    }

    def "template as details"() {
        given:
        def expectedMeaning = new RichText(
                new PlainText("{{K}} die "),
                new InternalLink.Builder().withPageTitle("Vereinigte Staaten von Amerika")
                                          .withLinkText("Vereinigten Staaten von Amerika")
                                          .build());

        expect:
        getMeaningFromWiktionaryEntry("Staat", 2).text == expectedMeaning
    }

    def "templates as details"() {
        given:
        def expectedMeaning = new RichText(
                new PlainText("{{iron.}} {{scherzh.}} großes "),
                new InternalLink.Builder().withPageTitle("Loch").build(),
                new PlainText(" im "),
                new InternalLink.Builder().withPageTitle("Strumpf").build());

        expect:
        getMeaningFromWiktionaryEntry("Kartoffel", 4).text == expectedMeaning
    }

    def "citation as details"() {
        given:
        def expectedMeaning = new RichText(
                new CursiveBlock(
                        new RichText(new InternalLink.Builder().withPageTitle("Militär").build(), new PlainText(":"))),
                new PlainText(" seegehende Einheiten einer bestimmten Größenordnung bei der Marine"));

        expect:
        getMeaningFromWiktionaryEntry("Boot", 1).text == expectedMeaning
    }

    def "sub-meaning"() {
        given:
        def expectedMeaning = new RichText(
                new PlainText("in "),
                new InternalLink.Builder().withPageTitle("Katar").build(),
                new PlainText(" (kleinere Einheit des "),
                new InternalLink.Builder().withPageTitle("Katar-Riyal").withLinkText("Katar-Riyals").build(),
                new PlainText(")"));

        expect:
        getMeaningFromWiktionaryEntry("Dirham", 3).text == expectedMeaning
    }

    def "sub-meaning (with '*' in front)"() {
        given:
        def expectedMeaning = new RichText(
                new InternalLink.Builder().withPageTitle("jährlich").build(),
                new PlainText("e "),
                new CursiveBlock(new RichText("Aberration:")),
                new PlainText(" aufgrund des "),
                new InternalLink.Builder().withPageTitle("Erdumlauf").build(),
                new PlainText("s um die "),
                new InternalLink.Builder().withPageTitle("Sonne").build());

        expect:
        getMeaningFromWiktionaryEntry("Aberration", 1).text == expectedMeaning
    }

    private Meaning getMeaningFromWiktionaryEntry(String lemma, int meaningIdx) {
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources(lemma), templateService).wiktionaryEntries[0]
        return entry.getMeanings().get(meaningIdx)
    }
}