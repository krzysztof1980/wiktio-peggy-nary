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

    /**
     * Cross-repo contract with the derdieapp exporter: the document {@code _id} format is
     * {@code %012d-%02d} ({@code pageSequence-entryIndex}). Because both components are
     * fixed-width and zero-padded, lexicographic order equals numeric order, so {@code _id}
     * ascending reconstructs Wiktionary dump page order — this is the ordering contract the
     * derdieapp exporter consumes.
     *
     * <p>Guarantees/ceilings: the entry-index component stays lexicographically ordered only
     * for fewer than 100 entries per page (the corpus maximum is single digits); the page
     * sequence counts only parsed pages (gaps are allowed) and is assigned monotonically on
     * the dump-stream thread.
     *
     * <p>Consumers must sort {@code _id} client-side: Elasticsearch 8.x disallows fielddata
     * access on {@code _id}, so a server-side sort on {@code _id} fails.
     */
    public static String documentId(long pageSequence, int entryIndex) {
        return String.format("%012d-%02d", pageSequence, entryIndex);
    }

    public void indexWiktionaryEntry(long pageSequence, int entryIndex, WiktionaryEntry entry) {
        String entryJsonString = entrySerializer.serializeWiktionaryEntry(entry);
        indexDocument(documentId(pageSequence, entryIndex), entryJsonString);
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
