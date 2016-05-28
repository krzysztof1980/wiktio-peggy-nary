package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.model.markup.RichText;

/**
 * @author Krzysztof Witukiewicz
 */
public class IfexistParserFunction extends SimpleParserFunctionWithParameters {

	public IfexistParserFunction(RichText... parameters) {
		super(parameters);
	}

	@Override
	public RichText doEvaluate(EvaluatedParameters params) {
		// TODO: for now, let's assume the page that is checked for existence does not exist
		return params.idx(2).get();
	}
}