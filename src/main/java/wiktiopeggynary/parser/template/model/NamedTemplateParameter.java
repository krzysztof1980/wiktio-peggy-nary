package wiktiopeggynary.parser.template.model;

/**
 * @author Krzysztof Witukiewicz
 */
public class NamedTemplateParameter extends TemplateParameter {

	private final String name;

    public NamedTemplateParameter(String name, DisplayableAsText value) {
        super(value);
	    if (name == null)
		    throw new IllegalArgumentException("name must not be null");
	    this.name = name;
    }

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "param{" + name + "='" + getValue() + "'}";
	}
}
