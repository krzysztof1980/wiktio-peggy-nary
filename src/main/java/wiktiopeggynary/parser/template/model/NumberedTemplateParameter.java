package wiktiopeggynary.parser.template.model;

/**
 * @author Krzysztof Witukiewicz
 */
public class NumberedTemplateParameter extends TemplateParameter {

	private final Integer number;

    public NumberedTemplateParameter(Integer number, DisplayableAsText value) {
        super(value);
	    if (number == null)
		    throw new IllegalArgumentException("number must not be null");
	    this.number = number;
    }

	public Integer getNumber() {
		return number;
	}

	@Override
	public String toString() {
		return "param{" + number + "='" + getValue() + "'}";
	}
}
