package wiktiopeggynary.model.visitor;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.markup.*;
import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.parser.template.model.TemplateDefinition;
import wiktiopeggynary.parser.template.model.TemplateDefinitionParameterListBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public class TemplateEvaluator {
	
	private final TemplateService templateService;
	
	public TemplateEvaluator(TemplateService templateService) {
		this.templateService = templateService;
	}
	
	public RichText evaluate(Template template, Collection<Constant> templateContext) {
		TemplateDefinition templateDefinition = templateService.parseTemplateDefinitionPageForTemplate(
				template.getName());
		Validate.notNull(templateDefinition, String.format("Missing definition for template '%s'", template.getName()));
		List<TemplateParameter> evaluatedParameters = evaluateParameters(template.getParameters(), templateContext);
		Collection<Constant> templateDefinitionContext = getParametersForTemplateDefinition(evaluatedParameters);
		return templateDefinition.evaluate(templateService, templateDefinitionContext);
	}
	
	private Collection<Constant> getParametersForTemplateDefinition(List<TemplateParameter> resolvedParameters) {
		TemplateDefinitionParameterListBuilder builder = new TemplateDefinitionParameterListBuilder();
		resolvedParameters.forEach(p -> p.accept(builder));
		return builder.build();
	}
	
	private List<TemplateParameter> evaluateParameters(List<TemplateParameter> parameters,
	                                                   Collection<Constant> constants) {
		List<TemplateParameter> evaluatedParameters = new ArrayList<>(parameters.size());
		parameters.forEach(p -> {
			final RichTextEvaluator evaluator = new RichTextEvaluator(templateService, constants);
			p.getValue().accept(evaluator);
			addParameterWithEvaluatedValue(p, evaluator.getResult(), evaluatedParameters);
		});
		return evaluatedParameters;
	}
	
	private void addParameterWithEvaluatedValue(TemplateParameter p, RichText evaluatedValue,
	                                            List<TemplateParameter> evaluatedParameters) {
		
		p.accept(new TemplateParameterVisitor() {
			@Override
			public void visit(AnonymousTemplateParameter param) {
				evaluatedParameters.add(new AnonymousTemplateParameter(evaluatedValue));
			}
			
			@Override
			public void visit(NumberedTemplateParameter param) {
				evaluatedParameters.add(new NumberedTemplateParameter(param.getNumber(), evaluatedValue));
			}
			
			@Override
			public void visit(NamedTemplateParameter param) {
				evaluatedParameters.add(new NamedTemplateParameter(param.getName(), evaluatedValue));
			}
		});
	}
}
