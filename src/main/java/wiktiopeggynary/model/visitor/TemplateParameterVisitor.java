package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.NamedTemplateParameter;
import wiktiopeggynary.model.markup.NumberedTemplateParameter;
import wiktiopeggynary.model.markup.PositionalTemplateParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public interface TemplateParameterVisitor {

	void visit(PositionalTemplateParameter param);

	void visit(NumberedTemplateParameter param);

	void visit(NamedTemplateParameter param);
}
