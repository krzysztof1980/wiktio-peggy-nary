package wiktiopeggynary.parser;

import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.markup.Template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Krzysztof Witukiewicz
 */
public class WiktionaryEntryPageParseResult {

	private final Collection<WiktionaryEntry> wiktionaryEntries = new ArrayList<>();
	private final Collection<Template> templates = new ArrayList<>();

	public WiktionaryEntryPageParseResult(Collection<WiktionaryEntry> wiktionaryEntries, Collection<Template> templates) {
		this.wiktionaryEntries.addAll(wiktionaryEntries);
		this.templates.addAll(templates);
	}

	public Collection<WiktionaryEntry> getWiktionaryEntries() {
		return Collections.unmodifiableCollection(wiktionaryEntries);
	}

	public Collection<Template> getTemplates() {
		return Collections.unmodifiableCollection(templates);
	}
}
