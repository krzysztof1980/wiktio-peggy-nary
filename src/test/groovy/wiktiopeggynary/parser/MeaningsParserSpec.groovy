package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.markup.*
import wiktiopeggynary.model.meaning.Meaning

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class MeaningsParserSpec extends ParserSpecBase {

    def "test text"() {
        given:
        def text = new RichText(new PlainText("nach Einfluss oder Aufgabe gestaffeltes "),
                                new InternalLink.Builder().withPageTitle("System").build(),
                                new PlainText(" aus "),
                                new InternalLink.Builder().withPageTitle("Individuum").withLinkText("Individuen").
                                        build())

        when:
        def meaning = getMeaningFromWiktionaryEntry("Staat", 0)

        then:
        meaning.text == text
    }

    def "plain text-KTemplate"() {
        given:
        def kTemplate = new KTemplate(parts: [new KTemplate.Part(text: new RichText("Politik"))],
                                      suffix: new RichText(":"))

        expect:
        getMeaningFromWiktionaryEntry("Staat", 1).text.components[0] == kTemplate
    }

    def "rich text-KTemplate"() {
        given:
        def kTemplate = new KTemplate(parts: [new KTemplate.Part(
                text: new RichText(new InternalLink.Builder().withPageTitle("umgangssprachlich").build(),
                                   new PlainText(", nur "),
                                   new InternalLink.Builder().withPageTitle("Plural").build()))],
                                      suffix: new RichText(":"))

        expect:
        getMeaningFromWiktionaryEntry("Staat", 2).text.components[0] == kTemplate
    }

    def "KTemplate with multiple parts"() {
        when:
        def kTemplate = getMeaningFromWiktionaryEntry("Bauch", 5).text.components[0] as KTemplate

        then:
        kTemplate.parts.size() == 3
        kTemplate.parts[0].text == new RichText("ugs.")
        kTemplate.parts[0].separator == new RichText("_")
        kTemplate.parts[1].text == new RichText("übertr.")
        kTemplate.parts[1].separator == new RichText("_")
        kTemplate.parts[2].text == new RichText("zu [5]")
        kTemplate.parts[2].separator == null
    }

    def "KTemplate with loose ordering of parameters"() {
        when:
        def kTemplate = getMeaningFromWiktionaryEntry("Verpflegung", 0).text.components[0] as KTemplate

        then:
        kTemplate.suffix == new RichText()
        kTemplate.parts.size() == 1
        def kontextPart = kTemplate.parts[0]
        kontextPart.text == new RichText("ohne Plural")
        kontextPart.separator == null
    }

    def "KTemplate with 'Kontext' as template name"() {
        when:
        def kTemplate = getMeaningFromWiktionaryEntry("Distribution", 0).text.components[0] as KTemplate

        then:
        kTemplate.parts.size() == 1
        kTemplate.parts[0].text == new RichText("Wirtschaft")
    }

    def "Abk-template"() {
        when:
        def entry = parseWiktionaryEntryPage("Steppke").wiktionaryEntries[0]

        then:
        entry.meanings.size() == 2
        entry.meanings[0].text.components[0] == new KTemplate(parts: [new KTemplate.Part(text: new RichText("ugs."))],
                                                              suffix: new RichText(","))
        entry.meanings[1].text.components[0] == new KTemplate(parts: [new KTemplate.Part(text: new RichText("ugs."))],
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

    def "QS Bedeutungen at the beginning of meaning-text"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Lichtwoche", 0)

        then:
        meaning.numbers == [ItemNumber.singleNumber("1")]
        meaning.text == new RichText("Entfernung, die das Licht in einer Woche im Vakuum zurücklegt")
    }

    def "QS Bedeutungen at the end of meaning-text"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Bekanntwerdung", 2)

        then:
        meaning.text.components.size() == 1
        meaning.text.components[0] == new PlainText("das Kennenlernen")
    }

    def "ref-elements should be ignored: element without attributes"() {
        when:
        def entry = parseWiktionaryEntryPage("Müller").wiktionaryEntries[1]
        def meaning = entry.meanings[0]

        then:
        PlainText mergedText = meaning.text.components[7] as PlainText
        mergedText.text.startsWith(
                " mit etwa 10,6% Anteil und über 600.000 Namenträgern häufigster Familienname in Deutschland")
    }

    def "ref-elements should be ignored: element with attribute"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Weltchronik", 0)

        then:
        PlainText lastComp = meaning.text.components.last() as PlainText
        lastComp.text.endsWith("historisch wird ")
    }

    private Meaning getMeaningFromWiktionaryEntry(String lemma, int meaningIdx) {
        def entry = parseWiktionaryEntryPage(lemma).wiktionaryEntries[0]
        return entry.meanings[meaningIdx]
    }
}