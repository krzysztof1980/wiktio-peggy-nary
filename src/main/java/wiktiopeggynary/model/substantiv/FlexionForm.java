package wiktiopeggynary.model.substantiv;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class FlexionForm {

	private final Gender gender;
    private final List<Flexion> flexions = new ArrayList<>();
	
	public FlexionForm(Gender gender) {
		Validate.notNull(gender);
		this.gender = gender;
	}
	
	public Gender getGender() {
		return gender;
	}
	
	public void addFlexion(Flexion flexionForm) {
        flexions.add(flexionForm);
    }

    public List<Flexion> getFlexions() {
        return Collections.unmodifiableList(flexions);
    }
	
	@Override
	public String toString() {
		return "FlexionForm{" +
				       "gender=" + gender +
				       ", flexions=" + flexions.stream().map(Flexion::toString).collect(Collectors.joining("\n")) +
				       '}';
	}
}
