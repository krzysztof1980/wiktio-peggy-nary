package wiktiopeggynary.parser.template.model

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.parser.template.model.runtime.NumberedTemplateDefinitionParameter

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TemplateParameterApplicationSpec extends Specification {

    def "parameter is available"() {
        given:
        def param = new NumberedTemplateDefinitionParameter(1, "foo")
        def paramApp = new NumberedTemplateParameterApplication(1)

        expect:
        paramApp.asText(param) == "foo"
    }

    def "numbered parameter is not available"() {
        given:
        def paramApp = new NumberedTemplateParameterApplication(1)

        expect:
        paramApp.asText() == "{{{1}}}"
    }

    def "named parameter is not available"() {
        given:
        def paramApp = new NamedTemplateParameterApplication("foo")

        expect:
        paramApp.asText() == "{{{foo}}}"
    }

    def "parameter not available but default value is defined"() {
        given:
        def paramApp = new NamedTemplateParameterApplication("foo")
        paramApp.setDefaultValue("bar")

        expect:
        paramApp.asText() == "bar"
    }
}
