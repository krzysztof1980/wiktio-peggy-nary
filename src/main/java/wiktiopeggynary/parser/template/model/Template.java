package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public class Template implements DisplayableAsText {

	private final String templateName;
	private final List<TemplateParameter> parameters = new ArrayList<>();

	public Template(String templateName) {
		this.templateName = templateName;
	}

	public String getName() {
		return templateName;
	}

	public List<TemplateParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	public void addParameter(TemplateParameter parameter) {
		parameters.add(parameter);
	}

	@Override
	public String asText(TemplateDefinitionParameter... parameters) {
		return null;
	}
}
