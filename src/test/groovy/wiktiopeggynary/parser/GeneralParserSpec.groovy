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
}
