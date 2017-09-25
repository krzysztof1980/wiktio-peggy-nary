package wiktiopeggynary.model.translation;

import com.fasterxml.jackson.annotation.JsonProperty;
import wiktiopeggynary.markup.ItemNumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public class TranslationMeaning {

    private List<ItemNumber> meaningNumbers;
    private List<Translation> translations = new ArrayList<>();

    public TranslationMeaning(@JsonProperty("meaningNumbers") List<ItemNumber> meaningNumbers) {
        this.meaningNumbers = new ArrayList<>(meaningNumbers);
    }

    public List<ItemNumber> getMeaningNumbers() {
        return Collections.unmodifiableList(meaningNumbers);
    }

    public List<Translation> getTranslations() {
        return Collections.unmodifiableList(translations);
    }

    public void addTranslation(Translation t) {
        translations.add(t);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranslationMeaning{");
        sb.append("meaningNumbers='").append(meaningNumbers).append('\'');
        sb.append(", translations=").append(translations);
        sb.append('}');
        return sb.toString();
    }
}