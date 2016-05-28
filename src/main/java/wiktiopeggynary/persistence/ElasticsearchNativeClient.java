package wiktiopeggynary.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.parser.template.model.TemplateDefinition;

import java.io.Closeable;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Krzysztof Witukiewicz
 */
public class ElasticsearchNativeClient implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(ElasticsearchNativeClient.class);

	private static final String WIKTIONARY_ENTRY_INDEX = "dewiktionary_entries";
	private static final String WIKTIONARY_ENTRY_TYPE = "entry";

	private static final String TEMPLATE_DEF_INDEX = "dewiktionary_templates";
	private static final String TEMPLATE_DEF_TYPE = "template";

	private final Client client;
	private final BulkProcessor bulkProcessor;
	
	public ElasticsearchNativeClient() {
		client = TransportClient.builder().build()
		                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getLoopbackAddress(),
		                                                                            9300));
		BulkProcessor.Listener bulkProcessorListener = new BulkProcessor.Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				logger.debug("Starting bulk execution #{} with {} actions", executionId, request.numberOfActions());
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				if (response.hasFailures()) {
					logger.warn(response.buildFailureMessage());
				} else {
					logger.debug("Finished bulk execution #{}", executionId);
				}
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				logger.error("Exception was thrown during bulk execution " + executionId, failure);
			}
		};
		bulkProcessor = BulkProcessor.builder(client, bulkProcessorListener)
		                             .setBulkSize(new ByteSizeValue(15, ByteSizeUnit.MB))
		                             .build();
	}

	public void indexWiktionaryEntry(WiktionaryEntry entry) {
		indexObject(entry, WIKTIONARY_ENTRY_INDEX, WIKTIONARY_ENTRY_TYPE);
	}

	public void indexTemplateDefinition(TemplateDefinition templateDefinition) {
		indexObject(templateDefinition, TEMPLATE_DEF_INDEX, TEMPLATE_DEF_TYPE);
	}

	private void indexObject(Object obj, String index, String type) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			bulkProcessor.add(new IndexRequest(index, type, UUID.randomUUID().toString())
					                  .opType(IndexRequest.OpType.CREATE)
					                  .source(objectMapper.writeValueAsBytes(obj)));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteWiktionaryEntryIndexIfExists() {
		deleteIndexIfExists(WIKTIONARY_ENTRY_INDEX);
	}

	public void deleteTemplateDefinitionIndexIfExists() {
		deleteIndexIfExists(TEMPLATE_DEF_INDEX);
	}

	private void deleteIndexIfExists(String index) {
		if (client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet().isExists()) {
			logger.info("Index '{}' exists, deleting...", index);
			DeleteIndexResponse delete = client.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
			Validate.isTrue(delete.isAcknowledged(), "Index could not be deleted");
		}
	}

	@Override
	public void close() {
		logger.info("Cleaning up after indexing...");
		try {
			if (bulkProcessor.awaitClose(5, TimeUnit.MINUTES)) {
				logger.info("All bulk requests finished.");
			} else {
				logger.warn("There were unfinished bulk requests, consider increasing ttl.");
			}
			client.admin().indices().flush(new FlushRequest(WIKTIONARY_ENTRY_INDEX, TEMPLATE_DEF_INDEX)).actionGet();
		} catch (InterruptedException e) {
			logger.error("Exception while waiting for bulk operation to finish", e);
		} finally {
			client.close();
			logger.info("Indexing was finished!");
		}
	}
}
