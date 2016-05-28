package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.markup.RichTextComponent;

import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public class IfParserFunction extends SimpleParserFunctionWithParameters {

	public IfParserFunction(RichText... parameters) {
		super(parameters);
	}

	@Override
	public Optional<IfeqParserFunction> mergeWith(RichTextComponent component) {
		return Optional.empty();
	}

	@Override
	protected RichText doEvaluate(EvaluatedParameters params) {
		return !params.idx(0).get().isEmpty() ? params.idx(1).get() : params.idx(2).orElse(RichText.empty());
	}
}
