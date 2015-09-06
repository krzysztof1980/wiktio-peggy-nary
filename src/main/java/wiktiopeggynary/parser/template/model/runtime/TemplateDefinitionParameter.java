package wiktiopeggynary.parser.template.model.runtime;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class TemplateDefinitionParameter<T> {

	private final T identifier;
	private final String value;

	public TemplateDefinitionParameter(T identifier, String value) {
		if (identifier == null)
			throw new IllegalArgumentException("identifier must not be null");
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TemplateDefinitionParameter<?> that = (TemplateDefinitionParameter<?>) o;

		if (!identifier.equals(that.identifier)) return false;
		return !(value != null ? !value.equals(that.value) : that.value != null);

	}

	@Override
	public int hashCode() {
		int result = identifier.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
