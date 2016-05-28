package wiktiopeggynary.parser.template.model;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.markup.AnonymousTemplateParameter;
import wiktiopeggynary.model.markup.Constant;
import wiktiopeggynary.model.markup.NamedTemplateParameter;
import wiktiopeggynary.model.markup.NumberedTemplateParameter;
import wiktiopeggynary.model.visitor.TemplateParameterVisitor;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public class TemplateDefinitionParameterListBuilder implements TemplateParameterVisitor {

	private List<AnonymousTemplateParameter> anonymousParameters = new ArrayList<>();
	private List<Constant> constants = new ArrayList<>();
	private Set<Integer> allocatedNumbers = new HashSet<>();

	@Override
	public void visit(AnonymousTemplateParameter param) {
		Validate.notNull(param);
		anonymousParameters.add(param);
	}

	@Override
	public void visit(NumberedTemplateParameter param) {
		Validate.notNull(param);
		constants.add(new Constant(param.getNumber().toString(), param.getValue()));
		allocatedNumbers.add(param.getNumber());
	}

	@Override
	public void visit(NamedTemplateParameter param) {
		Validate.notNull(param);
		constants.add(new Constant(param.getName(), param.getValue()));
	}

	public Collection<Constant> build() {
		Integer nextNumber = 1;
		for (AnonymousTemplateParameter param : anonymousParameters) {
			while (allocatedNumbers.contains(nextNumber))
				nextNumber++;
			constants.add(new Constant(String.valueOf(nextNumber++), param.getValue()));
		}

		return constants;
	}
}
