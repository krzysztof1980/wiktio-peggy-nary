package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.ObsoleteWiktionaryEntry

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserSpec extends ParserSpecBase {

    @Unroll
    def "newline: #testcase"() {
        expect:
        !parseWiktionaryEntryPage(lemma).wiktionaryEntries.isEmpty()

        where:
        testcase                          | lemma
        "empty line at the begin of file" | "Unabwendbarkeit"
    }

    def "create entity pro section"() {
        expect:
        parseWiktionaryEntryPage("Boot").wiktionaryEntries.size() == 3
    }

    def "parse only german sections"() {
        expect:
        parseWiktionaryEntryPage("Kim").wiktionaryEntries.size() == 2
    }

    @Unroll
    def "old spellings: '#oldSpelling' -> '#correctSpelling' (#description)"() {
        when:
        def entries = parseWiktionaryEntryPage(lemma).wiktionaryEntries

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
        def entries = parseWiktionaryEntryPage(lemma).wiktionaryEntries

        then:
        entries.size() == 1
        entries[0].lemma == lemma

        where:
        lemma << ["Audio Designer"]
    }
}
