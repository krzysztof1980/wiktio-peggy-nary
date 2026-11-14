package wiktiopeggynary.parser.dumpparser;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryPageDocument {

    private String namespace;
    private String title;
    private String text;
    private long sequence;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
