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
	private final long sequence;

	public WiktionaryEntryPageParseResult(Collection<WiktionaryEntry> wiktionaryEntries) {
		this(wiktionaryEntries, 0L);
	}

	public WiktionaryEntryPageParseResult(Collection<WiktionaryEntry> wiktionaryEntries, long sequence) {
		this.wiktionaryEntries.addAll(wiktionaryEntries);
		this.sequence = sequence;
	}

	public long getSequence() {
		return sequence;
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
