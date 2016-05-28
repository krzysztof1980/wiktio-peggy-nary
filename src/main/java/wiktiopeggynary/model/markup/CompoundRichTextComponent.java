package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class CompoundRichTextComponent implements RichTextComponent {

	private final Stack<RichTextComponent> components = new Stack<>();

	public void addComponent(RichTextComponent component) {
		Validate.notNull(component);
		if (components.isEmpty())
			components.push(component);
		else {
			RichTextComponent topComponent = components.peek();
			Optional<? extends RichTextComponent> mergedComponent = topComponent.mergeWith(component);
			if (mergedComponent.isPresent()) {
				components.pop();
				components.push(mergedComponent.get());
			} else {
				components.push(component);
			}
		}
	}

	public List<RichTextComponent> getComponents() {
		return Collections.unmodifiableList(components);
	}

	@Override
	public boolean isEmpty() {
		return components.isEmpty() || components.stream().allMatch(RichTextComponent::isEmpty);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CompoundRichTextComponent that = (CompoundRichTextComponent) o;

		return components.equals(that.components);

	}

	@Override
	public int hashCode() {
		return components.hashCode();
	}

	@Override
	public String toString() {
		return "CompoundRichTextComponent{" +
				       "components=" + components +
				       '}';
	}
}
