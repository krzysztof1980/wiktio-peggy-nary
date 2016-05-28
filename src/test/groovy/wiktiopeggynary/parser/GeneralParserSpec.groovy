package wiktiopeggynary.parser

import spock.lang.Specification
import wiktiopeggynary.parser.template.TemplateService

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class GeneralParserSpec extends Specification {

    def "create entity pro section"() {
        when:
        def parseResult = new ParserService(new SequentialParserTaskExecutorFactory()).parseWiktionaryEntryPage(
                readArticleFromResources("Boot"), Mock(TemplateService))

        then:
        parseResult.wiktionaryEntries.size() == 3
    }

    def "parse only german sections"() {
        expect:
        new ParserService(parserTaskExecutorFactory).parseWiktionaryEntryPage(
                readArticleFromResources("Kim")).size() == 2
    }
}
