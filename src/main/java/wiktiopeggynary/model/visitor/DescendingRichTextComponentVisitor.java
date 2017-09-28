package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.CursiveBlock;
import wiktiopeggynary.model.markup.RichText;

/**
 * @author Krzysztof Witukiewicz
 */
public class DescendingRichTextComponentVisitor extends DefaultRichTextComponentVisitor {
	
	@Override
	public void visit(RichText richText) {
		richText.getComponents().forEach(c -> c.accept(this));
	}
	
	@Override
	public void visit(CursiveBlock cursiveBlock) {
		cursiveBlock.getBody().accept(this);
	}
}
