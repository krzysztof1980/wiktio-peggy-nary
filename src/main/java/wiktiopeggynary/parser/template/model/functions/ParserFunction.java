package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.parser.template.model.DisplayableAsText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class ParserFunction implements DisplayableAsText {

    private List<DisplayableAsText> parameters = new ArrayList<>();

    public void addParameter(DisplayableAsText parameter) {
	    if (parameter == null)
		    throw new IllegalArgumentException("parameter must not be null");
        parameters.add(parameter);
    }

    protected List<DisplayableAsText> getParameters() {
        return Collections.unmodifiableList(parameters);
    }
}
