package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public class IfeqParserFunction extends ParserFunction {

    @Override
    public String asText(TemplateDefinitionParameter... parameters) {
        String s1 = getParameters().get(0).asText(parameters);
        String s2 = getParameters().get(1).asText(parameters);
        String valueIfIdentical = getParameters().get(2).asText(parameters);
        String valueIfDifferent = getParameters().get(3).asText(parameters);
        return s1.equals(s2) ? valueIfIdentical : valueIfDifferent;
    }
}
