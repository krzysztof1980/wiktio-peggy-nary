package wiktiopeggynary.model.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public class TranslationMeaning {

    private String meaningNumber;
    private List<Translation> translations = new ArrayList<>();

    public TranslationMeaning(String meaningNumber) {
        this.meaningNumber = meaningNumber;
    }

    public String getMeaningNumber() {
        return meaningNumber;
    }

    public Collection<Translation> getTranslations() {
        return Collections.unmodifiableCollection(translations);
    }

    public void addTranslation(Translation t) {
        translations.add(t);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TranslationMeaning{");
        sb.append("meaningNumber='").append(meaningNumber).append('\'');
        sb.append(", translations=").append(translations);
        sb.append('}');
        return sb.toString();
    }
}