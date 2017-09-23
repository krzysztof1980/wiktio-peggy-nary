package wiktiopeggynary.parser;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryDumpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageDocument;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageParser;
import wiktiopeggynary.parser.mouse.ParserBase;
import wiktiopeggynary.parser.mouse.SourceString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class ParserService {
	private static final Logger logger = LoggerFactory.getLogger(ParserService.class);
	private static final Logger erroneousEntriesLogger = LoggerFactory.getLogger("erroneous_wiktionary_entries");
	
	private static final String IGNORED_ENTRIES_FILE = "ignored_entries.txt";
	
	private final ParserTaskExecutorFactory parserTaskExecutorFactory;
	private final List<String> ignoredEntries;
	
	public ParserService(ParserTaskExecutorFactory parserTaskExecutorFactory) {
		this.parserTaskExecutorFactory = parserTaskExecutorFactory;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(ParserService.class.getResourceAsStream(
				"/" + IGNORED_ENTRIES_FILE)))) {
			ignoredEntries = reader.lines().collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException("Problem reading ignored entries from the file " + IGNORED_ENTRIES_FILE, e);
		}
	}
	
	public void getWiktionaryEntriesFromDump(Path wiktionaryDumpPath,
	                                         Consumer<WiktionaryEntryPageParseResult> consumer) {
		AtomicInteger count = new AtomicInteger();
		ExecutorService executor = parserTaskExecutorFactory.createExecutor();
		WiktionaryPageParser wiktionaryPageParser = new WiktionaryPageParser(p -> {
			if (p.getNamespace() == null && !ignoredEntries.contains(p.getTitle())) {
				executor.execute(new ParseWiktionaryEntryPageTask(p, consumer, count));
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
	
	public Optional<WiktionaryEntryPageParseResult> parseWiktionaryEntryPage(String page) {
		if (page == null)
			throw new IllegalArgumentException("page must not be null");
		WiktionaryParser parser = new WiktionaryParser();
		setTraceInParser(parser);
		if (!page.endsWith("\n"))
			page = page + "\n";
		if (parser.parse(new SourceString(page)))
			return Optional.of(new WiktionaryEntryPageParseResult(parser.semantics().getWiktionaryEntries()));
		else {
			return Optional.empty();
		}
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
		final Consumer<WiktionaryEntryPageParseResult> consumer;
		final AtomicInteger count;
		
		private ParseWiktionaryEntryPageTask(WiktionaryPageDocument pageDocument,
		                                     Consumer<WiktionaryEntryPageParseResult> consumer,
		                                     AtomicInteger count) {
			this.pageDocument = pageDocument;
			this.consumer = consumer;
			this.count = count;
		}
		
		@Override
		public void run() {
			try {
				Optional<WiktionaryEntryPageParseResult> optParseResult = parseWiktionaryEntryPage(
						pageDocument.getText());
				if (optParseResult.isPresent()) {
					consumer.accept(optParseResult.get());
				} else {
					logger.error("Cannot parse entry with title '{}'", pageDocument.getTitle());
					erroneousEntriesLogger.error(pageDocument.getTitle() + " (cannot parse)");
				}
				int currentCount = count.incrementAndGet();
				if (currentCount % 5000 == 0)
					logger.debug("Parsed {} wiktionary pages", currentCount);
			} catch (ParseException e) {
				logger.error(e.getMessage());
				erroneousEntriesLogger.error(String.format("%s (%s)", pageDocument.getTitle(), e.getMessage()));
			} catch (Exception e) {
				logger.error(String.format("Error parsing entry with title '%s'", pageDocument.getTitle()),
				             e);
				erroneousEntriesLogger.error(
						String.format("%s (exception: %s: %s)", pageDocument.getTitle(), e.getClass().getSimpleName(),
						              e.getMessage()));
			}
		}
	}
}
