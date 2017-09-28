package wiktiopeggynary.model.markup

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import wiktiopeggynary.model.visitor.RichTextComponentVisitor

import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class KTemplate implements RichTextComponent {

    List<Part> parts
    RichText suffix

    static KTemplate fromTemplate(List<TemplateParameter> params) {
        KTemplate meaningKontext = new KTemplate()
        meaningKontext.parts = params.stream()
                                     .filter { p -> p instanceof PositionalTemplateParameter }
                                     .map { p -> PositionalTemplateParameter.class.cast(p) }
                                     .map { p -> new Part(text: p.value) }
                                     .collect(Collectors.toList())
        params.stream()
              .filter { p -> p instanceof NamedTemplateParameter }
              .map { p -> NamedTemplateParameter.class.cast(p) }
              .forEach { p ->
            if (p.name.startsWith("t")) {
                def pos = Integer.valueOf(String.valueOf(p.name.charAt(1)))
                meaningKontext.parts[pos - 1].separator = p.value
            } else if (p.name == "ft") {
                meaningKontext.suffix = p.value
            }
        }
        // set default value for separator in all but the last Part, if null
        IntStream.range(0, meaningKontext.parts.size() - 1)
                 .forEach { i ->
            def part = meaningKontext.parts[i]
            if (part.separator == null)
                part.separator = new RichText(",")
        }
        // set default value for suffix, if null
        if (meaningKontext.suffix == null) {
            meaningKontext.suffix = new RichText(":")
        }

        return meaningKontext
    }

    @Override
    void accept(RichTextComponentVisitor visitor) {
        visitor.visit(this)
        parts.each { p ->
            p.text.accept(visitor)
            if (p.separator != null)
                p.separator.accept(visitor)
        }
        if (suffix != null)
            suffix.accept(visitor)
    }

    @Override
    Optional<? extends RichTextComponent> mergeWith(RichTextComponent component) {
        return Optional.empty()
    }

    @Override
    boolean isEmpty() {
        return false
    }

    @ToString(includePackage = false, ignoreNulls = true)
    @EqualsAndHashCode
    static class Part {

        Part() {
        }

        Part(RichText text, RichText separator) {
            this.text = text
            this.separator = separator
        }

        RichText text
        RichText separator
    }
}
