package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.model.markup.RichTextComponent;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public interface EvaluableRichTextComponent extends Evaluable, RichTextComponent {

	@Override
	default Optional<? extends RichTextComponent> mergeWith(RichTextComponent component) {
		throw new IllegalStateException("Can be called only on the result of evaluate-Method");
	}

	@Override
	default boolean isEmpty() {
		throw new IllegalStateException("Can be called only on the result of evaluate-Method");
	}
}
