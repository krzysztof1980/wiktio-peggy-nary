package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.model.visitor.RichTextComponentVisitor;
import wiktiopeggynary.parser.template.model.EvaluableRichTextComponent;

/**
 * @author Krzysztof Witukiewicz
 */
public abstract class ParserFunction implements EvaluableRichTextComponent {

	@Override
	public void accept(RichTextComponentVisitor visitor) {
		visitor.visit(this);
	}
}
