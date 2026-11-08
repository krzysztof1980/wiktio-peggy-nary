package wiktiopeggynary.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UncheckedIOException;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntrySerializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WiktionaryEntrySerializer() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public String serializeWiktionaryEntry(WiktionaryEntry entry) {
        try {
            return objectMapper.writeValueAsString(entry);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T extends WiktionaryEntry> T deserializeWiktionaryEntry(String entryJsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(entryJsonString, valueType);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
