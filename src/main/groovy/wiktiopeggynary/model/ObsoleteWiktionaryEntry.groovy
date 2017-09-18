package wiktiopeggynary.model

import groovy.transform.ToString

/**
 * @author Krzysztof Witukiewicz
 */
@ToString
class ObsoleteWiktionaryEntry extends WiktionaryEntry {

    String description
	String correctSpelling

    String getObsoleteSpelling() {
        return lemma
    }
}
