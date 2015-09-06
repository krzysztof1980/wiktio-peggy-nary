package wiktiopeggynary.parser.template.model

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.parser.template.TemplateService
import wiktiopeggynary.parser.template.model.runtime.NamedTemplateDefinitionParameter
import wiktiopeggynary.parser.template.model.runtime.NumberedTemplateDefinitionParameter
import wiktiopeggynary.util.ServiceLocator

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TemplateSpec extends Specification {

    def "asText and no parameters"() {

        given: "mocked service"
        TemplateService templateService = Mock() {
            TemplateDefinition templateDef = Mock() {
                1 * asText() >> "Hello, World!"
            }
            1 * findTemplateDefinition("test_template") >> templateDef
        }
        ServiceLocator.loadService(TemplateService.class, templateService)

        and: "the test template"
        Template template = new Template("test_template")

        expect:
        template.asText() == "Hello, World!"
    }

    def "asText and anonymous parameters"() {

        given: "mocked service"
        TemplateService templateService = Mock() {
            TemplateDefinition templateDef = Mock() {
                1 * asText(new NumberedTemplateDefinitionParameter(1, "param1Value"),
                           new NumberedTemplateDefinitionParameter(2, "param2Value")) >> "Hello, World!"
            }
            1 * findTemplateDefinition("test_template") >> templateDef
        }
        ServiceLocator.loadService(TemplateService.class, templateService)

        and: "the test template"
        Template template = new Template("test_template")
        template.addParameter(new AnonymousTemplateParameter(new PlainText("param1Value")))
        template.addParameter(new AnonymousTemplateParameter(new PlainText("param2Value")))

        expect:
        template.asText() == "Hello, World!"
    }

    def "asText and mixed parameters"() {
        given: "mocked service"
        TemplateService templateService = Mock() {
            TemplateDefinition templateDef = Mock() {
                1 * asText(new NumberedTemplateDefinitionParameter(2, "param2Value"),
                           new NumberedTemplateDefinitionParameter(3, "param3Value"),
                           new NumberedTemplateDefinitionParameter(1, "param1Value"),
                           new NamedTemplateDefinitionParameter("extra", "extraParamValue")) >> "Hello, World!"
            }
            1 * findTemplateDefinition("test_template") >> templateDef
        }
        ServiceLocator.loadService(TemplateService.class, templateService)

        and: "the test template"
        Template template = new Template("test_template")
        template.addParameter(new NumberedTemplateParameter(2, new PlainText("param2Value")))
        template.addParameter(new AnonymousTemplateParameter(new PlainText("param3Value")))
        template.addParameter(new NumberedTemplateParameter(1, new PlainText("param1Value")))
        template.addParameter(new NamedTemplateParameter("extra", new PlainText("extraParamValue")))

        expect:
        template.asText() == "Hello, World!"
    }
}
