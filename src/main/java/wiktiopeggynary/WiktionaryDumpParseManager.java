package wiktiopeggynary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.persistence.TemplateEsRepository;
import wiktiopeggynary.persistence.WiktionaryEntryEsRepository;

import java.nio.file.Path;
import java.util.Collections;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryDumpParseManager {
	private static final Logger logger = LoggerFactory.getLogger(WiktionaryDumpParseManager.class);
	
	private final ParserService parserService;
	private final WiktionaryEntryEsRepository wiktionaryEntryRepo;
	private final TemplateEsRepository templateRepo;

	public WiktionaryDumpParseManager(ParserService parserService,
	                                  WiktionaryEntryEsRepository wiktionaryEntryRepo,
	                                  TemplateEsRepository templateRepo) {
		this.parserService = parserService;
		this.wiktionaryEntryRepo = wiktionaryEntryRepo;
		this.templateRepo = templateRepo;
	}

	public void parse(Path wiktionaryDumpPath) {
		logger.info("Deleting indices...");

		// TODO: consider removing the TemplateService altogether if not needed
//		logger.info("Reading in template definitions pages...");
//		TemplateService templateService = new TemplateService(parserService.getTemplateDefinitionPagesFromDump(
//				wiktionaryDumpPath), parserService);

		logger.info("Indexing wiktionary entries...");
		wiktionaryEntryRepo.prepareIndexForRebuild();
		parserService.getWiktionaryEntriesFromDump(wiktionaryDumpPath, new TemplateService(Collections.emptyMap(), parserService),
		                                           r -> r.getWiktionaryEntries().forEach(wiktionaryEntryRepo::indexWiktionaryEntry)
		);
		wiktionaryEntryRepo.indexRebuildDone();

//		logger.info("Indexing template definitions...");
//		templateRepo.prepareIndexForRebuild();
//		templateService.processTemplateDefinitions(templateRepo::indexTemplateDefinition);
//		templateRepo.indexRebuildDone();
	}
}