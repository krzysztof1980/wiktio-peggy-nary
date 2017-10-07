package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.markup.*
import wiktiopeggynary.model.substantiv.Gender
import wiktiopeggynary.model.substantiv.MultiGender
import wiktiopeggynary.model.translation.Translation

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TranslationsParserSpec extends ParserSpecBase {

    def "translation meaning numbers"() {
        when:
        def entry = parseWiktionaryEntryPage("Bauch").wiktionaryEntries[0]

        then: "the meaning number is an integer"
        entry.translations["en"][0].meaningNumbers == [ItemNumber.singleNumber("1")]

        then: "the meaning number is a list of integers"
        entry.translations["en"][2].meaningNumbers == [ItemNumber.singleNumber("1"), ItemNumber.singleNumber("3")]

        then: "the meaning number is a list of integers and range"
        entry.translations["en"][1].meaningNumbers == [ItemNumber.singleNumber("1"),
                                                       ItemNumber.range("3", "4b"),
                                                       ItemNumber.singleNumber("5"),
                                                       ItemNumber.singleNumber("6")]
    }

    def "entry['#lang'] has #meaningCount meanings#notes"() {
        when:
        def entry = parseWiktionaryEntryPage("Staat").wiktionaryEntries[0]

        then:
        entry.translations[lang].size() == meaningCount

        where:
        notes                       | lang || meaningCount
        ""                          | "it" || 1
        " (separated by semicolon)" | "ru" || 4
        " (separated by space)"     | "pl" || 5
    }

    def "Translations with details in cursive: meaning '#meaningTxt'#notes has #translationsCount translations"() {
        when:
        def entry = parseWiktionaryEntryPage("Staat").wiktionaryEntries[0]
        def langTranslations = entry.translations["pl"]

        then:
        langTranslations.size() == 5
        def meaning = langTranslations[meaningIdx]

        and:
        meaning.translations.size() == translationsCount

        where:
        notes                | meaningIdx | meaningTxt                                                                           || translationsCount
        ""                   | 0          | "[1, 2] {{Ü|pl|państwo}} {{n}}"                                                      || 1
        " (space separated)" | 1          | "[3] {{Ü|pl|Stany}} ''Pl.,'' {{Ü|pl|Stany Zjednoczone}} ''Pl.''"                     || 2
        ""                   | 2          | "[4] {{Ü|pl|kanton}} {{m}};"                                                         || 1
        " (comma separated)" | 3          | "[5] {{Ü|pl|społeczeństwo}} {{n}}, ''Bienen, Wespen, Hummeln:'' {{Ü|pl|rój}} {{m}};" || 2
        ""                   | 4          | "[7] {{Ü|pl|dwór}} {{m}}"                                                            || 1
    }

    def "Translations with details as links: meaning '#meaningTxt'#notes has #translationsCount translations"() {
        when:
        def entry = parseWiktionaryEntryPage("Staat").wiktionaryEntries[0]
        def langTranslations = entry.translations["en"]

        then:
        langTranslations.size() == 6
        def meaning = langTranslations[meaningIdx]

        and:
        meaning.translations.size() == translationsCount

        where:
        notes                      | meaningIdx | meaningTxt                                               || translationsCount
        ""                         | 0          | "[1] {{Ü|en|state}};"                                    || 1
        " (comma separated)"       | 1          | "[2] [[national]] {{Ü|en|territory}}, {{Ü|en|country}};" || 2
        " (semicolon separated)"   | 2          | "[3] [[the]] {{Ü|en|States}}; [[the]] {{Ü|en|US}};"      || 2
        ""                         | 3          | "[4] {{Ü|en|canton}};"                                   || 1
        " (comma before template)" | 4          | "[5] [[insect]] [[society]], {{Ü|en|colony}};"           || 1
        ""                         | 5          | "[6] {{Ü|en|finery}}"                                    || 1
    }

    def "translation '#translationTxt' has gender: #gender"() {
        when:
        def entry = parseWiktionaryEntryPage("Staat").wiktionaryEntries[0]
        def langTranslations = entry.translations["pl"]

        then:
        langTranslations[meaningIdx].translations[translationIdx].gender == gender

        where:
        // @formatter:off
        meaningIdx | translationIdx | translationTxt                                     || gender
        0          | 0              | "[1, 2] {{Ü|pl|państwo}} {{n}}"                    || new MultiGender(Gender.NEUTRUM)
        1          | 0              | "[3] {{Ü|pl|Stany}} ''Pl.,''"                      || null
        1          | 1              | "{{Ü|pl|Stany Zjednoczone}} ''Pl.''"               || null
        2          | 0              | "[4] {{Ü|pl|kanton}} {{m}};"                       || new MultiGender(Gender.MASKULINUM)
        3          | 0              | "[5] {{Ü|pl|społeczeństwo}} {{n}},"                || new MultiGender(Gender.NEUTRUM)
        3          | 1              | "''Bienen, Wespen, Hummeln:'' {{Ü|pl|rój}} {{m}};" || new MultiGender(Gender.MASKULINUM)
        4          | 0              | "[7] {{Ü|pl|dwór}} {{m}}"                          || new MultiGender(Gender.MASKULINUM)
        // @formatter:on
    }

    def "content of translation in entry['#lang'] meaning[#meaningIdx] translation[#translationIdx]"() {
        when:
        def entry = parseWiktionaryEntryPage("Staat").wiktionaryEntries[0]
        def langTranslations = entry.translations[lang]
        def translation = langTranslations[meaningIdx].translations[translationIdx];

        then:
        translation.internalLink == internalLink
        translation.transcription == transcription
        translation.label == label

        where:
        lang | meaningIdx | translationIdx | translationTxt                      || internalLink  | transcription | label
        "pl" | 0          | 0              | "{{Ü|pl|państwo}}"                  || "państwo"     | null          | null
        "ru" | 0          | 0              | "{{Üt|ru|государство|gosudarstwo}}" || "государство" | "gosudarstwo" | null
    }

    def "ignore meanings with empty translation"() {
        when:
        def entry = parseWiktionaryEntryPage("Tiefgang").wiktionaryEntries[0]

        then:
        entry.translations.size() == 1
        entry.translations.containsKey("en")
    }

    def "meaning with non-empty text without translation-template"() {
        when:
        def entry = parseWiktionaryEntryPage("Spiel").wiktionaryEntries[0]

        then:
        def itMeanings = entry.translations["it"]
        itMeanings.size() == 3
        def meaning3 = itMeanings[2]
        meaning3.translations.isEmpty()
        meaning3.text == new RichText(new InternalLink.Builder().withPageTitle("partita").build(),
                                      new PlainText(" "),
                                      new MultiGender(Gender.FEMININUM))
    }

    def "translation inside cursive text"() {
        when:
        def entry = parseWiktionaryEntryPage("Erziehungsziel").wiktionaryEntries[0]

        then:
        def enMeanings = entry.translations["en"]
        enMeanings[0].translations.size() == 5
    }

    def "complex meaning text"() {
        when:
        def entry = parseWiktionaryEntryPage("Erziehungsziel").wiktionaryEntries[0]

        then:
        def enMeaning1 = entry.translations["en"][0]
        enMeaning1.text.components.size() == 7
        enMeaning1.text.components[0] == new Translation(internalLink: "child-rearing goal")
        enMeaning1.text.components[1] == new PlainText(", ")
        enMeaning1.text.components[2] == new Translation(internalLink: "parenting objective")
        enMeaning1.text.components[3] == new PlainText(", ")
        enMeaning1.text.components[4] == new Translation(internalLink: "parenting goal")
        enMeaning1.text.components[5] == new PlainText("; <br />")
        enMeaning1.text.components[6] ==
                new CursiveBlock(new RichText(new PlainText("Anmerkung: "),
                                              new Translation(internalLink: "educational objective"),
                                              new PlainText(" und "),
                                              new Translation(internalLink: "educational goal"),
                                              new PlainText(" bedeuten eher „"),
                                              new InternalLink.Builder().withPageTitle("Lernziel").build(),
                                              new PlainText("“ als „Erziehungsziel“")))
    }

    def "language variants"() {
        when:
        def entry = parseWiktionaryEntryPage("Billion").wiktionaryEntries[0]

        then:
        def enMeaning1 = entry.translations["en"][0]
        def langVariants = enMeaning1.text.components.findAll { c -> c instanceof LanguageVariant}
        langVariants.size() == 2

        and:
        def beVariant = langVariants[0] as LanguageVariant
        beVariant.variant == LanguageVariant.Variant.British
        beVariant.suffix == ":"

        and:
        def aeVariant = langVariants[1] as LanguageVariant
        aeVariant.variant == LanguageVariant.Variant.American
        aeVariant.suffix == ":"
    }
}
