package wiktiopeggynary.persistence

import org.apache.http.HttpEntity
import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.kwitukiewicz.wdb.elasticsearch.ElasticsearchIndexingRepository
import wiktiopeggynary.model.WiktionaryEntry
import wiktiopeggynary.model.WiktionaryEntrySerializer

/**
 * @author Krzysztof Witukiewicz
 */
class WiktionaryEntryEsRepository extends ElasticsearchIndexingRepository {
    private static final Logger logger = LoggerFactory.getLogger(WiktionaryEntryEsRepository.class)

    private static final String WIKTIONARY_ENTRY_INDEX = "dewiktionary_entries"
    private static final String WIKTIONARY_ENTRY_TYPE = "entry"

    private final WiktionaryEntrySerializer entrySerializer

    WiktionaryEntryEsRepository(WiktionaryEntrySerializer entrySerializer) {
        super(WIKTIONARY_ENTRY_INDEX, WIKTIONARY_ENTRY_TYPE)
        this.entrySerializer = entrySerializer
    }

    void indexWiktionaryEntry(WiktionaryEntry entry) {
        def id = UUID.randomUUID()
        def entryJsonString = entrySerializer.serializeWiktionaryEntry(entry)
        indexDocument(id.toString(), entryJsonString)
    }

    @Override
    void prepareIndexForRebuild() {
        super.prepareIndexForRebuild()
        def jsonString = """\
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
"""
        HttpEntity entity = new NStringEntity(jsonString, ContentType.APPLICATION_JSON)
        try {
            lowLevelClient.performRequest("PUT", "${index}/_mapping/${type}", Collections.emptyMap(), entity)
        } catch (Exception e) {
            throw new RuntimeException("Error setting mappings in ${index}", e)
        }
    }

    @Override
    protected Logger getLogger() {
        return logger
    }
}
