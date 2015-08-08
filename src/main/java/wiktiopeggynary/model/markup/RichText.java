package wiktiopeggynary.model.markup;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class RichText implements DisplayableAsText {

    private final Stack<DisplayableAsText> contents = new Stack<>();

    public RichText() {
    }

    public RichText(DisplayableAsText component) {
        addComponent(component);
    }

    public List<DisplayableAsText> getContents() {
        return Collections.unmodifiableList(contents);
    }

    public void addComponent(DisplayableAsText component) {
        if (component == null)
            throw new IllegalArgumentException("component must not be null");
        if (component instanceof PlainText)
            addPlainText((PlainText) component);
        else
            contents.add(component);
    }

    private void addPlainText(PlainText plainText) {
        DisplayableAsText topComponent = !contents.isEmpty() ? contents.peek() : null;
        if (topComponent != null && topComponent instanceof PlainText) {
            contents.pop();
            contents.push(new PlainText(topComponent.asText() + plainText.asText()));
        } else {
            contents.push(plainText);
        }
    }

    @Override
    public String asText() {
        return contents.stream().map(DisplayableAsText::asText).collect(Collectors.joining());
    }
}
