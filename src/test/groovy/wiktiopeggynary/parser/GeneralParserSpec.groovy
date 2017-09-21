package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.ReferenceWiktionaryEntry

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
    def "old spellings: '#lemma' -> '#referenceValue' (#referenceType)"() {
        when:
        def entries = parseWiktionaryEntryPage(lemma).wiktionaryEntries

        then:
        entries.size() == 1
        entries[0] instanceof ReferenceWiktionaryEntry
        def entry = entries[0] as ReferenceWiktionaryEntry
        entry.lemma == lemma
        entry.referenceValue == referenceValue
        entry.referenceType.referenceName == referenceType

        where:
        lemma                        || referenceValue              | referenceType
        "Gemse"                      || "Gämse"                     | "Alte Schreibweise"
        "Scheisskerl"                || "Scheißkerl"                | "Schweizer und Liechtensteiner Schreibweise"
        "Stilleben"                  || "Stillleben"                | "Alte Schreibweise"
        "Weisse-Kragen-Kriminalität" || "Weiße-Kragen-Kriminalität" | "Schweizer und Liechtensteiner Schreibweise"
        "Citrusfrucht"               || "Zitrusfrucht"              | "Alternative Schreibweise"
        "Egoshooter"                 || "Ego-Shooter"               | "Alternative Schreibweise"
        "Photo"                      || "Foto"                      | "Alternative Schreibweise"
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
