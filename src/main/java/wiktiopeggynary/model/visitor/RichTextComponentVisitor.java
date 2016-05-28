package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.*;
import wiktiopeggynary.parser.template.model.TemplateParameterApplication;
import wiktiopeggynary.parser.template.model.functions.ParserFunction;

/**
 * @author Krzysztof Witukiewicz
 */
public interface RichTextComponentVisitor {

	void visit(PlainText plainText);

	void visit(RichText richText);

	void visit(CursiveBlock cursiveBlock);

	void visit(InternalLink internalLink);

	void visit(Template template);

	void visit(ParserFunction function);
	
	void visit(TemplateParameterApplication parameterApplication);
}
