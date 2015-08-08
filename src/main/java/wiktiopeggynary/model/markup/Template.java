package wiktiopeggynary.model.markup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class Template implements DisplayableAsText {

    private final String name;
    private final List<RichText> attributes = new ArrayList<>();

    public Template(String name) {
        if (name == null)
            throw new IllegalArgumentException("template name must not be null");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<RichText> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    public void addAttribute(RichText attribute) {
        if (attribute == null)
            throw new IllegalArgumentException("attribute must not be null");
        attributes.add(attribute);
    }

    @Override
    public String asText() {
        // TODO: change to use TemplatePrinter or something similar
        String attrText = attributes.stream().map(DisplayableAsText::asText).collect(Collectors.joining("|"));
        return String.format("%s(%s)", name, attrText);
    }

    @Override
    public String toString() {
        return String.format("Template{name='%s', #%s attributes}", name, attributes.size());
    }
}
