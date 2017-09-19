package wiktiopeggynary.model.substantiv;

import wiktiopeggynary.model.Kasus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author Krzysztof Witukiewicz
 */
public class Flexion {

    private final Kasus kasus;
    private final List<String> variants = new ArrayList<>();

    public Flexion(Kasus kasus) {
        this.kasus = kasus;
    }

    public Kasus getKasus() {
        return kasus;
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
        return format("%s=%s", kasus, value);
    }
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		Flexion flexion = (Flexion) o;
		
		return kasus == flexion.kasus;
	}
	
	@Override
	public int hashCode() {
		return kasus.hashCode();
	}
}
