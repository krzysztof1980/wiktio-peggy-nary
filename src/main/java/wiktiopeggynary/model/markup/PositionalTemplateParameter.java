package wiktiopeggynary.model.markup;

import wiktiopeggynary.model.visitor.TemplateParameterVisitor;

/**
 * @author Krzysztof Witukiewicz
 */
public class PositionalTemplateParameter extends TemplateParameter {

	public PositionalTemplateParameter(RichText value) {
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
