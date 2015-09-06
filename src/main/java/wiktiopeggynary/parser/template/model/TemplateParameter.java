package wiktiopeggynary.parser.template.model;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class TemplateParameter<T> {

	private final DisplayableAsText value;

	public TemplateParameter(DisplayableAsText value) {
		if (value == null)
			throw new IllegalArgumentException("value must not be null");
		this.value = value;
	}

	public DisplayableAsText getValue() {
		return value;
	}
}
