package wiktiopeggynary.persistence;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.kwitukiewicz.wdb.elasticsearch.ElasticsearchIndexingRepository;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.WiktionaryEntrySerializer;

import java.util.UUID;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntryEsRepository extends ElasticsearchIndexingRepository {
    private static final Logger logger = LoggerFactory.getLogger(WiktionaryEntryEsRepository.class);

    private static final String WIKTIONARY_ENTRY_INDEX = "dewiktionary_entries";
    private static final String WIKTIONARY_ENTRY_TYPE = "entry";

    private final WiktionaryEntrySerializer entrySerializer;

    public WiktionaryEntryEsRepository(WiktionaryEntrySerializer entrySerializer) {
        super(WIKTIONARY_ENTRY_INDEX, WIKTIONARY_ENTRY_TYPE);
        this.entrySerializer = entrySerializer;
    }

    public void indexWiktionaryEntry(WiktionaryEntry entry) {
        String id = UUID.randomUUID().toString();
        String entryJsonString = entrySerializer.serializeWiktionaryEntry(entry);
        indexDocument(id, entryJsonString);
    }

    @Override
    public void prepareIndexForRebuild() {
        super.prepareIndexForRebuild();
        String jsonString = """
                {
                    "dynamic": false,
                    "properties": {
                        "lemma": {
                            "type": "text",
                            "fields": {
                                "keyword": {
                                    "type": "keyword"
                                }
                            }
                        }
                    }
                }
                """;
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON);
        Request request = new Request("PUT", index + "/_mapping");
        request.setEntity(entity);
        try {
            lowLevelClient.performRequest(request);
        } catch (Exception e) {
            throw new RuntimeException("Error setting mappings in " + index, e);
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
