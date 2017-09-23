package wiktiopeggynary.model.markup;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class InternalLink implements RichTextComponent {

    private final String pageTitle;

	private final String linkText;

	private InternalLink(@JsonProperty("pageTitle") String pageTitle, @JsonProperty("linkText") String linkText) {
		Validate.notBlank(pageTitle);
		this.pageTitle = pageTitle;
		this.linkText = linkText;
	}

    public String getPageTitle() {
        return pageTitle;
    }

    public String getLinkText() {
        return linkText;
    }

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Optional<InternalLink> mergeWith(RichTextComponent component) {
		return Optional.empty();
	}

	@Override
	public boolean isEmpty() {
		return StringUtils.isBlank(pageTitle) && StringUtils.isBlank(linkText);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		InternalLink that = (InternalLink) o;

		if (pageTitle != null ? !pageTitle.equals(that.pageTitle) : that.pageTitle != null) return false;
		return linkText != null ? linkText.equals(that.linkText) : that.linkText == null;

	}

	@Override
	public int hashCode() {
		int result = pageTitle != null ? pageTitle.hashCode() : 0;
		result = 31 * result + (linkText != null ? linkText.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "InternalLink{" +
				       "pageTitle='" + pageTitle + '\'' +
				       ", linkText='" + linkText + '\'' +
				       '}';
	}

	public static class Builder {

		private String pageTitle;
		private String linkText;

		public Builder withPageTitle(String pageTitle) {
			this.pageTitle = pageTitle;
			return this;
		}

		public Builder withLinkText(String linkText) {
			this.linkText = linkText;
			return this;
		}

		public InternalLink build() {
			return new InternalLink(pageTitle, linkText);
		}
	}
}
