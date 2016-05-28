package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.model.markup.RichText;

import java.util.function.Function;

/**
 * @author Krzysztof Witukiewicz
 */
public enum ParserFunctionType {

    IF(IfParserFunction::new),
    IFEQ(IfeqParserFunction::new),
    IFEXIST(IfexistParserFunction::new);

    private final Function<RichText[], SimpleParserFunctionWithParameters> factoryMethod;

    ParserFunctionType(Function<RichText[], SimpleParserFunctionWithParameters> factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public SimpleParserFunctionWithParameters createParserFunction(RichText... parameters) {
        return factoryMethod.apply(parameters);
    }
}
