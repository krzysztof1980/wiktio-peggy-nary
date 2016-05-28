package wiktiopeggynary.parser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Krzysztof Witukiewicz
 */
public class ConcurrentParserTaskExecutorFactory implements ParserTaskExecutorFactory {

	private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

	@Override
	public ExecutorService createExecutor() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE, 0L,
		                                                     TimeUnit.MILLISECONDS, new SynchronousQueue<>());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}
}
