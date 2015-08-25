package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class CompoundDisplayableAsText implements DisplayableAsText {

    private final Stack<DisplayableAsText> components = new Stack<>();

    public void addComponent(DisplayableAsText component) {
        if (component == null)
            throw new IllegalArgumentException("component must not be null");
        if (component instanceof PlainText)
            addPlainText((PlainText) component);
        else
            components.add(component);
    }

	public List<DisplayableAsText> getComponents() {
		return Collections.unmodifiableList(components);
	}

	private void addPlainText(PlainText plainText) {
        DisplayableAsText topComponent = !components.isEmpty() ? components.peek() : null;
        if (topComponent != null && topComponent instanceof PlainText) {
            components.pop();
            components.push(new PlainText(((PlainText) topComponent).getText() + plainText.getText()));
        } else {
            components.push(plainText);
        }
    }

    @Override
    public String asText(TemplateDefinitionParameter... parameters) {
        return components.stream().map(c -> c.asText(parameters)).collect(Collectors.joining());
    }
}
