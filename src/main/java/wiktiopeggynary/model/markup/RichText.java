package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.DefaultRichTextComponentVisitor;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class RichText extends CompoundRichTextComponent {

	public RichText() {
	}

    public RichText(RichTextComponent... components) {
	    Arrays.stream(components).forEach(this::addComponent);
    }

	public RichText(String plainText) {
		addComponent(new PlainText(plainText));
	}

	public static RichText empty() {
		return new RichText();
	}

	@Override
	public void addComponent(RichTextComponent component) {
		Validate.notNull(component);
		if (component instanceof RichText) {
			((RichText) component).getComponents().forEach(this::addComponent);
		} else {
			super.addComponent(component);
		}
	}

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Optional<RichText> mergeWith(RichTextComponent component) {
		final RichText[] mergedObj = new RichText[1];
		component.accept(new DefaultRichTextComponentVisitor() {
			@Override
			public void visit(RichText richText) {
				mergedObj[0] = new RichText();
				getComponents().forEach(mergedObj[0]::addComponent);
				richText.getComponents().forEach(mergedObj[0]::addComponent);
			}
		});
		return Optional.ofNullable(mergedObj[0]);
	}

	@Override
	public String toString() {
		return "RichText{" +
				       "components=" + getComponents() +
				       '}';
	}
}
