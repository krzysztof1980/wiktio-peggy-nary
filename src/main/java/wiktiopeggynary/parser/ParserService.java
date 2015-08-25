package wiktiopeggynary.parser;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryDumpParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.parser.dumpparser.WiktionaryAutocorrectionService;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageParser;
import wiktiopeggynary.parser.mouse.ParserBase;
import wiktiopeggynary.parser.mouse.SourceString;
import wiktiopeggynary.parser.template.parser.TemplateParser;
import wiktiopeggynary.util.ServiceLocator;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Krzysztof Witukiewicz
 */
public final class ParserService {
	private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

	public void getWiktionaryEntriesFromDump(Path wiktionaryDumpPath,
	                                         Consumer<WiktionaryEntryPageParseResult> consumer) {
		WiktionaryPageParser wiktionaryPageParser = new WiktionaryPageParser(p -> {
			if (p.getNamespace() == null) {
				try {
					consumer.accept(parseWiktionaryEntryPage(p.getText()));
				} catch (ParseException e) {
					logger.error("Error parsing wiktionary entry with title '{}'", p.getTitle());
				}
			}
		});
		processDump(wiktionaryDumpPath, wiktionaryPageParser);
	}

	public Map<String, String> getTemplateDefinitionPagesFromDump(Path wiktionaryDumpPath) {
		Map<String, String> result = new HashMap<>();
		WiktionaryPageParser wiktionaryPageParser = new WiktionaryPageParser(p -> {
			if ("Vorlage".equals(p.getNamespace()))
				result.put(p.getTitle(), p.getText());
		});
		processDump(wiktionaryDumpPath, wiktionaryPageParser);
		return result;
	}

	private void processDump(Path wiktionaryDumpPath,
	                         WiktionaryPageParser wiktionaryPageParser) {
		WiktionaryDumpParser dumpParser = new WiktionaryDumpParser(wiktionaryPageParser);
		dumpParser.parse(wiktionaryDumpPath.toFile());
	}

	public WiktionaryEntryPageParseResult parseWiktionaryEntryPage(String page) throws ParseException {
		if (page == null)
			throw new IllegalArgumentException("page must not be null");
		WiktionaryParser parser = new WiktionaryParser();
		setTraceInParser(parser);
		String correctedPage = ServiceLocator.getService(WiktionaryAutocorrectionService.class).correctPage(page);
		if (parser.parse(new SourceString(correctedPage)))
			return new WiktionaryEntryPageParseResult(parser.semantics().getWiktionaryEntries(),
			                                          parser.semantics().getTemplates());
		else
			throw new ParseException();
	}

	public TemplateDefinitionPageParseResult parseTemplateDefinitionPage(String page) throws ParseException {
		if (page == null)
			throw new IllegalArgumentException("page must not be null");
		TemplateParser parser = new TemplateParser();
		if (parser.parse(new SourceString(page)))
			return new TemplateDefinitionPageParseResult(parser.semantics().getTemplateDefinition(),
			                                             parser.semantics().getTemplates());
		else
			throw new ParseException();
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
}
