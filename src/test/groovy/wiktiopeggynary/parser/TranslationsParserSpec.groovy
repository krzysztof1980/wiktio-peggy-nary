package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.substantiv.Gender
import wiktiopeggynary.model.substantiv.MultiGender

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TranslationsParserSpec extends ParserSpecBase {

    def "translation meaning number is not interpreted"() {
        when:
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Staat"), templateService).wiktionaryEntries[0];

        then: "the meaning number is an integer"
        entry.translations["ar"][0].meaningNumber == "1"

        and: "the meaning number is a list of range and integer"
        entry.translations["fr"][0].meaningNumber == "1–2, 4"
    }

    def "entry['#lang'] has #meaningCount meanings#notes"() {
        when:
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Staat"), templateService).wiktionaryEntries[0];

        then:
        entry.translations[lang].size() == meaningCount

        where:
        notes                       | lang || meaningCount
        ""                          | "ar" || 1
        " (separated by semicolon)" | "ku" || 2
        " (separated by space)"     | "pl" || 5
    }

    def "Translations with details in cursive: meaning '#meaningTxt'#notes has #translationsCount translations"() {
        when:
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Staat"), templateService).wiktionaryEntries[0];
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
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Staat"), templateService).wiktionaryEntries[0];
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
        when: "we take translations for Spanish"
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Staat"), templateService).wiktionaryEntries[0];
        def langTranslations = entry.translations["pl"]

        then: "the first translation in second meaning is Femininum"
        langTranslations[meaningIdx].translations[translationIdx].gender == gender

        where:
        meaningIdx | translationIdx | translationTxt                                     || gender
        0          | 0              | "[1, 2] {{Ü|pl|państwo}} {{n}}"                    || new MultiGender(Gender.NEUTRUM)
        1          | 0              | "[3] {{Ü|pl|Stany}} ''Pl.,''"                      || null
        1          | 1              | "{{Ü|pl|Stany Zjednoczone}} ''Pl.''"               || null
        2          | 0              | "[4] {{Ü|pl|kanton}} {{m}};"                       || new MultiGender(Gender.MASKULINUM)
        3          | 0              | "[5] {{Ü|pl|społeczeństwo}} {{n}},"                || new MultiGender(Gender.NEUTRUM)
        3          | 1              | "''Bienen, Wespen, Hummeln:'' {{Ü|pl|rój}} {{m}};" || new MultiGender(Gender.MASKULINUM)
        4          | 0              | "[7] {{Ü|pl|dwór}} {{m}}"                          || new MultiGender(Gender.MASKULINUM)
    }

    def "content of translation in entry['#lang'] meaning[#meaningIdx] translation[#translationIdx]"() {
        when:
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Staat"), templateService).wiktionaryEntries[0];
        def langTranslations = entry.translations[lang]
        def translation = langTranslations[meaningIdx].translations[translationIdx];

        then:
        translation.internalLink == internalLink
        translation.transcription == transcription
        translation.label == label
        translation.externalLink == externalLink

        where:
        lang | meaningIdx | translationIdx | translationTxt               || internalLink | transcription | label    | externalLink
        "eu" | 0          | 0              | "{{Ü|eu|estatu}}"            || "estatu"     | null          | null     | null
        "bg" | 0          | 0              | "{{Üt|bg|държава|dŭrzhava}}" || "държава"    | "dŭrzhava"    | null     | null
    }

    def "ignore empty translations and comments"() {
        when:
        def entry = parserService.parseWiktionaryEntryPage(
                readArticleFromResources("Heckmeck"), templateService).wiktionaryEntries[0];

        then: "there is only a Swedish translation, because other 2 are empty"
        entry.translations.size() == 1
        entry.translations.containsKey("sv")
    }
}
