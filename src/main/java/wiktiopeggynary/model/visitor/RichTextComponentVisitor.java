package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.*;
import wiktiopeggynary.model.substantiv.MultiGender;
import wiktiopeggynary.model.translation.Translation;

/**
 * @author Krzysztof Witukiewicz
 */
public interface RichTextComponentVisitor {

	void visit(PlainText plainText);

	void visit(RichText richText);

	void visit(CursiveBlock cursiveBlock);

	void visit(InternalLink internalLink);
	
	void visit(WikipediaLink wikipediaLink);
	
	void visit(LanguageVariant languageVariant);
	
	void visit(KTemplate kTemplate);
	
	void visit(Translation translation);
	
	void visit(MultiGender gender);
}
