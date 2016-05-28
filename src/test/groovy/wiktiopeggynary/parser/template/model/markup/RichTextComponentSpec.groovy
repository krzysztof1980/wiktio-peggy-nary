package wiktiopeggynary.parser.template.model.markup

import spock.lang.Specification
import wiktiopeggynary.model.markup.PlainText
import wiktiopeggynary.model.markup.RichText


/**
 * @author Krzysztof Witukiewicz
 */
class RichTextComponentSpec extends Specification {

    def "merging multiple RichText objects"() {
        given:
        def richText1 = new RichText()
        def richText2 = new RichText(new PlainText("some text"))
        def richText3 = new RichText(new PlainText(" - some another text"))

        when:
        def mergeResult = richText1.mergeWith(richText2)

        then:
        mergeResult.isPresent()
        mergeResult.get().equals(richText2)

        when:
        mergeResult = mergeResult.get().mergeWith(richText3)

        then:
        mergeResult.isPresent()
        mergeResult.get().equals(new RichText(new PlainText("some text - some another text")))
    }
}