package wiktiopeggynary.parser.template.model.runtime

import spock.lang.Specification
import wiktiopeggynary.model.markup.*
import wiktiopeggynary.parser.template.model.TemplateDefinitionParameterListBuilder

/**
 * @author Krzysztof Witukiewicz
 */
class TemplateDefinitionParametersBuilderSpec extends Specification {

    def "anonymous parameters"() {
        given:
        TemplateDefinitionParameterListBuilder builder = new TemplateDefinitionParameterListBuilder()

        when:
        builder.visit(new AnonymousTemplateParameter(new RichText("param1Value")))
        builder.visit(new AnonymousTemplateParameter(new RichText("param2Value")))
        def parameters = builder.build()

        then:
        parameters.size() == 2
        parameters.contains(new Constant("1", new RichText("param1Value")))
        parameters.contains(new Constant("2", new RichText("param2Value")))
    }

    def "mixed parameters"() {
        given:
        TemplateDefinitionParameterListBuilder builder = new TemplateDefinitionParameterListBuilder()

        when:
        builder.visit(new NumberedTemplateParameter(2, new RichText("param2Value")))
        builder.visit(new AnonymousTemplateParameter(new RichText("param3Value")))
        builder.visit(new NumberedTemplateParameter(1, new RichText("param1Value")))
        builder.visit(new NamedTemplateParameter("extra", new RichText("extraParamValue")))
        def parameters = builder.build()

        then:
        parameters.size() == 4
        parameters.contains(new Constant("1", new RichText("param1Value")))
        parameters.contains(new Constant("2", new RichText("param2Value")))
        parameters.contains(new Constant("3", new RichText("param3Value")))
        parameters.contains(new Constant("extra", new RichText("extraParamValue")))
    }
}
