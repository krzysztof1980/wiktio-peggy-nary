package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;

/**
 * @author Krzysztof Witukiewicz
 */
public class Constant {

	private final String name;
	private final RichText value;

	public Constant(String name, RichText value) {
		Validate.notBlank(name);
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public RichText getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Constant{" +
				       "name='" + name + '\'' +
				       ", value=" + value +
				       '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Constant constant = (Constant) o;

		if (!name.equals(constant.name)) return false;
		return value != null ? value.equals(constant.value) : constant.value == null;

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
