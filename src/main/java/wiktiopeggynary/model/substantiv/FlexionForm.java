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

    public List<String> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    @Override
    public String toString() {
        String value = format("'%s'", variants.stream().collect(Collectors.joining(", ")));
        return format("%s %s=%s", kasus, numerus, value);
    }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		FlexionForm that = (FlexionForm) o;
		
		if (kasus != that.kasus) return false;
		return numerus == that.numerus;
	}
	
	@Override
	public int hashCode() {
		int result = kasus.hashCode();
		result = 31 * result + numerus.hashCode();
		return result;
	}
}
