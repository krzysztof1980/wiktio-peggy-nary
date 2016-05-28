package wiktiopeggynary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.persistence.ElasticsearchNativeClient;

import java.nio.file.Path;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryDumpParseManager {
	private static final Logger logger = LoggerFactory.getLogger(WiktionaryDumpParseManager.class);

	private final ElasticsearchNativeClient esClient;
	private final ParserService parserService;

	public WiktionaryDumpParseManager(ParserService parserService, ElasticsearchNativeClient esClient) {
		this.parserService = parserService;
		this.esClient = esClient;
	}

	public void parse(Path wiktionaryDumpPath) {
		logger.info("Deleting indices...");
		esClient.deleteWiktionaryEntryIndexIfExists();
		esClient.deleteTemplateDefinitionIndexIfExists();

		logger.info("Reading in template definitions pages...");
		TemplateService templateService = new TemplateService(parserService.getTemplateDefinitionPagesFromDump(
				wiktionaryDumpPath), parserService);

		logger.info("Indexing wiktionary entries...");
		parserService.getWiktionaryEntriesFromDump(wiktionaryDumpPath, templateService,
		                                           r -> r.getWiktionaryEntries().forEach(esClient::indexWiktionaryEntry)
		);

		logger.info("Indexing template definitions...");
		templateService.processTemplateDefinitions(esClient::indexTemplateDefinition);
	}
}