package wiktiopeggynary.parser.util;

import wiktiopeggynary.model.markup.Template;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class PrintingTemplateConsumer implements Consumer<Collection<Template>> {

    private final Set<String> templateSet = new TreeSet<>();

    @Override
    public void accept(Collection<Template> templates) {
        templateSet.addAll(templates.stream().map(Template::asText).collect(Collectors.toList()));
    }

    public void print() {
        templateSet.forEach(System.out::println);
    }
}
