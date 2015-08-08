package wiktiopeggynary.model;

import wiktiopeggynary.model.translation.TranslationMeaning;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntry {

    private String lemma;
    private final List<Meaning> meanings = new ArrayList<>();
    private final Map<String, List<TranslationMeaning>> translations = new HashMap<>();

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void addMeaning(Meaning meaning) {
        if (meaning == null)
            throw new IllegalArgumentException("meaning must not be null");
        meanings.add(meaning);
    }

    public List<Meaning> getMeanings() {
        return Collections.unmodifiableList(meanings);
    }

    public void addTranslationMeaning(String language, TranslationMeaning translationMeaning) {
        if (language == null || language.isEmpty())
            throw new IllegalArgumentException("language must not be empty");
        if (translationMeaning == null)
            throw new IllegalArgumentException("translationMeaning must not be null");

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
