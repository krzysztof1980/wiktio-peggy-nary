package wiktiopeggynary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.ParseException;
import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.TemplateDefinitionPageParseResult;
import wiktiopeggynary.parser.template.model.TemplateDefinition;
import wiktiopeggynary.persistence.ElasticsearchClient;
import wiktiopeggynary.util.ServiceLocator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryDumpParseManager {
	private static final Logger logger = LoggerFactory.getLogger(WiktionaryDumpParseManager.class);

	private final ElasticsearchClient esClient;
	private final ParserService parserService;

	public WiktionaryDumpParseManager(ElasticsearchClient esClient) {
		if (esClient == null)
			throw new IllegalArgumentException("esClient must not be null");
		this.esClient = esClient;
		parserService = ServiceLocator.getService(ParserService.class);
	}

	public void parse(Path wiktionaryDumpPath) {
		logger.debug("Deleting indices...");
		esClient.deleteWiktionaryEntryIndexIfExists();
		esClient.deleteTemplateDefinitionIndexIfExists();

		logger.debug("Reading in template definitions pages...");
		Map<String, String> templateDefinitionsPages =
				parserService.getTemplateDefinitionPagesFromDump(wiktionaryDumpPath);

		logger.debug("Indexing wiktionary entries...");
		Map<String, TemplateDefinition> templateDefinitions = new HashMap<>();
		parserService.getWiktionaryEntriesFromDump(wiktionaryDumpPath, r -> {
			r.getTemplates().forEach(template -> {
				// if templateDefinitionsPages does not contain the name, than it means, that it could not be parsed
				// and was removed
				if (!templateDefinitions.containsKey(template.getName()) && templateDefinitionsPages.containsKey(template.getName())) {
					parseTemplate(template.getName(), templateDefinitionsPages, templateDefinitions);
				}
				TemplateDefinition templateDefinition = templateDefinitions.get(template.getName());
				if (templateDefinition != null) {
					List<String> parameters = template.getAttributes().stream().map(a -> a.asText()).collect(Collectors.toList());
					//templateDefinition.asText()
				}
			});
			// TODO: do template processing
			r.getWiktionaryEntries().forEach(esClient::indexWiktionaryEntry);
		});

		logger.debug("Indexing template definitions...");
		templateDefinitions.values().forEach(esClient::indexTemplateDefinition);
	}

	private void parseTemplate(String name, Map<String, String> templateDefinitionsPages,
	                           Map<String, TemplateDefinition> templateDefinitions) {
		TemplateDefinitionPageParseResult parseResult;
		try {
			parseResult = parserService.parseTemplateDefinitionPage(templateDefinitionsPages.get(name));
		} catch (ParseException e) {
			logger.error("Error parsing template with name '{}'", name);
			return;
		}
		if (!parseResult.getTemplates().isEmpty()) {
			parseResult.getTemplates().stream()
			           .filter(t -> !templateDefinitions.containsKey(t.getName()))
			           .forEach(t -> parseTemplate(t.getName(), templateDefinitionsPages, templateDefinitions));
		}
		templateDefinitions.put(name, parseResult.getTemplateDefinition());
		templateDefinitionsPages.remove(name);
	}
}