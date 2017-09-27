package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.*;

/**
 * @author Krzysztof Witukiewicz
 */
public interface RichTextComponentVisitor {

	void visit(PlainText plainText);

	void visit(RichText richText);

	void visit(CursiveBlock cursiveBlock);

	void visit(InternalLink internalLink);
	
	void visit(KTemplate kTemplate);
}
