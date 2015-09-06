package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.parser.template.model.runtime.NamedTemplateDefinitionParameter;
import wiktiopeggynary.parser.template.model.runtime.NumberedTemplateDefinitionParameter;
import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;
import wiktiopeggynary.util.ServiceLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
		TemplateService templateService = ServiceLocator.getService(TemplateService.class);
		TemplateDefinition templateDefinition = templateService.findTemplateDefinition(templateName);
		return templateDefinition.asText(getParametersForTemplateDefinition(parameters));
	}
	
	private TemplateDefinitionParameter[] getParametersForTemplateDefinition(TemplateDefinitionParameter... currentParameters) {
		List<Integer> allocatedNumbers = parameters.stream()
		                                           .filter(p -> p instanceof NumberedTemplateParameter)
		                                           .map(p -> ((NumberedTemplateParameter) p).getNumber())
		                                           .collect(Collectors.toList());
		Integer[] nextNumber = {1};
		List<TemplateDefinitionParameter> templateDefinitionParameters = new ArrayList<>();
		parameters.forEach(p -> {
			String value = p.getValue().asText(currentParameters);
			if (p instanceof AnonymousTemplateParameter) {
				while (allocatedNumbers.contains(nextNumber[0]))
					nextNumber[0]++;
				templateDefinitionParameters.add(new NumberedTemplateDefinitionParameter(nextNumber[0]++, value));
			} else if (p instanceof NumberedTemplateParameter) {
				templateDefinitionParameters.add(new NumberedTemplateDefinitionParameter(((NumberedTemplateParameter) p).getNumber(), value));
			} else if (p instanceof NamedTemplateParameter) {
				templateDefinitionParameters.add(new NamedTemplateDefinitionParameter(((NamedTemplateParameter) p).getName(), value));
			} else {
				throw new IllegalArgumentException("Template parameter " + p.getClass().getSimpleName() + " cannot be recognized!");
			}
		});
		return templateDefinitionParameters.toArray(new TemplateDefinitionParameter[0]);
	}
}
