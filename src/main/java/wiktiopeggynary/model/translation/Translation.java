package wiktiopeggynary.model.translation;

import org.apache.commons.lang3.StringUtils;
import wiktiopeggynary.model.markup.RichTextComponent;
import wiktiopeggynary.model.substantiv.MultiGender;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class Translation implements RichTextComponent{

    private String internalLink;
    private String transcription;
    private String label;

    private MultiGender gender;
	
	public String getInternalLink() {
        return internalLink;
    }

    public void setInternalLink(String internalLink) {
        this.internalLink = internalLink;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public MultiGender getGender() {
        return gender;
    }

    public void setGender(MultiGender gender) {
        this.gender = gender;
    }
	
	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public Optional<Translation> mergeWith(RichTextComponent component) {
		return Optional.empty();
	}
	
	@Override
	public boolean isEmpty() {
		return StringUtils.isEmpty(internalLink) && StringUtils.isEmpty(transcription) && StringUtils.isEmpty(label);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Translation that = (Translation) o;
		
		if (internalLink != null ? !internalLink.equals(that.internalLink) : that.internalLink != null) return false;
		if (transcription != null ? !transcription.equals(that.transcription) : that.transcription != null)
			return false;
		if (label != null ? !label.equals(that.label) : that.label != null) return false;
		return gender != null ? gender.equals(that.gender) : that.gender == null;
	}
	
	@Override
	public int hashCode() {
		int result = internalLink != null ? internalLink.hashCode() : 0;
		result = 31 * result + (transcription != null ? transcription.hashCode() : 0);
		result = 31 * result + (label != null ? label.hashCode() : 0);
		result = 31 * result + (gender != null ? gender.hashCode() : 0);
		return result;
	}
	
	@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Translation{");
        sb.append("internalLink='").append(internalLink).append('\'');
        if (transcription != null)
            sb.append(", transcription='").append(transcription).append('\'');
        if (label != null)
            sb.append(", label='").append(label).append('\'');
        if (gender != null)
            sb.append(", gender='").append(gender).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
