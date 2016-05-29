package wiktiopeggynary.parser

import spock.lang.Specification
import wiktiopeggynary.model.markup.RichText
import wiktiopeggynary.parser.template.TemplateService
import wiktiopeggynary.parser.template.model.TemplateDefinition


/**
 * @author Krzysztof Witukiewicz
 */
class ParserSpecBase extends Specification {

    TemplateService templateService
    ParserService parserService

    def setup() {
        templateService = Mock(TemplateService) {
            parseTemplateDefinitionPageForTemplate(_) >> { String templateName ->
                new TemplateDefinition(new RichText("{{" + templateName + "}}"))
            }
        }
        parserService = new ParserService(new SequentialParserTaskExecutorFactory())
    }
}