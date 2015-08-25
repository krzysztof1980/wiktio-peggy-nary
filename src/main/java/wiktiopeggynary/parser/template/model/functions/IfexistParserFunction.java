package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public class IfexistParserFunction extends ParserFunction {

	@Override
	public String asText(TemplateDefinitionParameter... parameters) {
		// let's assume the page that is checked for existence does not exist
		return getParameters().get(2).asText(parameters);
	}
}
