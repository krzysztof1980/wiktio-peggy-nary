package wiktiopeggynary.model.meaning

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.commons.lang3.Validate
import wiktiopeggynary.model.markup.ItemNumber
import wiktiopeggynary.model.markup.RichText

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class Meaning {

    List<ItemNumber> numbers
    MeaningKontext kontext
    RichText text
    final List<Meaning> subMeanings = new ArrayList<>()

    List<Meaning> getSubMeanings() {
        return Collections.unmodifiableList(subMeanings)
    }

    void addSubMeaing(Meaning subMeaning) {
        Validate.notNull(subMeaning)
        subMeanings.add(subMeaning)
    }
}
