package wiktiopeggynary.parser.template;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.parser.ParserService;
import wiktiopeggynary.parser.TemplateDefinitionPageParseResult;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageDocument;
import wiktiopeggynary.parser.template.model.TemplateDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * @deprecated Complexity of this solution seems to be unneeded for the problem at hand
 * @author Krzysztof Witukiewicz
 */
@Deprecated
public class TemplateService {
	private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);
	private static final Logger missingDefLogger = LoggerFactory.getLogger("missing_template_definitions");
	private static final Logger erroneousDefLogger = LoggerFactory.getLogger("erroneous_template_definitions");

	private final ConcurrentMap<String, Future<TemplateDefinition>> templateDefinitions = new ConcurrentHashMap<>();
	private ConcurrentMap<String, String> templateDefinitionsPages;

	private final ThreadLocal<Map<String, TemplateDefinition>> pageSpecificTemplateDefinitions = new ThreadLocal<>();

	private final ParserService parserService;

	public TemplateService(Map<String, String> templateDefinitionPages, ParserService parserService) {
		Validate.notNull(templateDefinitionPages);
		Validate.notNull(parserService);
		templateDefinitionsPages = new ConcurrentHashMap<>(templateDefinitionPages);
		this.parserService = parserService;
	}

	public void initPageSpecificTemplateDefinitions(WiktionaryPageDocument page) {
		Map<String, TemplateDefinition> pageSpecificTemplateDefinitions = new HashMap<>();
		pageSpecificTemplateDefinitions.put("PAGENAME", new TemplateDefinition(new RichText(page.getTitle())));
		this.pageSpecificTemplateDefinitions.set(pageSpecificTemplateDefinitions);
	}

	public TemplateDefinition parseTemplateDefinitionPageForTemplate(String templateName) {
		if (pageSpecificTemplateDefinitions.get().containsKey(templateName))
			return pageSpecificTemplateDefinitions.get().get(templateName);
		TemplateDefinition templateDefinition = null;
		boolean interrupted = false;
		while (true) {
			Future<TemplateDefinition> f = templateDefinitions.get(templateName);
			if (f == null) {
				Callable<TemplateDefinition> eval = () -> {
					String templateDefPage = templateDefinitionsPages.get(templateName);
					if (templateDefPage == null) {
						missingDefLogger.error(templateName);
						return null;
					}
					TemplateDefinitionPageParseResult parseResult =
							parserService.parseTemplateDefinitionPage(templateDefPage);
					if (!parseResult.getTemplates().isEmpty()) {
						parseResult.getTemplates().stream()
						           .forEach(t -> parseTemplateDefinitionPageForTemplate(t.getName()));
					}
					return parseResult.getTemplateDefinition();
				};
				FutureTask<TemplateDefinition> ft = new FutureTask<>(eval);
				f = templateDefinitions.putIfAbsent(templateName, ft);
				if (f == null) {
					f = ft;
					ft.run();
				}
			}
			try {
				templateDefinition = f.get();
				templateDefinitionsPages.remove(templateName);
				break;
			} catch (InterruptedException e) {
				interrupted = true;
				templateDefinitions.remove(templateName, f);
			} catch (ExecutionException e) {
				logger.error("Error parsing template with name '{}'", templateName);
				erroneousDefLogger.error(templateName, e);
				break;
			}
		}
		if (interrupted)
			Thread.currentThread().interrupt();
		return templateDefinition;
	}

	public TemplateDefinition findTemplateDefinition(String templateName) {
		TemplateDefinition templateDefinition = null;
		boolean interrupted = false;
		while (true) {
			Future<TemplateDefinition> f = templateDefinitions.get(templateName);
			try {
				if (f != null)
					templateDefinition = f.get();
				break;
			} catch (InterruptedException e) {
				interrupted = true;
			} catch (ExecutionException e) {
				// should not happen: already handled in parseTemplateDefinitionPageForTemplate
				logger.error("Error retrieving definition for template {}", templateName);
				break;
			}
		}
		if (interrupted)
			Thread.currentThread().interrupt();
		return templateDefinition;
	}

	public void processTemplateDefinitions(Consumer<TemplateDefinition> consumer) {
		templateDefinitions.keySet().forEach(k -> {
			TemplateDefinition def = findTemplateDefinition(k);
			if (def != null)
				consumer.accept(def);
		});
	}
}
