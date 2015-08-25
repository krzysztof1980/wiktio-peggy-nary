package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

/**
 * @author Krzysztof Witukiewicz
 */
public interface DisplayableAsText {

    String asText(TemplateDefinitionParameter... parameters);
}
