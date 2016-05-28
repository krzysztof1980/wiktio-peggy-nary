package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.DefaultRichTextComponentVisitor;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class CursiveBlock implements RichTextComponent {

	private final RichText body;

	public CursiveBlock(RichText body) {
		Validate.notNull(body);
		this.body = body;
	}

	public RichText getBody() {
		return body;
	}

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Optional<CursiveBlock> mergeWith(RichTextComponent component) {
		final CursiveBlock[] mergedObj = new CursiveBlock[1];
		component.accept(new DefaultRichTextComponentVisitor() {
			@Override
			public void visit(CursiveBlock cursiveBlock) {
				Optional<RichText> mergedBody = body.mergeWith(cursiveBlock.body);
				if (mergedBody.isPresent())
					mergedObj[0] = new CursiveBlock(mergedBody.get());
			}
		});
		return Optional.ofNullable(mergedObj[0]);
	}

	@Override
	public boolean isEmpty() {
		return body.isEmpty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CursiveBlock that = (CursiveBlock) o;

		return body.equals(that.body);

	}

	@Override
	public int hashCode() {
		return body.hashCode();
	}

	@Override
	public String toString() {
		return "CursiveBlock{" +
				       "body=" + body +
				       '}';
	}
}
