package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public class IfParserFunction extends ParserFunction {

	@Override
	public String asText(TemplateDefinitionParameter... parameters) {
		String testString = getParameters().get(0).asText(parameters);
		String conditionTrue = getParameters().get(1).asText(parameters);
		String conditionFalse = getParameters().size() == 3 ? getParameters().get(2).asText(parameters) : "";
		return !testString.isEmpty() ? conditionTrue : conditionFalse;
	}
}
