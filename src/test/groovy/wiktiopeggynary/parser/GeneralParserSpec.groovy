package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.ObsoleteWiktionaryEntry

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserSpec extends ParserSpecBase {

    @Unroll
    def "newline: #testcase"() {
        expect:
        !parserService.parseWiktionaryEntryPage(readArticleFromResources(lemma), templateService).wiktionaryEntries.
                isEmpty()

        where:
        testcase                          | lemma
        "empty line at the begin of file" | "Unabwendbarkeit"
    }

    def "create entity pro section"() {
        expect:
        parserService.parseWiktionaryEntryPage(readArticleFromResources("Boot"), templateService).wiktionaryEntries.
                size() == 3
    }

    def "parse only german sections"() {
        expect:
        parserService.parseWiktionaryEntryPage(readArticleFromResources("Kim"), templateService).wiktionaryEntries.
                size() == 2
    }

    @Unroll
    def "old spellings: '#oldSpelling' -> '#correctSpelling' (#description)"() {
        when:
        def entries = parserService.
                parseWiktionaryEntryPage(readArticleFromResources(lemma), templateService).wiktionaryEntries

        then:
        entries.size() == 1
        entries[0] instanceof ObsoleteWiktionaryEntry
        def entry = entries[0] as ObsoleteWiktionaryEntry
        entry.lemma == lemma
        entry.obsoleteSpelling == oldSpelling
        entry.correctSpelling == correctSpelling
        entry.description == description

        where:
        lemma                        || oldSpelling                  | correctSpelling             | description
        "Gemse"                      || "Gemse"                      | "Gämse"                     | "Alte Schreibweise"
        "Scheisskerl"                || "Scheisskerl"                | "Scheißkerl"                | "Schweizer und Liechtensteiner Schreibweise"
        "Stilleben"                  || "Stilleben"                  | "Stillleben"                | "Alte Schreibweise"
        "Weisse-Kragen-Kriminalität" || "Weisse-Kragen-Kriminalität" | "Weiße-Kragen-Kriminalität" | "Schweizer und Liechtensteiner Schreibweise"
    }

    @Unroll
    def "lemma == '#lemma'"() {
        when:
        def entries = parserService.
                parseWiktionaryEntryPage(readArticleFromResources(lemma), templateService).wiktionaryEntries

        then:
        entries.size() == 1
        entries[0].lemma == lemma

        where:
        lemma << ["Audio Designer"]
    }
}
