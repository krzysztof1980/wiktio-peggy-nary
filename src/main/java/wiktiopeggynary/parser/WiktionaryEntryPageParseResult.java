package wiktiopeggynary.parser;

import wiktiopeggynary.model.WiktionaryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntryPageParseResult {

	private final List<WiktionaryEntry> wiktionaryEntries = new ArrayList<>();

	public WiktionaryEntryPageParseResult(Collection<WiktionaryEntry> wiktionaryEntries) {
		this.wiktionaryEntries.addAll(wiktionaryEntries);
	}

	public List<WiktionaryEntry> getWiktionaryEntries() {
		return Collections.unmodifiableList(wiktionaryEntries);
	}
	
	@Override
	public String toString() {
		return "WiktionaryEntryPageParseResult{" +
				       "wiktionaryEntries=" + wiktionaryEntries +
				       '}';
	}
}
