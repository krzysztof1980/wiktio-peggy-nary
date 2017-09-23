package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.CursiveBlock;
import wiktiopeggynary.model.markup.InternalLink;
import wiktiopeggynary.model.markup.PlainText;
import wiktiopeggynary.model.markup.RichText;

/**
 * @author Krzysztof Witukiewicz
 */
public interface RichTextComponentVisitor {

	void visit(PlainText plainText);

	void visit(RichText richText);

	void visit(CursiveBlock cursiveBlock);

	void visit(InternalLink internalLink);
}
