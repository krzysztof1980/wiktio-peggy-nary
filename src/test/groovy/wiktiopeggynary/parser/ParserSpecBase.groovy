package wiktiopeggynary.parser

import spock.lang.Specification

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class ParserSpecBase extends Specification {
    ParserService parserService

    def setup() {
        parserService = new ParserService(new SequentialParserTaskExecutorFactory())
    }

    WiktionaryEntryPageParseResult parseWiktionaryEntryPage(String lemma) {
        def optResult = parserService.parseWiktionaryEntryPage(readArticleFromResources(lemma))
        assert optResult.isPresent()
        return optResult.get()
    }
}