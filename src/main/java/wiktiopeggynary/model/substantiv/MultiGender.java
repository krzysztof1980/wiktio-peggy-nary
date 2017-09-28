package wiktiopeggynary.model.substantiv;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ArrayUtils;
import wiktiopeggynary.model.markup.RichTextComponent;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class MultiGender implements RichTextComponent {

    private Gender[] genders;

    public MultiGender(@JsonProperty("genders") Gender... genders) {
        if (genders == null || genders.length == 0)
            throw new IllegalArgumentException("genders cannot be null or empty");
        this.genders = genders;
    }

    public MultiGender(String shortcuts) {
        if (shortcuts == null || shortcuts.isEmpty())
            throw new IllegalArgumentException("shortcuts cannot be null or empty");
        genders = shortcuts.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .map(Gender::fromShortcut)
                .collect(Collectors.toList())
                .toArray(new Gender[0]);
    }

    public Gender[] getGenders() {
        return Arrays.copyOf(genders, genders.length);
    }

    public boolean isSameAs(Gender gender) {
        return genders != null && genders.length == 1 && genders[0].equals(gender);
    }
	
	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public Optional<MultiGender> mergeWith(RichTextComponent component) {
		if (!(component instanceof MultiGender))
			return Optional.empty();
		MultiGender otherGender = (MultiGender) component;
		return Optional.of(new MultiGender(ArrayUtils.addAll(genders, otherGender.genders)));
	}
	
	@Override
	public boolean isEmpty() {
		return genders != null && genders.length > 0;
	}
    
    @Override
    public String toString() {
        return Arrays.stream(genders)
                .map(Gender::getArtikel)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiGender that = (MultiGender) o;

        return Arrays.equals(genders, that.genders);

    }

    @Override
    public int hashCode() {
        return genders != null ? Arrays.hashCode(genders) : 0;
    }
}
