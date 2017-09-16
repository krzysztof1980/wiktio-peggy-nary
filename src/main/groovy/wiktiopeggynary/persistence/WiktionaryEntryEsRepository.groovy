package wiktiopeggynary.persistence

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pl.kwitukiewicz.wdb.elasticsearch.ElasticsearchIndexingRepository
import wiktiopeggynary.model.WiktionaryEntry

/**
 * @author Krzysztof Witukiewicz
 */
class WiktionaryEntryEsRepository extends ElasticsearchIndexingRepository {
    private static final Logger logger = LoggerFactory.getLogger(WiktionaryEntryEsRepository.class)

    private static final String WIKTIONARY_ENTRY_INDEX = "dewiktionary_entries"
    private static final String WIKTIONARY_ENTRY_TYPE = "entry"

    WiktionaryEntryEsRepository() {
        super(WIKTIONARY_ENTRY_INDEX, WIKTIONARY_ENTRY_TYPE)
    }

    void indexWiktionaryEntry(WiktionaryEntry entry) {
        indexObject(entry)
    }

    @Override
    protected Logger getLogger() {
        return logger
    }
}
