package wiktiopeggynary.parser

import spock.lang.Specification
import wiktiopeggynary.parser.template.TemplateService

/**
 * @author Krzysztof Witukiewicz
 */
class ParserSpecBase extends Specification {

    TemplateService templateService
    ParserService parserService

    def setup() {
        // TODO: remove together with TemplateService?
//        templateService = Mock(TemplateService) {
//            parseTemplateDefinitionPageForTemplate(_) >> { String templateName ->
//                new TemplateDefinition(new RichText("{{" + templateName + "}}"))
//            }
//        }
        parserService = new ParserService(new SequentialParserTaskExecutorFactory())
        templateService = new TemplateService(Collections.emptyMap(), parserService)
    }
}