package wiktiopeggynary.model.translation;

import wiktiopeggynary.model.substantiv.MultiGender;

/**
 * @author Krzysztof Witukiewicz
 */
public class Translation {

    private String internalLink;
    private String transcription;
    private String label;
    private String externalLink;

    private MultiGender gender;

    public String getInternalLink() {
        return internalLink;
    }

    public void setInternalLink(String internalLink) {
        this.internalLink = internalLink;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
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
    public String toString() {
        final StringBuilder sb = new StringBuilder("Translation{");
        sb.append("internalLink='").append(internalLink).append('\'');
        if (transcription != null)
            sb.append(", transcription='").append(transcription).append('\'');
        if (label != null)
            sb.append(", label='").append(label).append('\'');
        if (externalLink != null)
            sb.append(", externalLink='").append(externalLink).append('\'');
        if (gender != null)
            sb.append(", gender='").append(gender).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
