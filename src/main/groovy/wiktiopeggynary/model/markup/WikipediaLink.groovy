package wiktiopeggynary.model.markup

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.commons.lang3.StringUtils
import wiktiopeggynary.model.visitor.RichTextComponentVisitor

/**
 * @author Krzysztof Witukiewicz
 */
@ToString(includePackage = false, ignoreNulls = true)
@EqualsAndHashCode
class WikipediaLink implements RichTextComponent {

    String pageTitle
    String linkText

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
        return StringUtils.isBlank(pageTitle) && StringUtils.isBlank(linkText)
    }
}
