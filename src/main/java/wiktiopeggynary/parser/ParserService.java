package wiktiopeggynary.parser;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryDumpParser;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.parser.dumpparser.WiktionaryAutocorrectionService;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageDocument;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageParser;
import wiktiopeggynary.parser.mouse.SourceString;

import java.lang.reflect.InvocationTargetException;
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
        String trace = System.getProperty("wiktiopeggynary.trace");
        if (trace != null) {
            parser.setTrace(trace);
            try {
                parser.getClass().getMethod("setMemo", int.class).invoke(parser, 0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        String correctedPage = WiktionaryAutocorrectionService.getInstance().correctPage(page);
        boolean ok = parser.parse(new SourceString(correctedPage));
        return ok? parser.semantics().getWiktionaryEntries() : new ArrayList<>();
    }

    private void processWiktionaryPage(WiktionaryPageDocument wiktionaryPage, Consumer<WiktionaryEntry> consumer) {
        if (wiktionaryPage.getNamespace() == null) {
            Collection<WiktionaryEntry> wiktionaryEntries = parseWiktionaryPage(wiktionaryPage.getText());
            wiktionaryEntries.forEach(consumer::accept);
        }
    }
}
