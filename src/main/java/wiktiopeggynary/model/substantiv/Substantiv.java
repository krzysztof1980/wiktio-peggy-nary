package wiktiopeggynary.model.substantiv;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.Numerus;
import wiktiopeggynary.model.WiktionaryEntry;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public class Substantiv extends WiktionaryEntry {
	
	public static final String ATTR_ADJ_DEKLINATION = "AdjDeklination";
	private final Collection<String> attributes = new ArrayList<>();
	private final Map<Numerus, List<FlexionForm>> flexionForms = new HashMap<>();
	private MultiGender gender;
	
	public Substantiv() {
		flexionForms.put(Numerus.Singular, new ArrayList<>());
		flexionForms.put(Numerus.Plural, new ArrayList<>());
	}
	
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
		Validate.notBlank(attribute, "Substantiv '%s': attribute cannot be empty", getLemma());
		attributes.add(attribute);
	}
	
	public Map<Numerus, List<FlexionForm>> getFlexionForms() {
		return flexionForms;
	}
	
	public List<FlexionForm> getFlexionForms(Numerus numerus) {
		return Collections.unmodifiableList(flexionForms.get(numerus));
	}
	
	public void addFlexionForm(Numerus numerus, FlexionForm flexionForm) {
		Validate.notNull(numerus);
		Validate.notNull(flexionForm);
		flexionForms.get(numerus).add(flexionForm);
	}
	
	@Override
	public String toString() {
		return String.format("Substantiv{%s %s: %s flexion form(s), %s meaning(s), %s translation(s)}",
		                     getLemma(), gender,
		                     flexionForms.get(Numerus.Singular).size() + flexionForms.get(Numerus.Plural).size(),
		                     getMeanings().size(),
		                     getTranslations().size());
	}
}
