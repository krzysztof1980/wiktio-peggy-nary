package wiktiopeggynary.parser;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryDumpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageDocument;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageParser;
import wiktiopeggynary.parser.mouse.ParserBase;
import wiktiopeggynary.parser.mouse.SourceString;
import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.parser.template.parser.TemplateParser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Krzysztof Witukiewicz
 */
public class ParserService {
	private static final Logger logger = LoggerFactory.getLogger(ParserService.class);
	private static final Logger erroneousEntriesLogger = LoggerFactory.getLogger("erroneous_wiktionary_entries");
	
	private final ParserTaskExecutorFactory parserTaskExecutorFactory;
	
	public ParserService(ParserTaskExecutorFactory parserTaskExecutorFactory) {
		this.parserTaskExecutorFactory = parserTaskExecutorFactory;
	}
	
	public void getWiktionaryEntriesFromDump(Path wiktionaryDumpPath,
	                                         TemplateService templateService,
	                                         Consumer<WiktionaryEntryPageParseResult> consumer) {
		AtomicInteger count = new AtomicInteger();
		ExecutorService executor = parserTaskExecutorFactory.createExecutor();
		WiktionaryPageParser wiktionaryPageParser = new WiktionaryPageParser(p -> {
			if (p.getNamespace() == null) {
				executor.execute(new ParseWiktionaryEntryPageTask(p, templateService, consumer, count));
			}
		});
		WiktionaryDumpParser dumpParser = new WiktionaryDumpParser(wiktionaryPageParser);
		dumpParser.parse(wiktionaryDumpPath.toFile());
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("Exception waiting for threads parsing wiktionary pages to finish", e);
		}
	}
	
	public Map<String, String> getTemplateDefinitionPagesFromDump(Path wiktionaryDumpPath) {
		Map<String, String> result = new HashMap<>();
		WiktionaryPageParser wiktionaryPageParser = new WiktionaryPageParser(p -> {
			if ("Vorlage".equals(p.getNamespace()))
				result.put(p.getTitle(), p.getText());
		});
		processDump(wiktionaryDumpPath, wiktionaryPageParser);
		logger.info("Got {} template definitions from dump", result.size());
		return result;
	}
	
	private void processDump(Path wiktionaryDumpPath,
	                         WiktionaryPageParser wiktionaryPageParser) {
		WiktionaryDumpParser dumpParser = new WiktionaryDumpParser(wiktionaryPageParser);
		dumpParser.parse(wiktionaryDumpPath.toFile());
	}
	
	public Optional<WiktionaryEntryPageParseResult> parseWiktionaryEntryPage(String page,
	                                                                         TemplateService templateService) {
		if (page == null)
			throw new IllegalArgumentException("page must not be null");
		WiktionaryParser parser = new WiktionaryParser(templateService);
		setTraceInParser(parser);
		if (!page.endsWith("\n"))
			page = page + "\n";
		if (parser.parse(new SourceString(page)))
			return Optional.of(new WiktionaryEntryPageParseResult(parser.semantics().getWiktionaryEntries()));
		else {
			return Optional.empty();
		}
	}
	
	public TemplateDefinitionPageParseResult parseTemplateDefinitionPage(String page) {
		if (page == null)
			throw new IllegalArgumentException("page must not be null");
		TemplateParser parser = new TemplateParser();
		if (parser.parse(new SourceString(page)))
			return new TemplateDefinitionPageParseResult(parser.semantics().getTemplateDefinition(),
			                                             parser.semantics().getTemplates());
		else
			return null;
	}
	
	private void setTraceInParser(ParserBase parser) {
		String trace = System.getProperty("wiktiopeggynary.trace");
		if (trace != null) {
			parser.setTrace(trace);
			try {
				parser.getClass().getMethod("setMemo", int.class).invoke(parser, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ParseWiktionaryEntryPageTask implements Runnable {
		
		final WiktionaryPageDocument pageDocument;
		final TemplateService templateService;
		final Consumer<WiktionaryEntryPageParseResult> consumer;
		final AtomicInteger count;
		
		private ParseWiktionaryEntryPageTask(WiktionaryPageDocument pageDocument,
		                                     TemplateService templateService,
		                                     Consumer<WiktionaryEntryPageParseResult> consumer, AtomicInteger count) {
			this.pageDocument = pageDocument;
			this.templateService = templateService;
			this.consumer = consumer;
			this.count = count;
		}
		
		@Override
		public void run() {
			try {
				templateService.initPageSpecificTemplateDefinitions(pageDocument);
				Optional<WiktionaryEntryPageParseResult> optParseResult = parseWiktionaryEntryPage(
						pageDocument.getText(),
						templateService);
				if (optParseResult.isPresent()) {
					consumer.accept(optParseResult.get());
				} else {
					logger.error("Cannot parse wiktionary entry with title '{}'", pageDocument.getTitle());
					erroneousEntriesLogger.error(pageDocument.getTitle() + " (cannot parse)");
				}
				int currentCount = count.incrementAndGet();
				if (currentCount % 5000 == 0)
					logger.debug("Parsed {} wiktionary pages", currentCount);
			} catch (Exception e) {
				logger.error(String.format("Error parsing wiktionary entry with title '%s'", pageDocument.getTitle()),
				             e);
				erroneousEntriesLogger.error(
						String.format("%s (exception: %s: %s)", pageDocument.getTitle(), e.getClass().getSimpleName(),
						              e.getMessage()));
			}
		}
	}
}
