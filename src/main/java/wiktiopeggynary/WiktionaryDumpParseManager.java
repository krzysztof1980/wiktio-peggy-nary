package wiktiopeggynary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.persistence.WiktionaryEntryEsRepository;

import java.nio.file.Path;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryDumpParseManager {
	private static final Logger logger = LoggerFactory.getLogger(WiktionaryDumpParseManager.class);
	
	private final ParserService parserService;
	private final WiktionaryEntryEsRepository wiktionaryEntryRepo;

	public WiktionaryDumpParseManager(ParserService parserService,
	                                  WiktionaryEntryEsRepository wiktionaryEntryRepo) {
		this.parserService = parserService;
		this.wiktionaryEntryRepo = wiktionaryEntryRepo;
	}

	public void parse(Path wiktionaryDumpPath) {
		logger.info("Deleting indices...");

		logger.info("Indexing wiktionary entries...");
		wiktionaryEntryRepo.prepareIndexForRebuild();
		parserService.getWiktionaryEntriesFromDump(wiktionaryDumpPath,
		                                           r -> r.getWiktionaryEntries().forEach(wiktionaryEntryRepo::indexWiktionaryEntry)
		);
		wiktionaryEntryRepo.indexRebuildDone();
	}
}