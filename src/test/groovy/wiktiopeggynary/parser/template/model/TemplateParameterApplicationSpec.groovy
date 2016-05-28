package wiktiopeggynary.parser.template.model

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.model.markup.Constant
import wiktiopeggynary.model.markup.RichText
import wiktiopeggynary.parser.template.TemplateService

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TemplateParameterApplicationSpec extends Specification {

    def "parameter is available"() {
        given:
        def paramApp = new TemplateParameterApplication.Builder().withIdentifier("1").build()

        expect:
        paramApp.evaluate(Mock(TemplateService), [new Constant("1", new RichText("foo"))]) == new RichText("foo")
    }

    def "numbered parameter is not available"() {
        given:
        def paramApp = new TemplateParameterApplication.Builder().withIdentifier("1").build()

        expect:
        paramApp.evaluate(Mock(TemplateService), []) == new RichText("{{{1}}}")
    }

    def "named parameter is not available"() {
        given:
        def paramApp = new TemplateParameterApplication.Builder().withIdentifier("foo").build()

        expect:
        paramApp.evaluate(Mock(TemplateService), []) == new RichText("{{{foo}}}")
    }

    def "parameter not available but default value is defined"() {
        given:
        def paramApp = new TemplateParameterApplication.Builder().withIdentifier("foo").
                withDefaultValue(new RichText("bar")).build()

        expect:
        paramApp.evaluate(Mock(TemplateService), []) == new RichText("bar")
    }
}
