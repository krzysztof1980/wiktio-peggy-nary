package wiktiopeggynary.parser.template.model;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.markup.Constant;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;
import wiktiopeggynary.parser.template.TemplateService;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class TemplateParameterApplication implements EvaluableRichTextComponent {

	private final String identifier;

	private RichText defaultValue;

	private TemplateParameterApplication(String identifier, RichText defaultValue) {
		Validate.notBlank(identifier);
		this.identifier = identifier;
		this.defaultValue = defaultValue;
	}

	public String getIdentifier() {
		return identifier;
	}

	public RichText getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(RichText defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public RichText evaluate(TemplateService templateService, Collection<Constant> constants) {
		Optional<Constant> constant = constants.stream().filter(
				c -> c.getName().equals(identifier)).findFirst();
		if (constant.isPresent())
			return constant.get().getValue();
		else if (defaultValue != null)
			return defaultValue;
		else
			return new RichText(toString());
	}

	@Override
	public String toString() {
		if (defaultValue != null)
			return String.format("{{{%s|%s}}}", identifier, defaultValue);
		else
			return String.format("{{{%s}}}", identifier);
	}

	public static class Builder {

		private String identifier;
		private RichText defaultValue;

		public Builder withIdentifier(String identifier) {
			this.identifier = identifier;
			return this;
		}

		public Builder withDefaultValue(RichText defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public TemplateParameterApplication build() {
			return new TemplateParameterApplication(identifier, defaultValue);
		}
	}
}
