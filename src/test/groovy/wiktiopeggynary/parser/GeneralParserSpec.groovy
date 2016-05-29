package wiktiopeggynary.parser

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserSpec extends ParserSpecBase {

    def "create entity pro section"() {
        expect:
        parserService.parseWiktionaryEntryPage(readArticleFromResources("Boot"), templateService).wiktionaryEntries.size() == 3
    }

    def "parse only german sections"() {
        expect:
        parserService.parseWiktionaryEntryPage(readArticleFromResources("Kim"), templateService).wiktionaryEntries.size() == 2
    }
}
