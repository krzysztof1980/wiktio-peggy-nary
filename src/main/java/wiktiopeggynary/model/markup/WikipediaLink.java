package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.StringUtils;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public record WikipediaLink(String pageTitle, String linkText) implements RichTextComponent {

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
        return StringUtils.isBlank(pageTitle) && StringUtils.isBlank(linkText);
    }
}
