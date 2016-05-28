package wiktiopeggynary.model.markup;

import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public interface RichTextComponent {

	void accept(RichTextComponentVisitor visitor);

	Optional<? extends RichTextComponent> mergeWith(RichTextComponent component);

	boolean isEmpty();
}
