package wiktiopeggynary.model.markup

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class ItemNumber {

    String[] values

    private ItemNumber() {
    }

    static ItemNumber singleNumber(String value) {
        ItemNumber itemNumber = new ItemNumber()
        itemNumber.values = [value]
        return itemNumber
    }

    static ItemNumber range(String fromValue, String toValue) {
        ItemNumber itemNumber = new ItemNumber()
        itemNumber.values = [fromValue, toValue]
        return itemNumber
    }
}
