package wiktiopeggynary.parser;

import wiktiopeggynary.parser.template.model.Template;
import wiktiopeggynary.parser.template.model.TemplateDefinition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Krzysztof Witukiewicz
 */
public class TemplateDefinitionPageParseResult {

	private TemplateDefinition templateDefinition;
	private final Collection<Template> templates = new ArrayList<>();

	public TemplateDefinitionPageParseResult(TemplateDefinition templateDefinition, Collection<Template> templates) {
		this.templateDefinition = templateDefinition;
		this.templates.addAll(templates);
	}

	public TemplateDefinition getTemplateDefinition() {
		return templateDefinition;
	}

	public Collection<Template> getTemplates() {
		return templates;
	}
}
