package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.TemplateParameterVisitor;

/**
 * @author Krzysztof Witukiewicz
 */
public class NamedTemplateParameter extends TemplateParameter {

	private final String name;

    public NamedTemplateParameter(String name, RichText value) {
        super(value);
	    Validate.notNull(name);
	    this.name = name;
    }

	public String getName() {
		return name;
	}

	@Override
	public void accept(TemplateParameterVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "param{" + name + "='" + getValue() + "'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		NamedTemplateParameter that = (NamedTemplateParameter) o;

		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}
