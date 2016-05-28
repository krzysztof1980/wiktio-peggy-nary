package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.AnonymousTemplateParameter;
import wiktiopeggynary.model.markup.NamedTemplateParameter;
import wiktiopeggynary.model.markup.NumberedTemplateParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public interface TemplateParameterVisitor {

	void visit(AnonymousTemplateParameter param);

	void visit(NumberedTemplateParameter param);

	void visit(NamedTemplateParameter param);
}
