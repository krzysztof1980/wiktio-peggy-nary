package wiktiopeggynary.parser.template.model.functions;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.markup.Constant;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.visitor.RichTextEvaluator;
import wiktiopeggynary.parser.template.TemplateService;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class SimpleParserFunctionWithParameters extends ParserFunction {

	private List<RichText> parameters = new ArrayList<>();

	public SimpleParserFunctionWithParameters(RichText... parameters) {
		this.parameters.addAll(Arrays.asList(parameters));
	}

	@Override
	public RichText evaluate(TemplateService templateService, Collection<Constant> constants) {
		EvaluatedParameters evaluatedParameters = new EvaluatedParameters();
		parameters.forEach(p -> {
			RichTextEvaluator evaluator = new RichTextEvaluator(templateService, constants);
			p.accept(evaluator);
			evaluatedParameters.parameters.add(evaluator.getResult());
		});
		return doEvaluate(evaluatedParameters);
	}

	protected abstract RichText doEvaluate(EvaluatedParameters parameters);

	protected static class EvaluatedParameters {

		final List<RichText> parameters = new ArrayList<>();

		Optional<RichText> idx(int idx) {
			Validate.isTrue(idx >= 0);
			return idx < parameters.size() ? Optional.of(parameters.get(idx)) : Optional.empty();
		}
	}
}
