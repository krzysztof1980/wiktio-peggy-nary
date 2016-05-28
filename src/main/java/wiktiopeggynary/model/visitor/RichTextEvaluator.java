package wiktiopeggynary.model.visitor;

import wiktiopeggynary.model.markup.*;
import wiktiopeggynary.parser.template.TemplateService;
import wiktiopeggynary.parser.template.model.TemplateParameterApplication;
import wiktiopeggynary.parser.template.model.functions.ParserFunction;

import java.util.Collection;

/**
 * @author Krzysztof Witukiewicz
 */
public class RichTextEvaluator implements RichTextComponentVisitor {

	private final TemplateService templateService;
	private final Collection<Constant> constants;
	private RichText evaluationResult = new RichText();

	public RichTextEvaluator(TemplateService templateService, Collection<Constant> constants) {
		this.templateService = templateService;
		this.constants = constants;
	}

	@Override
	public void visit(PlainText plainText) {
		evaluationResult.addComponent(plainText);
	}

	@Override
	public void visit(RichText richText) {
		richText.getComponents()
		        .stream()
		        .map(c -> {
			        RichTextEvaluator evaluator = new RichTextEvaluator(templateService, constants);
			        c.accept(evaluator);
			        return evaluator.evaluationResult;
		        })
		        .forEach(evaluationResult::addComponent);
	}

	@Override
	public void visit(CursiveBlock cursiveBlock) {
		RichTextEvaluator bodyEvaluator = new RichTextEvaluator(templateService, constants);
		bodyEvaluator.visit(cursiveBlock.getBody());
		evaluationResult.addComponent(new CursiveBlock(bodyEvaluator.evaluationResult));
	}

	@Override
	public void visit(InternalLink internalLink) {
		evaluationResult.addComponent(internalLink);
	}

	@Override
	public void visit(Template template) {
		evaluationResult.addComponent(template.evaluate(templateService, constants));
	}

	@Override
	public void visit(ParserFunction function) {
		evaluationResult.addComponent(function.evaluate(templateService, constants));
	}

	@Override
	public void visit(TemplateParameterApplication parameterApplication) {
		evaluationResult.addComponent(parameterApplication.evaluate(templateService, constants));
	}

	public RichText getResult() {
		return evaluationResult;
	}
}
