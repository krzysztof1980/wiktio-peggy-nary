package wiktiopeggynary.model.markup;

/**
 * @author Krzysztof Witukiewicz
 */
public class PlainText implements DisplayableAsText {

    private final String text;

    public PlainText(String text) {
        if (text == null)
            throw new IllegalArgumentException("text must not be null");
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String asText() {
        return text;
    }
}
