package wiktiopeggynary.parser.template.model.functions;

import org.apache.commons.lang3.Validate;
import wiktiopeggynary.model.markup.Constant;
import wiktiopeggynary.model.markup.RichText;
import wiktiopeggynary.model.visitor.RichTextEvaluator;
import wiktiopeggynary.parser.template.TemplateService;

import java.util.*;

/**
 * @author Krzysztof Witukiewicz
 */
public class SwitchParserFunction extends ParserFunction {

	private final RichText comparisonString;
	private final Stack<SwitchTestCase> testCases = new Stack<>();

	public SwitchParserFunction(RichText comparisonString) {
		Validate.notNull(comparisonString);
		this.comparisonString = comparisonString;
	}

	public RichText getComparisonString() {
		return comparisonString;
	}

	public List<SwitchTestCase> getTestCases() {
		return Collections.unmodifiableList(testCases);
	}

	public void addTestCase(SwitchTestCase testCase) {
		Validate.notNull(testCase);
		if (testCases.isEmpty() || !testCases.peek().tryMerge(testCase)) {
			testCases.push(testCase);
		}
	}

	@Override
	public RichText evaluate(TemplateService templateService, Collection<Constant> constants) {
		RichTextEvaluator comparisonEvaluator = new RichTextEvaluator(templateService, constants);
		comparisonEvaluator.visit(comparisonString);
		RichText evaluatedComparison = comparisonEvaluator.getResult();
		for (SwitchTestCase c : testCases) {
			if (c.tests.isEmpty() || c.tests.stream().anyMatch(t -> {
				RichTextEvaluator testEvaluator = new RichTextEvaluator(templateService, constants);
				testEvaluator.visit(t);
				return testEvaluator.getResult().equals(evaluatedComparison);
			})) {
				RichTextEvaluator resultEvaluator = new RichTextEvaluator(templateService, constants);
				resultEvaluator.visit(c.getResult());
				return resultEvaluator.getResult();
			}
		}
		return RichText.empty();
	}

	public static class SwitchTestCase {

		private final List<RichText> tests = new ArrayList<>();
		private RichText result;

		public SwitchTestCase(RichText test, RichText result) {
			Validate.notNull(test);
			tests.add(test);
			this.result = result;
		}

		public SwitchTestCase(RichText defaultResult) {
			Validate.notNull(defaultResult);
			result = defaultResult;
		}

		public List<RichText> getTests() {
			return Collections.unmodifiableList(tests);
		}

		public RichText getResult() {
			return result;
		}

		private boolean tryMerge(SwitchTestCase testCase) {
			if (result != null)
				return false;
			tests.addAll(testCase.tests);
			result = testCase.result;
			return true;
		}
	}
}
