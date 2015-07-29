package wiktiopeggynary.parser

import spock.lang.Specification

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserSpec extends Specification {

    def "create entity pro section"() {
        expect:
        ParserService.getInstance().parseWiktionaryPage(
                readArticleFromResources("Boot")).size() == 3
    }

    def "parse only german sections"() {
        expect:
        ParserService.getInstance().parseWiktionaryPage(
                readArticleFromResources("Kim")).size() == 2
    }

    def "parse translations"() {
        when:
        def entry = ParserService.getInstance().parseWiktionaryPage(
                readArticleFromResources("Ablehnung"))[0];

        then: "there are translations for 10 languages"
        entry.getTranslations().size() == 10

        and: "there are 3 meanings for Swedish"
        def svMeanings = entry.getTranslations().get("sv")
        svMeanings.size() == 3

        and: "the 3rd meaning has 2 translations"
        def meaning3 = svMeanings.get(2)
        meaning3.getMeaningNumber() == "3"
        meaning3.getTranslations().size() == 2
    }

    def "ignore empty translations and comments"() {
        when:
        def entry = ParserService.getInstance().parseWiktionaryPage(
                readArticleFromResources("Heckmeck"))[0];

        then: "there is only a Swedish translation, because other 2 are empty"
        entry.getTranslations().size() == 1
        entry.getTranslations().containsKey("sv")
    }
}
