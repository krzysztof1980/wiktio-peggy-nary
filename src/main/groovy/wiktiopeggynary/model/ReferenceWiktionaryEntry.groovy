package wiktiopeggynary.model

import groovy.transform.ToString
import org.apache.commons.lang3.Validate

/**
 * @author Krzysztof Witukiewicz
 */
@ToString
class ReferenceWiktionaryEntry extends WiktionaryEntry {

    ReferenceType referenceType
	String referenceValue

    enum ReferenceType {
        OLD_SPELLING("Alte Schreibweise"),
        SWISS_SPELLING("Schweizer und Liechtensteiner Schreibweise"),
        ALTERNATIVE_SPELLING("Alternative Schreibweise")

        final String referenceName

        ReferenceType(String referenceName) {
            this.referenceName = referenceName
        }

        static ReferenceType fromReferenceName(String referenceName) {
            def referenceType = values().find {v -> v.referenceName == referenceName}
            Validate.isTrue(referenceType != null, "Invalid value for referenceName: " + referenceName)
            return referenceType
        }
    }
}
