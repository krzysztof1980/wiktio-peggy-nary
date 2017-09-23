package wiktiopeggynary.model.markup;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
public interface RichTextComponent {

	void accept(RichTextComponentVisitor visitor);

	Optional<? extends RichTextComponent> mergeWith(RichTextComponent component);

	boolean isEmpty();
}
