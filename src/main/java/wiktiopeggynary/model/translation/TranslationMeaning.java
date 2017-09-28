package wiktiopeggynary.model.translation;

import com.fasterxml.jackson.annotation.JsonProperty;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.substantiv.MultiGender;
import wiktiopeggynary.model.visitor.DescendingRichTextComponentVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public class TranslationMeaning {
	
	private List<ItemNumber> meaningNumbers;
	private List<Translation> translations = new ArrayList<>();
	private RichText text;
	
	public TranslationMeaning(@JsonProperty("meaningNumbers") List<ItemNumber> meaningNumbers,
	                          @JsonProperty("text") RichText text) {
		this.meaningNumbers = new ArrayList<>(meaningNumbers);
		this.text = text;
		text.accept(new DescendingRichTextComponentVisitor() {
			@Override
			public void visit(Translation translation) {
				translations.add(translation);
			}
			
			@Override
			public void visit(MultiGender gender) {
				if (!translations.isEmpty()) {
					Translation lastTranslation = translations.get(translations.size() - 1);
					if (lastTranslation.getGender() == null)
						lastTranslation.setGender(gender);
					else
						lastTranslation.setGender(lastTranslation.getGender().mergeWith(gender).get());
				}
			}
		});
	}
	
	public List<ItemNumber> getMeaningNumbers() {
		return Collections.unmodifiableList(meaningNumbers);
	}
	
	public List<Translation> getTranslations() {
		return Collections.unmodifiableList(translations);
	}
	
	public RichText getText() {
		return text;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TranslationMeaning{");
		sb.append("meaningNumbers='").append(meaningNumbers).append('\'');
		sb.append(", translations=").append(translations);
		sb.append('}');
		return sb.toString();
	}
}