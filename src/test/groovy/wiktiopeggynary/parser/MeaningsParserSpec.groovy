package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.markup.*
import wiktiopeggynary.model.meaning.Meaning
import wiktiopeggynary.model.meaning.MeaningKontext

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

    def "plain text-Kontext"() {
        given:
        def kontext = new MeaningKontext(parts: [new MeaningKontext.Part(text: new RichText("Politik"))],
                                         suffix: new RichText(":"))

        expect:
        getMeaningFromWiktionaryEntry("Staat", 1).kontext == kontext
    }

    def "rich text-Kontext"() {
        given:
        def kontext = new MeaningKontext(parts: [new MeaningKontext.Part(
                text: new RichText(new InternalLink.Builder().withPageTitle("umgangssprachlich").build(),
                                   new PlainText(", nur "),
                                   new InternalLink.Builder().withPageTitle("Plural").build()))],
                                         suffix: new RichText(":"))

        expect:
        getMeaningFromWiktionaryEntry("Staat", 2).kontext == kontext
    }

    def "Kontext with multiple parts"() {
        when:
        def kontext = getMeaningFromWiktionaryEntry("Bauch", 5).kontext

        then:
        kontext.parts.size() == 3
        kontext.parts[0].text == new RichText("ugs.")
        kontext.parts[0].separator == new RichText("_")
        kontext.parts[1].text == new RichText("übertr.")
        kontext.parts[1].separator == new RichText("_")
        kontext.parts[2].text == new RichText("zu [5]")
        kontext.parts[2].separator == null
    }

    def "Kontext with loose ordering of parameters"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Verpflegung", 0)

        then:
        meaning.kontext != null
        meaning.kontext.suffix == new RichText()
        meaning.kontext.parts.size() == 1
        def kontextPart = meaning.kontext.parts[0]
        kontextPart.text == new RichText("ohne Plural")
        kontextPart.separator == null
    }

    def "Kontext with 'Kontext' as template name"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Distribution", 0)

        then:
        meaning.kontext != null
        meaning.kontext.parts.size() == 1
        meaning.kontext.parts[0].text == new RichText("Wirtschaft")
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

    def "QS Bedeutungen at the beginning of meaning-text"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Lichtwoche", 0)

        then:
        meaning.numbers == [ItemNumber.singleNumber("1")]
        meaning.kontext == null
        meaning.text == new RichText("Entfernung, die das Licht in einer Woche im Vakuum zurücklegt")
    }

    def "QS Bedeutungen at the end of meaning-text"() {
        when:
        def meaning = getMeaningFromWiktionaryEntry("Bekanntwerdung", 2)

        then:
        meaning.text.components.size() == 1
        meaning.text.components[0] == new PlainText("das Kennenlernen")
    }

    def "ref-elements should be ignored"() {
        when:
        def entry = parseWiktionaryEntryPage("Müller").wiktionaryEntries[1]
        def meaning = entry.meanings[0]

        then:
        PlainText mergedText = meaning.text.components[7] as PlainText
        mergedText.text.startsWith(
                " mit etwa 10,6% Anteil und über 600.000 Namenträgern häufigster Familienname in Deutschland")
    }

    private Meaning getMeaningFromWiktionaryEntry(String lemma, int meaningIdx) {
        def entry = parseWiktionaryEntryPage(lemma).wiktionaryEntries[0]
        return entry.getMeanings().get(meaningIdx)
    }
}