package wiktiopeggynary.parser.template.model;

/**
 * @author Krzysztof Witukiewicz
 */
public class AnonymousTemplateParameter extends TemplateParameter {

	public AnonymousTemplateParameter(DisplayableAsText value) {
		super(value);
	}

	@Override
	public String toString() {
		return "param{" + getValue() + "'}";
	}


}
