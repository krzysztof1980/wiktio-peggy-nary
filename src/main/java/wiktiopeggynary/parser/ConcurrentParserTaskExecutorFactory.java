package wiktiopeggynary.parser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Krzysztof Witukiewicz
 */
public class ConcurrentParserTaskExecutorFactory implements ParserTaskExecutorFactory {

	private final int poolSize;

	public ConcurrentParserTaskExecutorFactory() {
		this(Runtime.getRuntime().availableProcessors());
	}

	public ConcurrentParserTaskExecutorFactory(int poolSize) {
		if (poolSize < 1)
			throw new IllegalArgumentException("poolSize must be at least 1");
		this.poolSize = poolSize;
	}

	@Override
	public ExecutorService createExecutor() {
		// Zero-capacity SynchronousQueue: a task is never buffered — it is handed to an idle worker
		// or, when all workers are busy, run by the submitting (XML-reader) thread.
		// poolSize == 1 gives sequential mode with bounded memory.
		ThreadPoolExecutor executor = new ThreadPoolExecutor(poolSize, poolSize, 0L,
		                                                     TimeUnit.MILLISECONDS, new SynchronousQueue<>());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}
}
