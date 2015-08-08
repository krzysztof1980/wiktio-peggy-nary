package wiktiopeggynary.parser;

import de.tudarmstadt.ukp.jwktl.parser.WiktionaryDumpParser;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.markup.Template;
import wiktiopeggynary.parser.dumpparser.WiktionaryAutocorrectionService;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageDocument;
import wiktiopeggynary.parser.dumpparser.WiktionaryPageParser;
import wiktiopeggynary.parser.mouse.SourceString;
import wiktiopeggynary.parser.util.PrintingTemplateConsumer;

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
        PrintingTemplateConsumer templateConsumer = new PrintingTemplateConsumer();
        WiktionaryDumpParser dumpParser = new WiktionaryDumpParser(
                new WiktionaryPageParser(p -> processWiktionaryPage(p, consumer, templateConsumer), cleanup));
        dumpParser.parse(wiktionaryDumpPath.toFile());
        templateConsumer.print();
    }

    public Collection<WiktionaryEntry> parseWiktionaryPage(String page) {
        PrintingTemplateConsumer templateConsumer = new PrintingTemplateConsumer();
        Collection<WiktionaryEntry> entries = parseWiktionaryPage(page, templateConsumer);
        templateConsumer.print();
        return entries;
    }

    private void processWiktionaryPage(WiktionaryPageDocument wiktionaryPage, Consumer<WiktionaryEntry> entryConsumer, Consumer<Collection<Template>> templatesConsumer) {
        if (wiktionaryPage.getNamespace() == null) {
            Collection<WiktionaryEntry> wiktionaryEntries = parseWiktionaryPage(wiktionaryPage.getText(), templatesConsumer);
            wiktionaryEntries.forEach(entryConsumer::accept);
        }
    }

    private Collection<WiktionaryEntry> parseWiktionaryPage(String page, Consumer<Collection<Template>> templatesConsumer) {
        WiktionaryParser parser = new WiktionaryParser();
        String trace = System.getProperty("wiktiopeggynary.trace");
        if (trace != null) {
            parser.setTrace(trace);
            try {
                parser.getClass().getMethod("setMemo", int.class).invoke(parser, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String correctedPage = WiktionaryAutocorrectionService.getInstance().correctPage(page);
        boolean ok = parser.parse(new SourceString(correctedPage));
        templatesConsumer.accept(parser.semantics().getTemplates());
        return ok ? parser.semantics().getWiktionaryEntries() : new ArrayList<>();
    }
}
