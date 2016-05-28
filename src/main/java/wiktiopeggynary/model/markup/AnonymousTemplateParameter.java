package wiktiopeggynary.model.markup;

import wiktiopeggynary.model.visitor.TemplateParameterVisitor;

/**
 * @author Krzysztof Witukiewicz
 */
public class AnonymousTemplateParameter extends TemplateParameter {

	public AnonymousTemplateParameter(RichText value) {
		super(value);
	}

	@Override
	public void accept(TemplateParameterVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "param{" + getValue() + "'}";
	}


}
