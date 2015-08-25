package wiktiopeggynary.parser.template.model.runtime;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class TemplateDefinitionParameter<T> {

	private final T identifier;
	private final String value;

	public TemplateDefinitionParameter(T identifier, String value) {
		this.identifier = identifier;
		this.value = value;
	}

	public T getIdentifier() {
		return identifier;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "param{" + identifier + "='" + value + "'}";
	}
}
