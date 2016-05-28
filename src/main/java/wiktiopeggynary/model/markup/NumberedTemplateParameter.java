package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.TemplateParameterVisitor;

/**
 * @author Krzysztof Witukiewicz
 */
public class NumberedTemplateParameter extends TemplateParameter {

	private final Integer number;

    public NumberedTemplateParameter(Integer number, RichText value) {
        super(value);
	    Validate.notNull(value);
	    this.number = number;
    }

	public Integer getNumber() {
		return number;
	}

	@Override
	public void accept(TemplateParameterVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "param{" + number + "='" + getValue() + "'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		NumberedTemplateParameter that = (NumberedTemplateParameter) o;

		return number.equals(that.number);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + number.hashCode();
		return result;
	}
}
