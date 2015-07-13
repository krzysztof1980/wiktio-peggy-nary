package wiktiopeggynary.parser;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryDumpParser;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageDocument;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageParser;
import wiktiopeggynary.parser.mouse.SourceString;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Krzysztof Witukiewicz
 */
public final class ParserService {

    private static final ParserService instance = new ParserService();

    private ParserService() {
    }

    public static ParserService getInstance() {
        return instance;
    }

    public void parseWiktionaryDump(Path wiktionaryDumpPath, Consumer<WiktionaryEntry> consumer, Runnable cleanup) {
        WiktionaryDumpParser dumpParser = new WiktionaryDumpParser(
                new WiktionaryPageParser(p -> processWiktionaryPage(p, consumer), cleanup));
        dumpParser.parse(wiktionaryDumpPath.toFile());
    }

    public Collection<WiktionaryEntry> parseWiktionaryPage(String page) {
        WiktionaryParser parser = new WiktionaryParser();
        boolean ok = parser.parse(new SourceString(page));
        return ok? parser.semantics().getWiktionaryEntries() : new ArrayList<>();
    }

    private void processWiktionaryPage(WiktionaryPageDocument wiktionaryPage, Consumer<WiktionaryEntry> consumer) {
        if (wiktionaryPage.getNamespace() == null) {
            Collection<WiktionaryEntry> wiktionaryEntries = parseWiktionaryPage(wiktionaryPage.getText());
            wiktionaryEntries.forEach(consumer::accept);
        }
    }
}
