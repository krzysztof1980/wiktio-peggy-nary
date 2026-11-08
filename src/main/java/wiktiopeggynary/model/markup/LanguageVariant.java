package wiktiopeggynary.model.markup;

import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public record LanguageVariant(Variant variant, String suffix) implements RichTextComponent {

    public enum Variant {
        British, American
    }

    @Override
    public void accept(RichTextComponentVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Optional<? extends RichTextComponent> mergeWith(RichTextComponent component) {
        return Optional.empty();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
