package wiktiopeggynary.model;

import wiktiopeggynary.model.translation.TranslationMeaning;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntry {

    private String lemma;
    private Map<String, List<TranslationMeaning>> translations = new HashMap<>();

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void addTranslationMeaning(String language, TranslationMeaning translationMeaning) {
        if (!translations.containsKey(language)) {
            translations.put(language, new ArrayList<>());
        }
        translations.get(language).add(translationMeaning);
    }

    public Map<String, List<TranslationMeaning>> getTranslations() {
        return Collections.unmodifiableMap(translations);
    }

    @Override
    public String toString() {
        return "WiktionaryEntry{" +
                "lemma='" + lemma + '\'' +
                ", translations=" + translations +
                '}';
    }
}
