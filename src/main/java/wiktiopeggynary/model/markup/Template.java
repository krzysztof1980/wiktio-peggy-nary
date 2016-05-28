package wiktiopeggynary.model.markup;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.visitor.RichTextComponentVisitor;
import wiktiopeggynary.model.visitor.RichTextEvaluator;
import wiktiopeggynary.model.visitor.TemplateParameterVisitor;
import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.parser.template.model.EvaluableRichTextComponent;
import wiktiopeggynary.parser.template.model.TemplateDefinition;
import wiktiopeggynary.parser.template.model.TemplateDefinitionParameterListBuilder;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public class Template implements EvaluableRichTextComponent {

	private final String name;
	private final List<TemplateParameter> parameters;

	private Template(String name, TemplateParameter... parameters) {
		Validate.notNull(name);
		this.name = name;
		this.parameters = Arrays.asList(parameters);
	}

	private static List<TemplateParameter> resolveParameters(TemplateService templateService,
	                                                         List<TemplateParameter> parameters,
	                                                         Collection<Constant> constants) {
		List<TemplateParameter> resolvedParameters = new ArrayList<>(parameters.size());
		parameters.forEach(p -> {
			final RichTextEvaluator evaluator = new RichTextEvaluator(templateService, constants);
			p.getValue().accept(evaluator);
			p.accept(new TemplateParameterVisitor() {
				@Override
				public void visit(AnonymousTemplateParameter param) {
					resolvedParameters.add(new AnonymousTemplateParameter(evaluator.getResult()));
				}

				@Override
				public void visit(NumberedTemplateParameter param) {
					resolvedParameters.add(new NumberedTemplateParameter(param.getNumber(), evaluator.getResult()));
				}

				@Override
				public void visit(NamedTemplateParameter param) {
					resolvedParameters.add(new NamedTemplateParameter(param.getName(), evaluator.getResult()));
				}
			});
		});
		return resolvedParameters;
	}

	private static Collection<Constant> getParametersForTemplateDefinition(List<TemplateParameter> resolvedParameters) {
		TemplateDefinitionParameterListBuilder builder = new TemplateDefinitionParameterListBuilder();
		resolvedParameters.forEach(p -> p.accept(builder));
		return builder.build();
	}

	public String getName() {
		return name;
	}

	public List<TemplateParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	@Override
	public RichText evaluate(TemplateService templateService, Collection<Constant> constants) {
		TemplateDefinition templateDefinition = templateService.parseTemplateDefinitionPageForTemplate(name);
		Validate.notNull(templateDefinition, String.format("Missing definition for template '%s'", name));
		return templateDefinition.evaluate(templateService, getParametersForTemplateDefinition(
				resolveParameters(templateService, getParameters(), constants)));
	}

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Optional<Template> mergeWith(RichTextComponent component) {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return "Template{" +
				       "name='" + name + '\'' +
				       ", parameters=" + parameters +
				       '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Template template = (Template) o;

		if (!name.equals(template.name)) return false;
		return parameters.equals(template.parameters);

	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + parameters.hashCode();
		return result;
	}

	public static final class Builder {

		private String name;
		private List<TemplateParameter> parameters = new ArrayList<>();

		public Builder withName(String name) {
			this.name = name;
			return this;
		}

		public Builder withParameter(TemplateParameter parameter) {
			parameters.add(parameter);
			return this;
		}

		public Builder withNamedParameter(String name, RichText value) {
			parameters.add(new NamedTemplateParameter(name, value));
			return this;
		}

		public Builder withNumberedParameter(Integer number, RichText value) {
			parameters.add(new NumberedTemplateParameter(number, value));
			return this;
		}

		public Builder withAnonymousParameter(RichText value) {
			parameters.add(new AnonymousTemplateParameter(value));
			return this;
		}

		public Template build() {
			return new Template(name, parameters.toArray(new TemplateParameter[0]));
		}
	}
}
