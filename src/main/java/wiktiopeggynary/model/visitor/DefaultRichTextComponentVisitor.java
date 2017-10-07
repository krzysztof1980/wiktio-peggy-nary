package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.*;
import wiktiopeggynary.model.substantiv.MultiGender;
import wiktiopeggynary.model.translation.Translation;

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
	public void visit(WikipediaLink wikipediaLink) {
	
	}
	
	@Override
	public void visit(LanguageVariant languageVariant) {
	
	}
	
	@Override
	public void visit(InternalLink internalLink) {
	}
	
	@Override
	public void visit(KTemplate kTemplate) {
	
	}
	
	@Override
	public void visit(Translation translation) {
	
	}
	
	@Override
	public void visit(MultiGender gender) {
	
	}
}
