package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.DefaultRichTextComponentVisitor;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class PlainText implements RichTextComponent {

    private final String text;

    public PlainText(String text) {
	    Validate.notNull(text);
	    this.text = text;
    }

	public static PlainText empty() {
		return new PlainText("");
	}

    public String getText() {
        return text;
    }

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Optional<PlainText> mergeWith(RichTextComponent component) {
		final PlainText[] mergedObj = new PlainText[1];
		component.accept(new DefaultRichTextComponentVisitor() {
			@Override
			public void visit(PlainText plainText) {
				mergedObj[0] = new PlainText(text + plainText.getText());
			}
		});
		return Optional.ofNullable(mergedObj[0]);
	}

	@Override
	public boolean isEmpty() {
		return StringUtils.isBlank(text);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PlainText plainText = (PlainText) o;

		return text.equals(plainText.text);

	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}

	@Override
	public String toString() {
		return "PlainText{" +
				       "text='" + text + '\'' +
				       '}';
	}
}
