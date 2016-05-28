package wiktiopeggynary.parser;

import wiktiopeggynary.model.WiktionaryEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntryPageParseResult {

	private final Collection<WiktionaryEntry> wiktionaryEntries = new ArrayList<>();

	public WiktionaryEntryPageParseResult(Collection<WiktionaryEntry> wiktionaryEntries) {
		this.wiktionaryEntries.addAll(wiktionaryEntries);
	}

	public Collection<WiktionaryEntry> getWiktionaryEntries() {
		return Collections.unmodifiableCollection(wiktionaryEntries);
	}
}
