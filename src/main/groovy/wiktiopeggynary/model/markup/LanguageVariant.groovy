package wiktiopeggynary.model.markup

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import wiktiopeggynary.model.visitor.RichTextComponentVisitor

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class LanguageVariant implements RichTextComponent {

    final Variant variant
    String suffix

    LanguageVariant(@JsonProperty("variant") Variant variant) {
        this.variant = variant
    }

    @Override
    void accept(RichTextComponentVisitor visitor) {
        visitor.visit(this)
    }

    @Override
    Optional<? extends RichTextComponent> mergeWith(RichTextComponent component) {
        return Optional.empty()
    }

    @Override
    boolean isEmpty() {
        return false
    }

    enum Variant {

        British, American
    }
}
