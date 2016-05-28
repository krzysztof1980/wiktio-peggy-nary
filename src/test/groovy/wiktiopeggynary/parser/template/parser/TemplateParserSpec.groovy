package wiktiopeggynary.parser.template.parser

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.model.markup.Constant
import wiktiopeggynary.model.markup.PlainText
import wiktiopeggynary.model.markup.RichText
import wiktiopeggynary.model.markup.Template
import wiktiopeggynary.parser.mouse.SourceString
import wiktiopeggynary.parser.template.TemplateService
import wiktiopeggynary.parser.template.model.TemplateDefinition
import wiktiopeggynary.parser.template.model.TemplateParameterApplication
import wiktiopeggynary.parser.template.model.functions.SwitchParserFunction

import static wiktiopeggynary.parser.util.ResourceUtils.readTemplateDefinitionFromResources

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class TemplateParserSpec extends Specification {

    def "template call"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(text))

        then: "template definition has one component"
        def components = parser.semantics().templateDefinition.body.components
        components.size() == 1

        and:
        components.get(0) == result

        where:
        // @formatter:off
        text                        || result
        "{{Kontext | param1}}"      || new Template.Builder().withName("Kontext")
                                                             .withAnonymousParameter(new RichText("param1"))
                                                             .build()
        "{{Kontext | | param2}}"    || new Template.Builder().withName("Kontext")
                                                             .withAnonymousParameter(RichText.empty())
                                                             .withAnonymousParameter(new RichText("param2"))
                                                             .build()
        "{{K|[[umgangssprachlich]], nur [[Plural]]}}" || new Template.Builder().withName("K")
                                                                               .withAnonymousParameter(
                                                                                    new RichText("[[umgangssprachlich]], nur [[Plural]]"))
                                                                               .build()
        // TODO: TemplateParser treats parameters as plain text, subject to change
//        "{{K|[[umgangssprachlich]], nur [[Plural]]}}" || new Template.Builder().withName("K")
//                                                                               .withAnonymousParameter(
//                                                                                    new RichText(
//                                                                                            new InternalLink.Builder().withPageTitle("umgangssprachlich").build(),
//                                                                                            new PlainText(", nur "),
//                                                                                            new InternalLink.Builder().withPageTitle("Plural").build()))
//                                                                               .build()
        // @formatter:on
    }

    def "switch function: '#text' -> '#result'"() {
        given:
        TemplateParser parser = new TemplateParser()
        TemplateService templateService = Mock()

        when:
        parser.parse(new SourceString(text))

        then:
        parser.semantics().templateDefinition != null

        expect:
        parser.semantics().templateDefinition.evaluate(templateService, []) == result

        where:
        text                                                              || result
        "{{#switch: baz | foo = Foo | baz = Baz      | Bar }}"            || new RichText("Baz")
        "{{#switch: foo | foo = Foo | baz = Baz      | Bar }}"            || new RichText("Foo")
        "{{#switch: zzz | foo = Foo | baz = Baz      | Bar }}"            || new RichText("Bar")
        "{{#switch: zzz | foo = Foo | baz = Baz      | #default = Bar }}" || new RichText("Bar")
        "{{#switch: foo | foo       | baz = FooOrBaz | Bar }}"            || new RichText("FooOrBaz")
        "{{#switch: foo | foo = }}"                                       || new RichText()
    }

    def "switch function: template as test"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString("{{#switch: foo | {{A_TEMPLATE}}=some value }}"))

        then: "template definition has one component"
        def body = parser.semantics().templateDefinition.body
        body.components.size() == 1

        and: "the component is a switch function"
        body.components[0] instanceof SwitchParserFunction

        and: "it has one case"
        SwitchParserFunction switchFunction = body.components[0] as SwitchParserFunction
        switchFunction.testCases.size() == 1
        def testCase = switchFunction.testCases[0]

        and: "the case has template call as test"
        testCase.tests.size() == 1
        testCase.tests[0].components.size() == 1
        testCase.tests[0].components[0] instanceof Template
        Template template = testCase.tests[0].components[0] as Template
        template.name == "A_TEMPLATE"

        and: "the case has a text as result"
        testCase.result.components.size() == 1
        testCase.result.components[0] instanceof PlainText
        PlainText text = testCase.result.components[0] as PlainText
        text.text == "some value"
    }

    def "named parameter"() {
        given:
        def parser = new TemplateParser()

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("Lit-Czihak_Biologie")))

        then: "template definition is not null"
        parser.semantics().templateDefinition != null

        and: "it has one component"
        def body = parser.semantics().templateDefinition.body
        body.components.size() == 1

        and: "the component is a switch function"
        body.components[0] instanceof SwitchParserFunction
        SwitchParserFunction switchFunction = body.components[0] as SwitchParserFunction

        and: "the comparison string is an application of named parameter"
        switchFunction.comparisonString.components.size() == 1
        switchFunction.comparisonString.components[0] instanceof TemplateParameterApplication
        TemplateParameterApplication param = switchFunction.comparisonString.components
                [0] as TemplateParameterApplication
        param.identifier == "A"
    }

    def "if(eq) (#notes): evaluate(lat.(#parameters)) == '#evaluationResult'"() {
        given:
        def parser = new TemplateParser()
        def templateService = Mock(TemplateService)

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("lat_dot")))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.evaluate(templateService, parameters) == evaluationResult

        where:
        // @formatter:off
        notes                      | parameters        || evaluationResult
        // TODO: TemplateParser does not recognize links yet (replace it with RichTextParser)
//        "no parameter"             | []                || new RichText(new InternalLink.Builder().withPageTitle("lateinisch")
//                                                                                                 .withLinkText("lateinisch")
//                                                                                                 .build())
//        "1st. ifeq true"           | [param("1", ":")] || new RichText(new InternalLink.Builder().withPageTitle("lateinisch")
//                                                                                                 .withLinkText("lateinisch<nowiki>:</nowiki>")
//                                                                                                 .build())
//        "2nd. ifeq true"           | [param("1", ";")] || new RichText(new InternalLink.Builder().withPageTitle("lateinisch")
//                                                                                                 .withLinkText("lateinisch<nowiki>;</nowiki>")
//                                                                                                 .build())
//        "parameter not recognized" | [param("1", "=")] || new RichText(new InternalLink.Builder().withPageTitle("lateinisch")
//                                                                                                 .withLinkText("lateinisch=")
//                                                                                                 .build())
        "no parameter"             | []                || new RichText("[[lateinisch|lateinisch]]")
        "1st. ifeq true"           | [param("1", ":")] || new RichText("[[lateinisch|lateinisch<nowiki>:</nowiki>]]")
        "2nd. ifeq true"           | [param("1", ";")] || new RichText("[[lateinisch|lateinisch<nowiki>;</nowiki>]]")
        "parameter not recognized" | [param("1", "=")] || new RichText("[[lateinisch|lateinisch=]]")
        // @formatter:on
    }

    def "switch (#notes): evaluate(Kontext/Abk(#parameters)) == '#evaluationResult'"() {
        given:
        def parser = new TemplateParser()
        def templateService = Mock(TemplateService)

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("Kontext_Abk")))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.evaluate(templateService, parameters) == evaluationResult

        where:
        notes           | parameters              || evaluationResult
        "no group"      | [param("1", "allg.")]   || new RichText("allgemein")
        "group"         | [param("1", "Genitiv")] || new RichText("mit Genitiv")
        "default"       | [param("1", "echo")]    || new RichText("echo")
        "2nd parameter" | [param("2", "mA")]      || new RichText("Akkusativobjekt")
    }

    def "Kontext (#notes): evaluate(Kontext(#parameters)) == '#evaluationResult'"() {
        given:
        def parser = new TemplateParser()
        def templateService = Mock(TemplateService) {
            parseTemplateDefinitionPageForTemplate("PAGENAME") >> new TemplateDefinition(new RichText("page title"))
        }

        when:
        parser.parse(new SourceString(readTemplateDefinitionFromResources("Kontext2")))

        then:
        parser.semantics().templateDefinition != null
        parser.semantics().templateDefinition.evaluate(templateService, parameters) == evaluationResult

        where:
        notes       | parameters                 || evaluationResult
        "one param" | [param("1", "page title")] || new RichText("<i>")
    }

    private static Constant param(String name, String value) {
        return new Constant(name, new RichText(value))
    }
}
