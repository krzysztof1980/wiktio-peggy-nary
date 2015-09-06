package wiktiopeggynary

import spock.lang.Specification
import spock.lang.Unroll
import wiktiopeggynary.model.WiktionaryEntry
import wiktiopeggynary.model.markup.Template
import wiktiopeggynary.parser.ParserService
import wiktiopeggynary.parser.TemplateDefinitionPageParseResult
import wiktiopeggynary.parser.WiktionaryEntryPageParseResult
import wiktiopeggynary.parser.template.model.TemplateDefinition
import wiktiopeggynary.persistence.ElasticsearchClient
import wiktiopeggynary.util.ServiceLocator

import java.nio.file.Path
import java.util.function.Consumer

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class WiktionaryDumpParseManagerSpec extends Specification {

    final Path DUMP_PATH = Mock()

    ElasticsearchClient esClient = Mock() {
        1 * deleteWiktionaryEntryIndexIfExists()
        1 * deleteTemplateDefinitionIndexIfExists()
    }

    ParserService parserService = Mock()

    def setup() {
        ServiceLocator.loadService(ParserService.class, parserService)
    }

    def "parse no entries"() {
        given: "ParserService that returns neither wiktionary entries nor templates"
        with(parserService) {
            1 * getTemplateDefinitionPagesFromDump(DUMP_PATH) >> [:]
            1 * getWiktionaryEntriesFromDump(DUMP_PATH, _)
        }

        and: "object under test"
        WiktionaryDumpParseManager wiktionaryDumpParseManager = new WiktionaryDumpParseManager(esClient)

        when:
        wiktionaryDumpParseManager.parse(DUMP_PATH)

        then:
        0 * parserService.parseTemplateDefinitionPage(_)
        with(esClient) {
            0 * indexWiktionaryEntry(_)
            0 * indexTemplateDefinition(_)
        }
    }

    def "parse an entry that uses no templates"() {
        given: "ParserService that returns a wiktionary entry"
        def wiktionaryEntry = new WiktionaryEntry()
        def pageParseResult = new WiktionaryEntryPageParseResult([wiktionaryEntry], [])
        with(parserService) {
            1 * getTemplateDefinitionPagesFromDump(DUMP_PATH) >> [:]
            1 * getWiktionaryEntriesFromDump(DUMP_PATH, _) >>
                    { _, Consumer<WiktionaryEntryPageParseResult> consumer -> consumer.accept(pageParseResult) }
        }

        and: "object under test"
        WiktionaryDumpParseManager wiktionaryDumpParseManager = new WiktionaryDumpParseManager(esClient)

        when:
        wiktionaryDumpParseManager.parse(DUMP_PATH)

        then:
        0 * parserService.parseTemplateDefinitionPage(_)
        with(esClient) {
            1 * indexWiktionaryEntry(wiktionaryEntry)
            0 * indexTemplateDefinition(_)
        }
    }

    def "parse an entry that uses templates"() {
        final TEMPLATE_NAME = "templateX"
        final TEMPLATE_DEFINITION = "text of template definition"

        given: "ParserService that returns a wiktionary entry and a template"
        def wiktionaryEntry = Mock(WiktionaryEntry)
        def template = Mock(Template) {
            getName() >> TEMPLATE_NAME
        }
        def pageParseResult = new WiktionaryEntryPageParseResult([wiktionaryEntry], [template])
        with(parserService) {
            1 * getTemplateDefinitionPagesFromDump(DUMP_PATH) >> [(TEMPLATE_NAME): TEMPLATE_DEFINITION]
            1 * getWiktionaryEntriesFromDump(DUMP_PATH, _) >>
                    { _, Consumer<WiktionaryEntryPageParseResult> consumer -> consumer.accept(pageParseResult) }
        }

        and: "object under test"
        WiktionaryDumpParseManager wiktionaryDumpParseManager = new WiktionaryDumpParseManager(esClient)

        when:
        wiktionaryDumpParseManager.parse(DUMP_PATH)

        then:
        interaction {
            def templateDefinition = Mock(TemplateDefinition)
            with(parserService) {
                def templatePageParseResult = new TemplateDefinitionPageParseResult(templateDefinition, [])
                1 * parseTemplateDefinitionPage(TEMPLATE_DEFINITION) >> templatePageParseResult
            }
            with(esClient) {
                1 * indexWiktionaryEntry(wiktionaryEntry)
                1 * indexTemplateDefinition(templateDefinition)
            }
        }
    }

    def "template containing another template"() {
        final OUTER_TEMPLATE_NAME = "outerTemplate"
        final OUTER_TEMPLATE_DEFINITION = "text of outer template definition"
        final INNER_TEMPLATE_NAME = "innerTemplate"
        final INNER_TEMPLATE_DEFINITION = "text of inner template definition"

        given: "ParserService that returns a wiktionary entry and templates"
        def wiktionaryEntry = Mock(WiktionaryEntry)
        def outerTemplate = Mock(Template) {
            getName() >> OUTER_TEMPLATE_NAME
        }
        def pageParseResult = new WiktionaryEntryPageParseResult([wiktionaryEntry], [outerTemplate])
        with(parserService) {
            1 * getTemplateDefinitionPagesFromDump(DUMP_PATH) >>
                    [(OUTER_TEMPLATE_NAME): OUTER_TEMPLATE_DEFINITION, (INNER_TEMPLATE_NAME): INNER_TEMPLATE_DEFINITION]
            1 * getWiktionaryEntriesFromDump(DUMP_PATH, _) >>
                    { _, Consumer<WiktionaryEntryPageParseResult> consumer -> consumer.accept(pageParseResult) }
        }

        and: "object under test"
        WiktionaryDumpParseManager wiktionaryDumpParseManager = new WiktionaryDumpParseManager(esClient)

        when:
        wiktionaryDumpParseManager.parse(DUMP_PATH)

        then:
        interaction {
            def outerTemplateDefinition = Mock(TemplateDefinition)
            def innerTemplateDefinition = Mock(TemplateDefinition)
            with(parserService) {
                def innerTemplate = Mock(wiktiopeggynary.parser.template.model.Template) {
                    getName() >> INNER_TEMPLATE_NAME
                }
                1 * parseTemplateDefinitionPage(OUTER_TEMPLATE_DEFINITION) >>
                        new TemplateDefinitionPageParseResult(outerTemplateDefinition, [innerTemplate])
                1 * parseTemplateDefinitionPage(INNER_TEMPLATE_DEFINITION) >>
                        new TemplateDefinitionPageParseResult(innerTemplateDefinition, [])
            }
            with(esClient) {
                1 * indexWiktionaryEntry(wiktionaryEntry)
                1 * indexTemplateDefinition(outerTemplateDefinition)
                1 * indexTemplateDefinition(innerTemplateDefinition)
            }
        }
    }
}
