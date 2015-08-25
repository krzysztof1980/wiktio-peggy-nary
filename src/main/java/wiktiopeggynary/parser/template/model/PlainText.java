package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public class PlainText implements DisplayableAsText {

	public static final PlainText EMPTY_TEXT = new PlainText("");

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
    public String asText(TemplateDefinitionParameter... parameters) {
        return text;
    }
}
