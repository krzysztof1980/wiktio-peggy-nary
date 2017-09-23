package wiktiopeggynary.meaning

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import wiktiopeggynary.model.markup.RichText

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class Meaning {

    MeaningKontext kontext
    RichText text
}
