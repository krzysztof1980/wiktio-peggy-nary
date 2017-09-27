package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.*;

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
	
	@Override
	public void visit(KTemplate kTemplate) {
	
	}
}
