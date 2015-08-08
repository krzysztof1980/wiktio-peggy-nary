package wiktiopeggynary.model;

import wiktiopeggynary.model.markup.RichText;

/**
 * @author Krzysztof Witukiewicz
 */
public class Meaning {

    private final RichText text;

    public Meaning(RichText text) {
        this.text = text;
    }

    public RichText getText() {
        return text;
    }
}
