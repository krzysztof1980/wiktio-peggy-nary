package wiktiopeggynary.parser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Krzysztof Witukiewicz
 */
public class SequentialParserTaskExecutorFactory implements ParserTaskExecutorFactory {

	@Override
	public ExecutorService createExecutor() {
		return Executors.newSingleThreadExecutor();
	}
}
