package wiktiopeggynary.parser;

import wiktiopeggynary.parser.template.TemplateService;

public class WiktionaryParser extends MouseWiktionaryParser {

	public WiktionaryParser(TemplateService templateService) {
		sem.setTemplateService(templateService);
	}
}
