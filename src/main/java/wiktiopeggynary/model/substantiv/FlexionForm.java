package wiktiopeggynary.model.substantiv;

import wiktiopeggynary.model.Kasus;
import wiktiopeggynary.model.Numerus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author Krzysztof Witukiewicz
 */
public class FlexionForm {

    private final Kasus kasus;
    private final Numerus numerus;
    private final List<String> variants = new ArrayList<>();
    private String unparsedForm;

    public FlexionForm(Kasus kasus, Numerus numerus) {
        this.kasus = kasus;
        this.numerus = numerus;
    }

    public Kasus getKasus() {
        return kasus;
    }

    public Numerus getNumerus() {
        return numerus;
    }

    public void addVariant(String variant) {
        variants.add(variant);
    }

    public String getUnparsedForm() {
        return unparsedForm;
    }

    public void setUnparsedForm(String unparsedForm) {
        this.unparsedForm = unparsedForm;
    }

    public Iterable<String> getVariants() {
        return Collections.unmodifiableCollection(variants);
    }

    public boolean isUnparsed() {
        return getUnparsedForm() != null;
    }

    @Override
    public String toString() {
        String value = isUnparsed()
                ? format("'%s' (unparsed)", getUnparsedForm())
                : format("'%s'", variants.stream().collect(Collectors.joining(", ")));
        return format("%s %s=%s", kasus, numerus, value);
    }
}
