package wiktiopeggynary.model.meaning;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.markup.RichText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Krzysztof Witukiewicz
 */
public class Meaning {

    private List<ItemNumber> numbers;
    private RichText text;
    private final List<Meaning> subMeanings = new ArrayList<>();

    public List<ItemNumber> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<ItemNumber> numbers) {
        this.numbers = numbers;
    }

    public RichText getText() {
        return text;
    }

    public void setText(RichText text) {
        this.text = text;
    }

    public List<Meaning> getSubMeanings() {
        return Collections.unmodifiableList(subMeanings);
    }

    public void addSubMeaning(Meaning subMeaning) {
        Validate.notNull(subMeaning);
        subMeanings.add(subMeaning);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meaning meaning = (Meaning) o;
        return Objects.equals(numbers, meaning.numbers)
                && Objects.equals(text, meaning.text)
                && Objects.equals(subMeanings, meaning.subMeanings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers, text, subMeanings);
    }

    @Override
    public String toString() {
        return "Meaning{" +
                "numbers=" + numbers +
                ", text=" + text +
                ", subMeanings=" + subMeanings +
                '}';
    }
}
