package wiktiopeggynary.parser

import spock.lang.Specification

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserSpec extends Specification {

    def "create entity pro section"() {
        expect:
        new ParserService().parseWiktionaryEntryPage(
                readArticleFromResources("Boot")).size() == 3
    }

    def "parse only german sections"() {
        expect:
        new ParserService().parseWiktionaryEntryPage(
                readArticleFromResources("Kim")).size() == 2
    }
}
