package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.model.markup.RichText;

/**
 * @author Krzysztof Witukiewicz
 */
public class IfeqParserFunction extends SimpleParserFunctionWithParameters {

	public IfeqParserFunction(RichText... parameters) {
		super(parameters);
	}

	@Override
	public RichText doEvaluate(EvaluatedParameters params) {
		return params.idx(0).get().equals(params.idx(1).get()) ? params.idx(2).get() : params.idx(3).get();
	}
}
