package wiktiopeggynary.model.meaning

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import wiktiopeggynary.model.markup.RichText

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class MeaningKontext {

    List<Part> parts
    RichText suffix

    @ToString(includePackage = false, ignoreNulls = true)
    @EqualsAndHashCode
    static class Part {

        Part() {
        }

        Part(RichText text, String separator) {
            this.text = text
            this.separator = separator
        }

        RichText text
        String separator
    }
}
