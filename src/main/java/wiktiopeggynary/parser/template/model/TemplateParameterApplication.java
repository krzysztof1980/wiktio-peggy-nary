package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class TemplateParameterApplication<T> implements DisplayableAsText {

	private final T identifier;

	private String defaultValue;

	public TemplateParameterApplication(T identifier) {
		this.identifier = identifier;
	}

	public T getIdentifier() {
		return identifier;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String asText(TemplateDefinitionParameter... parameters) {
		Optional<TemplateDefinitionParameter> param = Arrays.stream(parameters).filter(p -> p.getIdentifier().equals(identifier)).findFirst();
		return param.isPresent() ? param.get().getValue() : defaultValue;
	}
}
