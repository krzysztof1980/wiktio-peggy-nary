package wiktiopeggynary.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * @author Krzysztof Witukiewicz
 */
class WiktionaryEntrySerializer {

    private final ObjectMapper objectMapper = new ObjectMapper()

    WiktionaryEntrySerializer() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    String serializeWiktionaryEntry(WiktionaryEntry entry) {
        objectMapper.writeValueAsString(entry)
    }

    public <T extends WiktionaryEntry> T deserializeWiktionaryEntry(String entryJsonString, Class<T> valueType) {
        objectMapper.readValue(entryJsonString, valueType)
    }
}
