package wiktiopeggynary.parser.template.model.functions

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.model.markup.Constant
import wiktiopeggynary.model.markup.RichText
import wiktiopeggynary.parser.mouse.SourceString
import wiktiopeggynary.parser.template.TemplateService
import wiktiopeggynary.parser.template.model.TemplateParameterApplication.Builder
import wiktiopeggynary.parser.template.parser.TemplateParser

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class IfParserFunctionSpec extends Specification {

    def "evaluation of template parameter"() {
        given:
        def templateService = Mock(TemplateService)

        expect:
        parameter.evaluate(templateService, context) == result

        where:
        // @formatter:off
        parameter                                                                           | context                                       || result
        new Builder().withIdentifier("1").build()                                           | []                                            | new RichText("{{{1}}}")
        new Builder().withIdentifier("1").withDefaultValue(new RichText("bla")).build()     | []                                            | new RichText("bla")
        new Builder().withIdentifier("1").build()                                           | [ new Constant("1", new RichText("hello")) ]  | new RichText("hello")
        new Builder().withIdentifier("1").withDefaultValue(new RichText("bla")).build()     | [ new Constant("1", new RichText("hello")) ]  | new RichText("hello")
        // @formatter:on
    }

    def "various comparisons"() {
        given:
        def parser = new TemplateParser()
        def templateService = Mock(TemplateService)

        when:
        parser.parse(new SourceString(source))

        then:
        def templateDefinition = parser.semantics().templateDefinition
        templateDefinition.body.components.size() == 1
        templateDefinition.body.components[0] instanceof IfParserFunction
        def function = templateDefinition.body.components[0] as IfParserFunction
        function.evaluate(templateService, context) == result

        where:
        source                               | context                                       || result
        "{{#if: {{{1}}}     | true| false}}" | []                                             | new RichText("true")
        "{{#if: {{{param}}} | true| false}}" | []                                             | new RichText("true")
        "{{#if: {{{1|}}}    | true| false}}" | []                                             | new RichText("false")
        "{{#if: {{{param|}}}| true| false}}" | []                                             | new RichText("false")

        "{{#if: {{{1}}}     | true| false}}" | [new Constant("1", RichText.empty())]          | new RichText("false")
        "{{#if: {{{param}}} | true| false}}" | [new Constant("param", RichText.empty())]      | new RichText("false")
        "{{#if: {{{1|}}}    | true| false}}" | [new Constant("1", RichText.empty())]          | new RichText("false")
        "{{#if: {{{param|}}}| true| false}}" | [new Constant("param", RichText.empty())]      | new RichText("false")

        "{{#if: {{{1}}}     | true| false}}" | [new Constant("1", new RichText("value"))]     | new RichText("true")
        "{{#if: {{{param}}} | true| false}}" | [new Constant("param", new RichText("value"))] | new RichText("true")
        "{{#if: {{{1|}}}    | true| false}}" | [new Constant("1", new RichText("value"))]     | new RichText("true")
        "{{#if: {{{param|}}}| true| false}}" | [new Constant("param", new RichText("value"))] | new RichText("true")
    }
}
