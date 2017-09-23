package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.CursiveBlock;
import wiktiopeggynary.model.markup.InternalLink;
import wiktiopeggynary.model.markup.PlainText;
import wiktiopeggynary.model.markup.RichText;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class DefaultRichTextComponentVisitor implements RichTextComponentVisitor {

	@Override
	public void visit(PlainText plainText) {
	}

	@Override
	public void visit(RichText richText) {
	}

	@Override
	public void visit(CursiveBlock cursiveBlock) {
	}

	@Override
	public void visit(InternalLink internalLink) {
	}
}
