package wiktiopeggynary.parser.template.model;

import wiktiopeggynary.model.markup.Constant;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.visitor.RichTextEvaluator;
import wiktiopeggynary.parser.template.TemplateService;

import java.util.Collection;

/**
 * @author Krzysztof Witukiewicz
 */
public class TemplateDefinition implements Evaluable {

	private final RichText body;

	public TemplateDefinition(RichText body) {
		this.body = body;
	}

	public RichText getBody() {
		return body;
	}

	@Override
	public RichText evaluate(TemplateService templateService, Collection<Constant> constants) {
		RichTextEvaluator evaluator = new RichTextEvaluator(templateService, constants);
		evaluator.visit(body);
		return evaluator.getResult();
	}
}
