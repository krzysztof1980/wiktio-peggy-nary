package wiktiopeggynary.parser.template.parser

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.parser.mouse.SourceString
import wiktiopeggynary.parser.template.model.AnonymousTemplateParameter
import wiktiopeggynary.parser.template.model.NamedTemplateParameterApplication
import wiktiopeggynary.parser.template.model.Template
import wiktiopeggynary.parser.template.model.functions.SwitchParserFunction
import wiktiopeggynary.parser.template.model.runtime.NumberedTemplateDefinitionParameter
import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter

import static wiktiopeggynary.parser.util.ResourceUtils.readTemplateDefinitionFromResources

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TemplateParserSpec extends Specification {

    def "template call with anonymous parameters"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString("{{Kontext | param1}}"))

        then: "template definition has one component"
        def components = parser.semantics().templateDefinition.components
        components.size() == 1

        and: "the component is a template call"
        components[0] instanceof Template
        Template template = components[0] as Template

        and: "it has 1 parameter"
        template.parameters.size() == 1

        and: "the parameter is anonymous and has value 'param1'"
        template.parameters[0] instanceof AnonymousTemplateParameter
        template.parameters[0].value.asText() == "param1"
    }

    def "template call with null parameter"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString("{{Kontext | | param2}}"))

        then: "template definition has one component"
        def components = parser.semantics().templateDefinition.components
        components.size() == 1

        and: "the component is a template call"
        components[0] instanceof Template
        Template template = components[0] as Template

        and: "it has 2 parameters"
        template.parameters.size() == 2

        and: "the 1st parameter is an empty text"
        template.parameters[0] instanceof AnonymousTemplateParameter
        template.parameters[0].value.asText() == ""
    }

    def "switch function: '#text' -> '#result'"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(text))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.asText() == result

        where:
        text                                                              || result
        "{{#switch: baz | foo = Foo | baz = Baz      | Bar }}"            || "Baz"
        "{{#switch: foo | foo = Foo | baz = Baz      | Bar }}"            || "Foo"
        "{{#switch: zzz | foo = Foo | baz = Baz      | Bar }}"            || "Bar"
        "{{#switch: zzz | foo = Foo | baz = Baz      | #default = Bar }}" || "Bar"
        "{{#switch: foo | foo       | baz = FooOrBaz | Bar }}"            || "FooOrBaz"
        "{{#switch: foo | foo = }}"                                       || ""
    }

    def "switch function: template as test"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString("{{#switch: foo | {{A_TEMPLATE}}=some value }}"))

        then: "template definition has one component"
        parser.semantics().templateDefinition.components.size() == 1

        and: "the component is a switch function"
        parser.semantics().templateDefinition.components[0] instanceof SwitchParserFunction

        and: "it has one case"
        SwitchParserFunction switchFunction = parser.semantics().templateDefinition.components[0] as SwitchParserFunction
        switchFunction.testCases.size() == 1
        SwitchParserFunction.SwitchTestCase testCase = switchFunction.testCases[0] as SwitchParserFunction.SwitchTestCase

        and: "the case has template call as test"
        testCase.tests.size() == 1
        testCase.tests[0] instanceof Template
        Template template = testCase.tests[0]
        template.name == "A_TEMPLATE"

        and: "the case has a text as result"
        testCase.result.asText() == "some value"
    }

    def "named parameter"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("Lit-Czihak_Biologie")))

        then: "template definition is not null"
        parser.semantics().templateDefinition != null

        and: "it has one component"
        def components = parser.semantics().templateDefinition.components
        components.size() == 1

        and: "the component is a switch function"
        components[0] instanceof SwitchParserFunction
        SwitchParserFunction switchFunction = components[0] as SwitchParserFunction

        and: "the comparison string is an application of named parameter"
        switchFunction.comparisonString instanceof NamedTemplateParameterApplication
        NamedTemplateParameterApplication param = switchFunction.comparisonString as NamedTemplateParameterApplication
        param.identifier == "A"
    }

    def "if(eq) (#notes): evaluate(lat.(#parameters)) == '#definitionText'"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("lat_dot")))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.asText(parameters) == definitionText

        where:
        notes                      | parameters                                      || definitionText
        "no parameter"             | []             as TemplateDefinitionParameter[] || "[[lateinisch|lateinisch]]"
        "1st. ifeq true"           | [numP(1, ":")] as TemplateDefinitionParameter[] || "[[lateinisch|lateinisch<nowiki>:</nowiki>]]"
        "2nd. ifeq true"           | [numP(1, ";")] as TemplateDefinitionParameter[] || "[[lateinisch|lateinisch<nowiki>;</nowiki>]]"
        "parameter not recognized" | [numP(1, "=")] as TemplateDefinitionParameter[] || "[[lateinisch|lateinisch=]]"
    }

    def "switch (#notes): evaluate(Kontext/Abk(#parameters)) == '#definitionText'"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("Kontext_Abk")))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.asText(parameters) == definitionText

        where:
        notes           | parameters                                            || definitionText
        "no group"      | [numP(1, "allg.")]   as TemplateDefinitionParameter[] || "allgemein"
        "group"         | [numP(1, "Genitiv")] as TemplateDefinitionParameter[] || "mit Genitiv"
        "default"       | [numP(1, "echo")]    as TemplateDefinitionParameter[] || "echo"
        "2nd parameter" | [numP(2, "mA")]      as TemplateDefinitionParameter[] || "Akkusativobjekt"
    }

    def "Kontext (#notes): evaluate(Kontext(#parameters)) == '#definitionText'"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("Kontext2")))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.asText(parameters) == definitionText

        where:
        notes           | parameters                                               || definitionText
        "one param"     | [numP(1, "Geometrie")]  as TemplateDefinitionParameter[] || "<i>Geometrie:</i>"
    }

    private NumberedTemplateDefinitionParameter numP(Integer number, String value) {
        return new NumberedTemplateDefinitionParameter(number, value)
    }
}
