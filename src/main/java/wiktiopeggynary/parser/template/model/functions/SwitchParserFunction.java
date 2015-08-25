package wiktiopeggynary.parser.template.model.functions;

import wiktiopeggynary.parser.template.model.DisplayableAsText;
import wiktiopeggynary.parser.template.model.runtime.TemplateDefinitionParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author Krzysztof Witukiewicz
 */
public class SwitchParserFunction implements DisplayableAsText {

	private final DisplayableAsText comparisonString;
	private final Stack<SwitchTestCase> testCases = new Stack<>();

	public SwitchParserFunction(DisplayableAsText comparisonString) {
		if (comparisonString == null)
			throw new IllegalArgumentException("comparisonString must not be null");
		this.comparisonString = comparisonString;
	}

	public DisplayableAsText getComparisonString() {
		return comparisonString;
	}

	public List<SwitchTestCase> getTestCases() {
		return Collections.unmodifiableList(testCases);
	}

	public void addTestCase(SwitchTestCase testCase) {
		if (testCase == null)
			throw new IllegalArgumentException("testCase must not be null");
		if (testCases.isEmpty() || !testCases.peek().tryMerge(testCase)) {
			testCases.push(testCase);
		}
	}

	@Override
	public String asText(TemplateDefinitionParameter... parameters) {
		String comparisonStringText = comparisonString.asText(parameters);
		for (SwitchTestCase c : testCases) {
			if (c.tests.isEmpty() || c.tests.stream().anyMatch(t -> t.asText(parameters).trim().equals(comparisonStringText)))
				return c.result.asText(parameters);
		}
		return "";
	}

	public static class SwitchTestCase {

		private final List<DisplayableAsText> tests = new ArrayList<>();
		private DisplayableAsText result;

		public SwitchTestCase(DisplayableAsText test, DisplayableAsText result) {
			if (test == null)
				throw new IllegalArgumentException("test must not be null");
			tests.add(test);
			this.result = result;
		}

		public SwitchTestCase(DisplayableAsText defaultResult) {
			if (defaultResult == null)
				throw new IllegalArgumentException("defaultResult must not be null");
			result = defaultResult;
		}

		public List<DisplayableAsText> getTests() {
			return Collections.unmodifiableList(tests);
		}

		public DisplayableAsText getResult() {
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
