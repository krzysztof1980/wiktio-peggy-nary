package wiktiopeggynary.model.substantiv;

import wiktiopeggynary.model.WiktionaryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Krzysztof Witukiewicz
 */
public class Substantiv extends WiktionaryEntry {

    public static final String ATTR_ADJ_DEKLINATION = "AdjDeklination";

    private MultiGender gender;

    private Collection<String> attributes = new ArrayList<>();

    private FlexionTable flexionTable = new FlexionTable();

    public MultiGender getGender() {
        return gender;
    }

    public void setGender(MultiGender gender) {
        this.gender = gender;
    }

    public Collection<String> getAttributes() {
        return Collections.unmodifiableCollection(attributes);
    }

    public void addAttribute(String attribute) {
        if (attribute == null || attribute.isEmpty())
            throw new IllegalArgumentException(String.format("Substantiv '%s': attribute cannot be empty", getLemma()));
        attributes.add(attribute);
    }

    public FlexionTable getFlexionTable() {
        return flexionTable;
    }

    @Override
    public String toString() {
        return String.format("Substantiv{%s %s: %s flexion forms, %s translation(s)}",
                getLemma(), gender, flexionTable.getFlexionForms().size(), getTranslations().size());
    }
}
