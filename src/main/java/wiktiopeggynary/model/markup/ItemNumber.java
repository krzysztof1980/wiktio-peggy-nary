package wiktiopeggynary.model.markup;

import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public record ItemNumber(List<String> values) {

    public static ItemNumber singleNumber(String value) {
        return new ItemNumber(List.of(value));
    }

    public static ItemNumber range(String fromValue, String toValue) {
        return new ItemNumber(List.of(fromValue, toValue));
    }
}
