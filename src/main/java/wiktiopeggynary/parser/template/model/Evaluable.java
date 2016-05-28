package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.model.markup.Constant;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.parser.template.TemplateService;

import java.util.Collection;

/**
 * @author Krzysztof Witukiewicz
 */
public interface Evaluable {

	/**
	 * Evaluate the object based on given constants. Must not change the object under evaluation. Thread-safe.
	 */
	RichText evaluate(TemplateService templateService, Collection<Constant> constants);
}
