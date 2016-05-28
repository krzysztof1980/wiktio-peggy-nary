package wiktiopeggynary.parser;

import java.util.concurrent.ExecutorService;

/**
 * @author Krzysztof Witukiewicz
 */
public interface ParserTaskExecutorFactory {

	ExecutorService createExecutor();
}
