package wiktiopeggynary.model.markup;

/**
 * @author Krzysztof Witukiewicz
 */
public class InternalLink implements DisplayableAsText {

    private final String pageTitle;
    private String linkText;

    public InternalLink(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    @Override
    public String asText() {
        return linkText != null ? linkText : pageTitle;
    }
}
