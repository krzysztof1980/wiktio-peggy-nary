package wiktiopeggynary.parser.template.model.functions;

import java.util.function.Supplier;

/**
 * @author Krzysztof Witukiewicz
 */
public enum ParserFunctionType {

    IF(IfParserFunction::new),
    IFEQ(IfeqParserFunction::new),
    IFEXIST(IfexistParserFunction::new);

    private final Supplier<ParserFunction> supplier;

    private ParserFunctionType(Supplier<ParserFunction> supplier) {
        this.supplier = supplier;
    }

    public ParserFunction createParserFunction() {
        return supplier.get();
    }
}
