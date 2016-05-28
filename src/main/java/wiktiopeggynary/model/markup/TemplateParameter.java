package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.TemplateParameterVisitor;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class TemplateParameter {

	private final RichText value;

	public TemplateParameter(RichText value) {
		Validate.notNull(value);
		this.value = value;
	}

	public RichText getValue() {
		return value;
	}

	public abstract void accept(TemplateParameterVisitor visitor);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TemplateParameter that = (TemplateParameter) o;

		return value.equals(that.value);

	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
