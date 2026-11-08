package wiktiopeggynary.model;

import java.util.Objects;

/**
 * @author Krzysztof Witukiewicz
 */
public class ReferenceWiktionaryEntry extends WiktionaryEntry {

    private ReferenceType referenceType;
    private String referenceValue;

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(String referenceValue) {
        this.referenceValue = referenceValue;
    }

    public enum ReferenceType {
        OLD_SPELLING("Alte Schreibweise"),
        SWISS_SPELLING("Schweizer und Liechtensteiner Schreibweise"),
        ALTERNATIVE_SPELLING("Alternative Schreibweise");

        private final String referenceName;

        ReferenceType(String referenceName) {
            this.referenceName = referenceName;
        }

        public String getReferenceName() {
            return referenceName;
        }

        public static ReferenceType fromReferenceName(String referenceName) {
            for (ReferenceType referenceType : values()) {
                if (referenceType.referenceName.equals(referenceName))
                    return referenceType;
            }
            throw new IllegalArgumentException("Invalid value for referenceName: " + referenceName);
        }
    }

    @Override
    public String toString() {
        return "ReferenceWiktionaryEntry{" +
                "referenceType=" + referenceType +
                ", referenceValue='" + referenceValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ReferenceWiktionaryEntry entry = (ReferenceWiktionaryEntry) o;
        return referenceType == entry.referenceType
                && Objects.equals(referenceValue, entry.referenceValue);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(referenceType);
        result = 31 * result + Objects.hashCode(referenceValue);
        return result;
    }
}
